package com.oopproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * A player to play songs and organise them in playlists and albums.
 */
public class MusicPlayer extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MusicPlayer.class.getResource("musicPlayer-overview-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("MediaPlayer");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/AppIcon2.png"))));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}