package com.oopproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.*;
import java.io.File;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class PlayerController implements Initializable {

    @FXML Label songLabel;
    @FXML Label currentTimeLabel;
    @FXML Label totalTimeLabel;
    @FXML Label volumeLabel;
    @FXML Slider timeSlider;
    @FXML Slider volumeSlider;

    private File directory;
    private File[] files;
    private ArrayList<File> songs;
    private Media media;
    private MediaPlayer mediaPlayer;
    private int songNumber;
    private ArrayList<Integer> songSequence;
    private boolean isPlaying = false;
    private  boolean isLooping = false;
    private boolean isRandom = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Testing code Start
        songs = new ArrayList<>();
        directory = new File("C:\\Users\\rikiv\\OneDrive\\Desktop\\MediaMusic");
        files = directory.listFiles();
        if (files != null) {
            songs.addAll(Arrays.asList(files));
        }
        //Testing code End

        volumeSlider.setMax(1.0);
        volumeSlider.setValue(0.5);
        volumeLabel.setText("50%");
        loadSong(songs.get(songNumber).toURI().toString());
    }

    private void loadSong(String songName) {
        media = new Media(songName);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.volumeProperty().bindBidirectional(volumeSlider.valueProperty());

        volumeSlider.valueChangingProperty().addListener((observable, wasChanging, isChanging) ->
                volumeLabel.setText(String.format("%d", (int) (volumeSlider.getValue() * 100)) + "%"));

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) ->
                volumeLabel.setText(String.format("%d", (int) (newValue.doubleValue() * 100)) + "%"));

        timeSlider.setValue(0.0);
        currentTimeLabel.setText("0");
        mediaPlayer.totalDurationProperty().addListener((observable, oldDuration, newDuration) -> {
            timeSlider.setMax(newDuration.toSeconds());
            totalTimeLabel.setText(timeFormatting(newDuration));
        });
        timeSlider.valueChangingProperty().addListener((observable, wasChanging, isChanging) -> {
            mediaPlayer.pause();
            if (!isChanging) {
                mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                currentTimeLabel.setText(timeFormatting(mediaPlayer.getCurrentTime()));
                if (isPlaying) {
                    mediaPlayer.play();
                }
            }
        });
        timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double currentTime = mediaPlayer.getCurrentTime().toSeconds();
            if (Math.abs(currentTime - newValue.doubleValue()) > 0.5) {
                mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                currentTimeLabel.setText(timeFormatting(Duration.seconds(newValue.doubleValue())));
            }
        });
        mediaPlayer.currentTimeProperty().addListener((observable, oldDuration, newDuration) -> {
            if (!timeSlider.isValueChanging()) {
                timeSlider.setValue(newDuration.toSeconds());
                currentTimeLabel.setText(timeFormatting(newDuration));
            }
        });
        mediaPlayer.setOnEndOfMedia(this::nextAction);
        songLabel.setText(songs.get(songNumber).getName());
    }

    @FXML
    void nextAction() {
        if (nextSongIndex(songNumber) == songs.size() - 1) {
            songNumber = 0;
        } else {
            songNumber++;
        }
        pauseAction();
        loadSong(songs.get(nextSongIndex(songNumber)).toURI().toString());
        startAction();
    }

    @FXML
    void previousAction() {
        if (mediaPlayer.getCurrentTime().toSeconds() < 2.5){
            if (nextSongIndex(songNumber) == 0) {
                songNumber = songs.size() - 1;
            } else {
                songNumber--;
            }
            pauseAction();
            loadSong(songs.get(nextSongIndex(songNumber)).toURI().toString());
            startAction();
        } else {
            mediaPlayer.seek(Duration.seconds(0.0));
        }
    }

    @FXML
    void loopAction() {
        if(!isLooping) {
            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.seconds(0.0)));
            isLooping = true;
        }else{
            mediaPlayer.setOnEndOfMedia(this::nextAction);
            isLooping = false;
        }
    }
    @FXML
    void randomAction(){
        if(!isRandom) {
            songSequence = new ArrayList<>(songs.size());
            IntStream.
                    range(0, songs.size()).
                    forEach(i -> songSequence.add(i));
            Collections.shuffle(songSequence);
            isRandom = true;
        }else{
            isRandom = false;
        }
    }

    @FXML
    void volumeAction() {

    }

    @FXML
    void startStopAction() {
        if (mediaPlayer.getStatus().toString().equals("STALLED")) {
            pauseAction();
        } else {
            if (!mediaPlayer.getStatus().toString().equals("PLAYING")) {
                startAction();
            } else {
                pauseAction();
            }
        }
    }

    private void startAction() {
        mediaPlayer.play();
        isPlaying = true;
    }

    private void pauseAction() {
        mediaPlayer.pause();
        isPlaying = false;
    }

    private int nextSongIndex(int songRealIndex){
        if(!isRandom){
            return songRealIndex;
        }else{
            return songSequence.get(songRealIndex);
        }
    }

    public String timeFormatting(Duration time) {
        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();

        if (seconds > 59) {
            seconds = seconds % 60;
        }
        if (minutes > 59) {
            minutes = minutes % 60;
        }
        if (minutes > 0) {
            return String.format("%02d:%02d", minutes, seconds);
        } else {
            return String.format("%02d", seconds);
        }
    }

    //Debug methods
    @FXML
    void infoAction() {
        System.out.println(mediaPlayer.getStatus());
        System.out.println(media.getDuration());
        System.out.println(mediaPlayer.getCurrentTime());
        System.out.println(mediaPlayer.getStopTime());
    }

    @FXML
    void debugAction() {
    }
}
