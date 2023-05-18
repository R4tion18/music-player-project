package com.oopproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class PlayerController implements Initializable {



    @FXML Label songLabel;
    private File directory;
    private File[] files;
    private ArrayList<File> songs;
    private Media media;
    private MediaPlayer mediaPlayer;
    private int songNumber;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Testing code Start
        songs = new ArrayList<File>();
        directory = new File("C:\\Users\\rikiv\\OneDrive\\Desktop\\MediaMusic");
        files = directory.listFiles();
        if (files != null){
            Collections.addAll(songs, files);
        }
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(1);
        mediaPlayer.setOnEndOfMedia(this::nextAction);
        songLabel.setText(songs.get(songNumber).getName());
        //Testing code End
    }
    @FXML void nextAction() {
        if(songNumber == songs.size() - 1){
            songNumber = 0;
        }else{
            songNumber++;
        }
        stopAction();
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(songs.get(songNumber).getName());
        mediaPlayer.setOnEndOfMedia(this::nextAction);
        startAction();
    }

    @FXML void previousAction() {
        if(songNumber == 0){
            songNumber = songs.size() - 1;
        }else{
            songNumber--;
        }
        stopAction();
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(songs.get(songNumber).getName());
        mediaPlayer.setOnEndOfMedia(this::nextAction);
        startAction();
    }

    @FXML void replayAction() {
        mediaPlayer.seek(Duration.seconds(0.0));
    }

    @FXML void startStopAction() {
        if(mediaPlayer.getStatus().toString().equals("STALLED")) {
            stopAction();
        } else {
            if (!mediaPlayer.getStatus().toString().equals("PLAYING")) {
                startAction();
            } else {
                stopAction();
            }
        }
    }
    private void startAction(){
        mediaPlayer.play();
    }
    private void stopAction(){
        mediaPlayer.pause();
    }
    @FXML void infoAction() {
        System.out.println(mediaPlayer.getStatus());
        System.out.println(media.getDuration());
        System.out.println(mediaPlayer.getCurrentTime());
    }

    @FXML void debugAction(){
    }
}
