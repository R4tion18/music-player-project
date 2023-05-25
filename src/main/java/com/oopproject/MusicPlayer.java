package com.oopproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MusicPlayer extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MusicPlayer.class.getResource("ActualPlayer.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("MediaPlayer");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/AppIcon2.png"))));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}