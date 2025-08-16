module com.github.dzmiv {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.annotation;
	requires java.desktop;

    opens com.github.dzmiv to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.github.dzmiv;
}