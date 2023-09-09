package com.oopproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.util.Duration;

public class MusicPlayerOverviewController implements Initializable {

    @FXML ListView<String> songListView;
    @FXML ListView<String> playlistListView;
    @FXML ListView<String> albumListView;
    @FXML Label songLabel;
    @FXML Label currentTimeLabel;
    @FXML Label totalTimeLabel;
    @FXML Label volumeLabel;
    @FXML Label titleLabel;
    @FXML Label artistLabel;
    @FXML Label albumLabel;
    @FXML Label albumArtistLabel;
    @FXML Label yearLabel;
    @FXML Slider timeSlider;
    @FXML Slider volumeSlider;
    @FXML Button playPauseButton;
    @FXML Button nextButton;
    @FXML Button previousButton;
    @FXML Button queueButton;

    QueueViewController controller;
    private Media media;
    private MediaPlayer mediaPlayer;
    private SongQueue songs = new SongQueue();
    private boolean isPlaying = false;
    private  boolean isLooping = false;
    public Library library;
    public Properties playerProperties;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        volumeSlider.setMax(1.0);
        volumeSlider.setValue(0.5);
        volumeLabel.setText("50%");
        //loadSong(songs.getSong());
        setImage(playPauseButton, "icons/playIcon1.png");
        setImage(nextButton, "icons/nextIcon.png");
        setImage(previousButton, "icons/previousIcon.png");
        setImage(queueButton, "icons/queueIcon.png");

