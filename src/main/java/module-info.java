module com.oopproject {
    requires java.desktop;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires org.controlsfx.controls;

    opens com.oopproject to javafx.fxml;
    exports com.oopproject;
}