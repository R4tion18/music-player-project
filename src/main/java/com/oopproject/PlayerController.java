package com.oopproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
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
    @FXML Label volumeLabel;
    @FXML Slider timeSlider;
    @FXML Slider volumeSlider;


    private Media media;
    private MediaPlayer mediaPlayer;
    private SongQueue songs;
    private boolean isPlaying = false;
    private  boolean isLooping = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Testing code start
        songs = new SongQueue(new File("HollowKnightTestPlaylist\\"));
        //Testing code stop
        volumeSlider.setMax(1.0);
        volumeSlider.setValue(0.5);
        volumeLabel.setText("50%");
        loadSong(songs.getSong());
    }
    @FXML
    void nextAction() {
        pauseAction();
        loadSong(songs.getNextSong());
        startAction();
    }
    @FXML
    void previousAction() {
        if (mediaPlayer.getCurrentTime().toSeconds() < 2.5){
            pauseAction();
            loadSong(songs.getPreviousSong());
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
        songs.setRandom();
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
        isLooping = false;
        mediaPlayer.setOnEndOfMedia(this::nextAction);
        songLabel.setText(songs.getName());
    }

    private void startAction() {
        mediaPlayer.play();
        isPlaying = true;
    }

    private void pauseAction() {
        mediaPlayer.pause();
        isPlaying = false;
    }

    public String timeFormatting(Duration time) {
        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();

        if (seconds > 59) {
            seconds %= 60;
        }
        if (minutes > 59) {
            minutes %= 60;
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
    }

    @FXML
    void debugAction() {
        System.out.println(songs.getSongSequence());
        System.out.println(songs.getSongNumber());
        System.out.println(songs.getSongSequence().get(songs.getSongNumber()));
    }
}
