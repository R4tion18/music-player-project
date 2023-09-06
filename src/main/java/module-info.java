module com.oopproject {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires mp3agic;

    opens com.oopproject to javafx.fxml;
    exports com.oopproject;
}