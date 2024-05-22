module com.example.javafx.optimize_method {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;

    exports com.example.javafx.optimize_method;
    opens com.example.javafx.optimize_method to javafx.fxml;

    exports com.example.javafx.optimize_method.controller;
    opens com.example.javafx.optimize_method.controller to javafx.controls, javafx.fxml;

}