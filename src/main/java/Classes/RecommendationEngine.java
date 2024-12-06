package Classes;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.*;
import org.apache.commons.math3.linear.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class RecommendationEngine {

    private final DatabaseHandler dbHandler;
    private final StanfordCoreNLP pipeline;
    private final ExecutorService executorService;

    public RecommendationEngine() {
        this.dbHandler = new DatabaseHandler();

        // Set up Stanford NLP pipeline
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        this.pipeline = new StanfordCoreNLP(props);

        // Set up thread pool for concurrent tasks
        this.executorService = Executors.newFixedThreadPool(10); // Adjust the pool size as needed
    }

    /**
     * Recommends articles to a user based on their reading activity and likes.
     *
     * @param userID The ID of the user.
     * @return A list of recommended articles.
     */
    public List<Article> recommendArticles(int userID) {
        List<Article> userReadArticles = dbHandler.fetchArticlesReadByUser(userID);

        // Handle new users with no reading history
        if (userReadArticles.isEmpty()) {
            System.out.println("New user detected. No recommendations available.");
            return Collections.emptyList(); // Return an empty list
        }

        List<Article> allArticles = dbHandler.fetchAllArticles();
        allArticles.removeAll(userReadArticles); // Exclude articles the user has already read

        // Use TF-IDF and cosine similarity for recommendations
        Map<Article, Double> similarityScores = calculateSimilarities(userReadArticles, allArticles);

        // Define a similarity threshold (e.g., 0.3 for articles with 30% similarity or more)
        double similarityThreshold = 0.3;

        return similarityScores.entrySet().stream()
                .filter(entry -> entry.getValue() >= similarityThreshold) // Filter by similarity threshold
                .sorted(Map.Entry.<Article, Double>comparingByValue().reversed()) // Sort by similarity
                .limit(10) // Top 10 recommendations
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    /**
     * Calculates similarity scores between a user's read articles and all other articles.
     *
     * @param userReadArticles List of articles read by the user.
     * @param allArticles      List of all available articles.
     * @return A map of articles to similarity scores.
     */
    private Map<Article, Double> calculateSimilarities(List<Article> userReadArticles, List<Article> allArticles) {
        String userProfileContent = userReadArticles.stream()
                .map(Article::getContent)
                .collect(Collectors.joining(" "));

        Map<String, Double> userProfileVector = tokenizeAndVectorize(userProfileContent);

        // Parallelize similarity calculations using parallelStream
        return allArticles.parallelStream()
                .collect(Collectors.toConcurrentMap(
                        article -> article,
                        article -> {
                            Map<String, Double> articleVector = tokenizeAndVectorize(article.getContent());
                            return computeCosineSimilarity(userProfileVector, articleVector);
                        }
                ));
    }

    /**
     * Tokenizes and vectorizes content using Stanford CoreNLP and TF-IDF.
     *
     * @param content The article content to process.
     * @return A vector representation of the content.
     */
    private Map<String, Double> tokenizeAndVectorize(String content) {
        Map<String, Integer> termFrequencies = new ConcurrentHashMap<>();
        Annotation document = new Annotation(content);
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        sentences.parallelStream().forEach(sentence -> {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                termFrequencies.merge(lemma, 1, Integer::sum);
            }
        });

        System.out.println("Term Frequencies: " + termFrequencies);

        Map<String, Double> tfIdfVector = new ConcurrentHashMap<>();
        int totalTerms = termFrequencies.values().stream().reduce(0, Integer::sum);

        termFrequencies.keySet().parallelStream().forEach(term -> {
            double tf = (double) termFrequencies.get(term) / totalTerms;
            double idf = Math.log(1 + (1.0 / (1 + dbHandler.getArticleCountContainingTerm(term))));
            tfIdfVector.put(term, tf * idf);
        });

        System.out.println("TF-IDF Vector: " + tfIdfVector);
        return tfIdfVector;
    }

    /**
     * Computes cosine similarity between two vectors.
     *
     * @param vectorA The first vector.
     * @param vectorB The second vector.
     * @return The cosine similarity.
     */
    private double computeCosineSimilarity(Map<String, Double> vectorA, Map<String, Double> vectorB) {
        double normA = Math.sqrt(vectorA.values().stream().mapToDouble(val -> val * val).sum());
        double normB = Math.sqrt(vectorB.values().stream().mapToDouble(val -> val * val).sum());

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        Set<String> allTerms = new HashSet<>(vectorA.keySet());
        allTerms.addAll(vectorB.keySet());

        RealVector vecA = new ArrayRealVector(allTerms.size());
        RealVector vecB = new ArrayRealVector(allTerms.size());

        int index = 0;
        for (String term : allTerms) {
            vecA.setEntry(index, vectorA.getOrDefault(term, 0.0));
            vecB.setEntry(index, vectorB.getOrDefault(term, 0.0));
            index++;
        }

        return vecA.cosine(vecB);
    }

    /**
     * Shutdown the executor service to release resources.
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
