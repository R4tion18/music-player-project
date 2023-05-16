package com.oopproject;

import javafx.fxml.Initializable;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class PlayerController implements Initializable {

    private File directory;
    private File[] files;
    private ArrayList<File> songs;
    private Media media;
    private MediaPlayer mediaPlayer;
    private int songNumber;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Properties defaultProps = new Properties();
        try {
            defaultProps.load(PlayerController.class.getResourceAsStream("default.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Properties playerProperties = new Properties(defaultProps);
        try {
            playerProperties.load(PlayerController.class.getResourceAsStream("library.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}