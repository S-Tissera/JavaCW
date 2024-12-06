module com.example.cm2601cwfinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires edu.stanford.nlp.corenlp_english_models;
    requires edu.stanford.nlp.corenlp;
    requires commons.math3;
    requires com.zaxxer.hikari;

    opens com.example.Controllers to javafx.fxml;
    exports com.example.Controllers;
}
