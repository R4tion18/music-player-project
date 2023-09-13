package com.oopproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.io.File;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

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
    @FXML ImageView coverImageView;
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
    @FXML ToggleButton loopButton;
    @FXML ToggleButton shuffleButton;

    QueueViewController controller;
    PlaylistViewController playlistViewController;
    AlbumViewController albumViewController;

    private MediaPlayer mediaPlayer;
    private SongQueue songs = new SongQueue();
    private boolean isPlaying = false;
    private  boolean isLooping = false;
    private boolean isShuffled = false;
    public Library library;
    public Properties playerProperties;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        volumeSlider.setMax(1.0);
        volumeSlider.setValue(0.5);
        volumeLabel.setText("50%");
        setImage(playPauseButton, "icons/playIcon1.png");
        setImage(nextButton, "icons/nextIcon.png");
        setImage(previousButton, "icons/previousIcon.png");
        setImage(queueButton, "icons/queueIcon.png");
        setImage(loopButton, "icons/loopIcon.png");
        setImage(shuffleButton, "icons/shuffleIcon.png");

        Properties defaultProperties = new Properties();
        try {
            defaultProperties.load(getClass().getResourceAsStream("default.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        playerProperties = new Properties(defaultProperties);
        try {
            playerProperties.load(getClass().getResourceAsStream("player.properties"));
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
        songListView.setItems(library.getSongTitles());
        playlistListView.setItems(library.getPlaylistNames());
        albumListView.setItems(library.getAlbumNames());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void loadSong(String songName) {
        mediaPlayer = new MediaPlayer(new Media(songName));
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
        if (Song.getTag(songName).get().getAlbumImage() == null)    {
            coverImageView.setImage(( new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons" +
                    "/musicalNoteIcon.png")))));
        }   else {
            coverImageView.setImage(( new Image(new ByteArrayInputStream(Song.getTag(songName).get().getAlbumImage()))));
        }
        songLabel.setText(Song.getTitle(songName));
        titleLabel.setText(Song.getTitle(songName));
        artistLabel.setText(Song.getArtist(songName));
        albumLabel.setText(Song.getAlbum(songName));
        albumArtistLabel.setText(Song.getAlbumArtist(songName));
        yearLabel.setText(String.valueOf(Song.getYear(songName)));
    }

    public String textInput(String currentDefinition, String currentField, String newDefinition, String title) throws IllegalAccessException {
        String text = "";
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("text-input-view.fxml"));
            DialogPane view = loader.load();
            TextInputViewController inputController = loader.getController();

            inputController.setCurrentDefinition(currentDefinition);
            inputController.setCurrentField(currentField);
            inputController.setNewDefinition(newDefinition);

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle(title);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            inputController.setDialog(dialog);
            text = inputController.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
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
            setImage(loopButton, "icons/loopPressedIcon.png");
            isLooping = true;
        }else{
            mediaPlayer.setOnEndOfMedia(this::nextAction);
            setImage(loopButton, "icons/loopIcon.png");
            isLooping = false;
        }
    }
    @FXML
    void shuffleAction(){
        if(!isShuffled){
            setImage(shuffleButton, "icons/shufflePressedIcon.png");
            isShuffled = true;
        }else{
            setImage(shuffleButton, "icons/shuffleIcon.png");
            isShuffled = false;
        }
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
            if (mediaPlayer != null)    {
                mediaPlayer.dispose();
            }
            songs = new SongQueue();
            addToQueueSAction();
            loadSong(songs.getSong());
            playAction();
        }
    }
    @FXML
    void playPlaylistAction(){
        if (playlistListView.getSelectionModel().getSelectedIndex() >= 0)   {
            if (mediaPlayer != null)    {
                mediaPlayer.dispose();
            }
            songs = new SongQueue();
            addToQueuePAction();
            loadSong(songs.getSong());
            playAction();
        }
    }
    @FXML
    void playAlbumAction(){
        if (albumListView.getSelectionModel().getSelectedIndex() >= 0)   {
            if (mediaPlayer != null)    {
                mediaPlayer.dispose();
            }
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
            if(controller != null){
                controller.setSongs(songs);
            }
        }
    }

    @FXML
    void addToQueueAAction()    {
        if (albumListView.getSelectionModel().getSelectedIndex() >= 0)   {
            songs.addSongs(new ArrayList<>(library.getAlbum(albumListView.getSelectionModel().getSelectedItem()).getSongURIs()));
            if(controller != null){
                controller.setSongs(songs);
            }
        }
    }

    @FXML
    void addToQueuePAction()  {
        if (playlistListView.getSelectionModel().getSelectedIndex() >= 0)   {
            songs.addSongs(new ArrayList<>(library.getPlaylist(playlistListView.getSelectionModel().getSelectedItem()).getSongURIs()));
            if(controller != null){
                controller.setSongs(songs);
            }
        }
    }

    @FXML
    void removeAction() {
        if (songListView.getSelectionModel().getSelectedIndex() >= 0)   {
            library.deleteSong(library.getIndex(songListView.getSelectionModel().getSelectedItem()));
        }
        songListView.setItems(library.getSongTitles());
    }

    @FXML
    void showPlaylistAction()   {
        if (playlistListView.getSelectionModel().getSelectedIndex() >= 0)   {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("playlist-view.fxml"));
                DialogPane view = loader.load();
                playlistViewController = loader.getController();

                playlistViewController.setSongs(this, playlistListView.getSelectionModel().getSelectedItem());

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("View playlist");
                dialog.initModality(Modality.WINDOW_MODAL);
                dialog.setDialogPane(view);

                dialog.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void showAlbumAction()  {
        albumViewController = null;
        if (albumListView.getSelectionModel().getSelectedIndex() >= 0)   {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("album-view.fxml"));
                DialogPane view = loader.load();
                albumViewController = loader.getController();

                albumViewController.setSongs(this, albumListView.getSelectionModel().getSelectedItem());

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("View album");
                dialog.initModality(Modality.WINDOW_MODAL);
                dialog.setDialogPane(view);

                dialog.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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

        if (newSong != null && !playerProperties.getProperty("libraryFolder").isEmpty())    {
            library.addSongFile(newSong.toURI().toString());
        }   else {
            new Alert(Alert.AlertType.ERROR, "Could not load that file.").showAndWait();
        }
        songListView.setItems(library.getSongTitles());
    }

    @FXML
    private void handleCreatePlaylist() {
        String playlistName;
        try {
            playlistName = textInput("", "Playlist name: ", "", "Create playlist");
        } catch (IllegalAccessException e) {
            playlistName = "";
        }

        if (!playlistName.isEmpty()) {
            while (library.getPlaylistNames().contains(playlistName))   {
                playlistName += " ";
            }
            library.createPlaylist(playlistName);
        }

        playlistListView.setItems(library.getPlaylistNames());
    }

    @FXML
    private void handleCreateAlbum()    {
        String albumTitle;
        String albumArtist;
        String albumYear;

        try {
            albumTitle = textInput("", "Album title: ", "", "Create album");
        } catch (IllegalAccessException e) {
            return;
        }

        try {
            albumArtist = textInput("", "Album author: ", "", "Create album");
        } catch (IllegalAccessException e) {
            return;
        }

        try {
            albumYear = textInput("", "Album year: ", "", "Create album");
        } catch (IllegalAccessException e) {
            return;
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File albumDirectory = directoryChooser.showDialog(null);
        if (albumDirectory != null && !albumTitle.isEmpty() && !albumArtist.isEmpty() && !albumYear.isEmpty()) {
            while (library.getAlbumNames().contains(albumTitle))    {
                albumTitle += " ";
            }
            library.createAlbum(albumTitle, albumArtist, albumDirectory);
        }

        albumListView.setItems(library.getAlbumNames());
        songListView.setItems(library.getSongTitles());
    }

    @FXML
    private void  handleEditSong()    {
        if (songListView.getSelectionModel().getSelectedIndex() >= 0)   {
            Song song = new Song(Song.getFile(library.getSong(library.getIndex(songListView.getSelectionModel().getSelectedItem()))));
            CopyOnWriteArrayList<String> names = new CopyOnWriteArrayList<>(new String[]{"title", "album", "artist", "album artist", "year"});
            CopyOnWriteArrayList<String> current = new CopyOnWriteArrayList<>(new String[]{
                    song.getTitle(),
                    song.getAlbum(),
                    song.getArtist(),
                    song.getAlbumArtist(),
                    String.valueOf(song.getYear())
            });
            CopyOnWriteArrayList<String> changed = new CopyOnWriteArrayList<>();

            for (String name : names) {
                try {
                    changed.add(textInput("current " + name, current.get(names.indexOf(name)), "new " + name, "Edit " + name));
                } catch (IllegalAccessException e) {
                    return;
                }
            }

            if (!changed.get(0).isEmpty() && !changed.get(0).equals(names.get(0)))  {
                song.setTitle(changed.get(0));
            }
            if (!changed.get(1).isEmpty() && !changed.get(1).equals(names.get(1)))  {
                song.setAlbum(changed.get(1));
            }
            if (!changed.get(2).isEmpty() && !changed.get(2).equals(names.get(2)))  {
                song.setArtist(changed.get(2));
            }
            if (!changed.get(3).isEmpty() && !changed.get(3).equals(names.get(3)))  {
                song.setAlbumArtist(changed.get(3));
            }
            if (!changed.get(4).isEmpty() && !changed.get(4).equals(names.get(4)))  {
                song.setYear(Integer.parseInt(changed.get(4)));
            }

            songListView.setItems(library.getSongTitles());
        }
    }

    @FXML
    private void handleAddSongToP() {
        if (songListView.getSelectionModel().getSelectedIndex() >= 0 && playlistListView.getSelectionModel().getSelectedIndex() >= 0)   {
            library.getPlaylist(playlistListView.getSelectionModel().getSelectedItem()).addSong(library.getIndex(songListView.getSelectionModel().getSelectedItem()));
            if (playlistViewController != null) {
                playlistViewController.setSongs(this, playlistListView.getSelectionModel().getSelectedItem());
            }
        }   else {
            new Alert(Alert.AlertType.INFORMATION, "To add a song to a playlist, select the playlist in the playlists tab, the song in the songs tab, then press add song to playlist").showAndWait();
        }
    }

    @FXML
    private void handleAddSongToA()   {
        if (songListView.getSelectionModel().getSelectedIndex() >= 0 && albumListView.getSelectionModel().getSelectedIndex() >= 0)   {
            library.getAlbum(albumListView.getSelectionModel().getSelectedItem()).addSong(library.getIndex(songListView.getSelectionModel().getSelectedItem()));
        }   else {
            new Alert(Alert.AlertType.INFORMATION, "To add a song to an album, select the album in the albums tab, the song in the songs tab, then press add song to album").showAndWait();
        }
    }

    @FXML
    private void handleRemovePlaylist() {
        if (playlistListView.getSelectionModel().getSelectedIndex() >= 0)   {
            library.deletePlaylist(playlistListView.getSelectionModel().getSelectedItem(), false);
            playlistListView.setItems(library.getPlaylistNames());
        }
    }

    @FXML
    private void handleRemoveAlbum()    {
        if (albumListView.getSelectionModel().getSelectedIndex() >= 0)  {
            library.deleteAlbum(albumListView.getSelectionModel().getSelectedItem(), true);
            albumListView.setItems(library.getAlbumNames());
            songListView.setItems(library.getSongTitles());
        }
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
        try (FileOutputStream properties = new FileOutputStream("src" + File.separator
                + "main" + File.separator
                + "resources" + File.separator
                + "com" + File.separator
                + "oopproject" + File.separator
                + "player.properties")) {
            playerProperties.store(properties, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    private <T extends ButtonBase> void setImage(T button, String uri){
        ImageView thisImageView =
                new ImageView( new Image(Objects.requireNonNull(getClass().getResourceAsStream(uri))));
        thisImageView.setFitHeight(40);
        thisImageView.setFitWidth(50.0);
        button.setGraphic(thisImageView);
    }
}
