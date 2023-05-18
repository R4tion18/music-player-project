package com.oopproject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.*;
import java.io.File;
import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class PlayerController implements Initializable {



    @FXML Label songLabel;
    @FXML Label currentTimeLabel;
    @FXML Label totalTimeLabel;
    @FXML Slider timeSlider;
    @FXML Slider volumeSlider;

    private File directory;
    private File[] files;
    private ArrayList<File> songs;
    private Media media;
    private MediaPlayer mediaPlayer;
    private int songNumber;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Testing code Start
        songs = new ArrayList<>();
        directory = new File("C:\\Users\\rikiv\\OneDrive\\Desktop\\MediaMusic");
        files = directory.listFiles();
        if (files != null){
            Collections.addAll(songs, files);
        }
        volumeSlider.setMax(1.0);
        volumeSlider.setValue(0.5);
        mediaLoader();
        //Testing code End
    }
    private void mediaLoader(){
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.volumeProperty().bindBidirectional(volumeSlider.valueProperty());
        timeSlider.setValue(0.0);
        currentTimeLabel.setText("0");


        mediaPlayer.totalDurationProperty().addListener((observable, oldDuration, newDuration) -> {
            timeSlider.setMax(newDuration.toSeconds());
            totalTimeLabel.setText(getTime(newDuration));
        });
        timeSlider.valueChangingProperty().addListener((observable, wasChanging, isChanging) -> {
            if(!isChanging){
                mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                currentTimeLabel.setText(getTime(mediaPlayer.getCurrentTime()));
            }
        });
        timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double currentTime = mediaPlayer.getCurrentTime().toSeconds();
            if (Math.abs(currentTime - newValue.doubleValue()) > 0.5){
                mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                currentTimeLabel.setText(getTime(Duration.seconds(newValue.doubleValue())));
            }
        });
        mediaPlayer.currentTimeProperty().addListener((observable, oldDuration, newDuration) -> {
            if (!timeSlider.isValueChanging()){
                timeSlider.setValue(newDuration.toSeconds());
                currentTimeLabel.setText(getTime(newDuration));
            }
        });
        mediaPlayer.setOnEndOfMedia(this::nextAction);
        songLabel.setText(songs.get(songNumber).getName());

    }
    @FXML void nextAction() {
        if(songNumber == songs.size() - 1){
            songNumber = 0;
        }else{
            songNumber++;
        }
        stopAction();
        mediaLoader();
        startAction();
    }

    @FXML void previousAction() {
        if(songNumber == 0){
            songNumber = songs.size() - 1;
        }else{
            songNumber--;
        }
        stopAction();
        mediaLoader();
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
    public String getTime(Duration time){
        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();

        if (seconds > 59){
            seconds = seconds % 60;
        }
        if(minutes > 59){
            minutes = minutes % 60;
        }
        if(minutes > 0){
            return String.format("%02d:%02d", minutes, seconds);
        } else {
            return String.format("%02d", seconds);
        }
    }

   //Debug methods
    @FXML void infoAction() {
        System.out.println(mediaPlayer.getStatus());
        System.out.println(media.getDuration());
        System.out.println(mediaPlayer.getCurrentTime());
        System.out.println(mediaPlayer.getStopTime());

    }

    @FXML void debugAction(){
    }
}
