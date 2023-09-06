package com.oopproject;

import javafx.fxml.Initializable;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
/*
public class PlayerController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Properties defaultProps = new Properties();
        boolean isFirstSetup = false;

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

        if (playerProperties.getProperty("libraryFolder", "").isEmpty())   {
            //ask for directory;
            //playerProperties.setProperty("libraryFolder", directory);
            isFirstSetup = true;
        }

        Library library = new Library(playerProperties, isFirstSetup);
    }
}*/