        Properties defaultProperties = new Properties();
        try {
            defaultProperties.load(MusicPlayerOverviewController.class.getResourceAsStream("default.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        playerProperties = new Properties(defaultProperties);
        try {
            playerProperties.load(MusicPlayerOverviewController.class.getResourceAsStream("player.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (playerProperties.getProperty("libraryFolder", "").isEmpty())   {
            new Alert(Alert.AlertType.INFORMATION, "No directory has been selected. Open a directory on your computer to setup the player. ").showAndWait();
        }   else {
            loadLibrary(false);
        }
    }

    private void loadLibrary(boolean isFirstSetup)  {
        library = new Library(playerProperties, isFirstSetup);
        //songs = new SongQueue(new File("/Users/Francesco/IdeaProjects/music-player-project/src/test/resources/test-songs"));
        //IntStream.range(0, songs.getSongSequence().size()).forEach(i -> allSavedSong.add(songs.getSongNames().get((i + songs.getSongNumber() + 1) % songs.getSongSequence().size())));
        //Testing code stop
        songListView.setItems(library.getSongTitles());
        playlistListView.setItems(library.getPlaylistNames());
        albumListView.setItems(library.getAlbumNames());
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
        currentTimeLabel.setText("0:00");
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
        titleLabel.setText(Song.getTitle(songName));
        artistLabel.setText(Song.getArtist(songName));
        albumLabel.setText(Song.getAlbum(songName));
        albumArtistLabel.setText(Song.getAlbumArtist(songName));
        yearLabel.setText(String.valueOf(Song.getYear(songName)));
    }
    @FXML
    void nextAction() {
        pauseAction();
        loadSong(songs.getNextSong());
        playAction();
        if(controller != null){
            controller.next();
        }
    }
    @FXML
    void previousAction() {
        if (mediaPlayer.getCurrentTime().toSeconds() < 2.5){
            pauseAction();
            loadSong(songs.getPreviousSong());
            playAction();
            if(controller != null){
                controller.previous();
            }
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
    void shuffleAction(){
        songs.setShuffle();
        if(controller != null){
            controller.shuffle();
        }
    }
    @FXML
    void playPauseAction() {
        if (mediaPlayer == null)    {
            if (songs.getSongNames().isEmpty())   {
                return;
            }
            loadSong(songs.getSong());
        }
        if (mediaPlayer.getStatus().toString().equals("STALLED")) {
            pauseAction();
        } else {
            if (!mediaPlayer.getStatus().toString().equals("PLAYING")) {
                playAction();
            } else {
                pauseAction();
            }
        }
    }

    @FXML
    void menuAction(){

    }
    @FXML
    void queueAction(){
        if(controller == null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("queue-view.fxml"));
                DialogPane view = loader.load();
                controller = loader.getController();

                controller.setSongs(songs);

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Songs Queue");
                dialog.initModality(Modality.WINDOW_MODAL);
                dialog.setDialogPane(view);

                dialog.showAndWait();
                controller = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    void playSongAction(){
        if (songListView.getSelectionModel().getSelectedIndex() >= 0)   {
            mediaPlayer.dispose();
            songs = new SongQueue();
            addToQueueSAction();
            loadSong(songs.getSong());
            playAction();
        }
    }
    @FXML
    void playPlaylistAction(){
        if (playlistListView.getSelectionModel().getSelectedIndex() >= 0)   {
            mediaPlayer.dispose();
            songs = new SongQueue();
            addToQueuePAction();
            loadSong(songs.getSong());
            playAction();
        }
    }
    @FXML
    void playAlbumAction(){
        if (albumListView.getSelectionModel().getSelectedIndex() >= 0)   {
            mediaPlayer.dispose();
            songs = new SongQueue();
            addToQueueAAction();
            loadSong(songs.getSong());
            playAction();
        }
    }
    @FXML
    void addToQueueSAction(){
        if (songListView.getSelectionModel().getSelectedIndex() >= 0)   {
            songs.addSong(library.getSong(library.getIndex(songListView.getSelectionModel().getSelectedItem())));
        }
    }

    @FXML
    void addToQueueAAction()    {
        if (albumListView.getSelectionModel().getSelectedIndex() >= 0)   {
            songs.addSongs(new ArrayList<>(library.getAlbum(albumListView.getSelectionModel().getSelectedItem()).getSongURIs()));
        }
    }

    @FXML
    void addToQueuePAction()  {
        if (playlistListView.getSelectionModel().getSelectedIndex() >= 0)   {
            songs.addSongs(new ArrayList<>(library.getPlaylist(playlistListView.getSelectionModel().getSelectedItem()).getSongURIs()));
        }
    }

    /*@FXML
    void addToQueueMAction(){

    }*/

    private void playAction() {
        mediaPlayer.play();
        isPlaying = true;
        setImage(playPauseButton, "icons/pauseIcon.png");

    }

    private void pauseAction() {
        mediaPlayer.pause();
        isPlaying = false;
        setImage(playPauseButton, "icons/playIcon1.png");
    }

    @FXML void handleOpen(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File startingDirectory = directoryChooser.showDialog(null);
        if (startingDirectory != null){
            playerProperties.setProperty("libraryFolder", startingDirectory.toURI().toString());
            loadLibrary(true);
        }else{
            new Alert(Alert.AlertType.ERROR, "Could not load directory").showAndWait();
        }
    }

    @FXML
    private void handleAddSong(){
        //If the folder is not selected, select the folder first.
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.mp3"));
        File newSong = fileChooser.showOpenDialog(null);

        if (newSong != null){
            library.addSongFile(newSong.toURI().toString());
        }else{
            new Alert(Alert.AlertType.ERROR, "Could not load that file.").showAndWait();
        }
        songListView.setItems(library.getSongTitles());
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Music Mp3 Player");
        alert.setHeaderText("About");
        alert.setContentText("Author: 301114@studenti.unimore.it & 302418@studenti.unimore.it");
        alert.showAndWait();
    }
    @FXML
    private void handleClose() {
        try (FileOutputStream properties = new FileOutputStream("src/main/resources/com/oopproject/player.properties")) {
            playerProperties.store(properties, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
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
            if(minutes > 9){
                return String.format("%02d:%02d", minutes, seconds);
            }
            return String.format("%01d:%02d", minutes, seconds);
        } else {
            return String.format("0:%02d", seconds);
        }
    }

    private void setImage(Button button, String uri){
        ImageView thisImageView =
                new ImageView( new Image(Objects.requireNonNull(getClass().getResourceAsStream(uri))));
        thisImageView.setFitHeight(40);
        thisImageView.setFitWidth(50.0);
        button.setGraphic(thisImageView);
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
