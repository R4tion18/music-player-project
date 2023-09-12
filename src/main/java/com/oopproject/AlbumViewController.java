package com.oopproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.IntStream;

public class AlbumViewController {
    @FXML ListView<String> albumListView;
    @FXML ScrollPane albumScrollPane;
    @FXML Button plusButton;
    @FXML Button minusButton;
    @FXML Label titleLabel;
    @FXML Label artistLabel;
    @FXML Label yearLabel;

    MusicPlayerOverviewController controller;
    String title;
    SongQueue songs;
    ObservableList<String> songList;

    @FXML
    public void initialize() {
        songList = FXCollections.observableArrayList();
        setImage(plusButton, "icons/plusIcon.png");
        setImage(minusButton, "icons/minusIcon.png");
    }

    @FXML
    void editAction()   {
        String newTitle;
        String newArtist;
        String newYear;
        try {
            newTitle = controller.textInput("Current title: ", title, "New title: ", "Edit title");
        } catch (IllegalAccessException e) {
            return;
        }
        try {
            newArtist = controller.textInput("Current artist: ", controller.library.getAlbum(title).getArtist(), "New artist: ", "Edit artist");
        } catch (IllegalAccessException e) {
            return;
        }
        try {
            newYear = controller.textInput("Current year: ", String.valueOf(controller.library.getAlbum(title).getYear()), "New year: ", "Edit year");
        } catch (IllegalAccessException e) {
            return;
        }

        if (!newTitle.isEmpty() && !newTitle.equals(title)) {
            while ( controller.library.getPlaylistNames().contains(newTitle)) {
                newTitle += " ";
            }
            controller.library.getAlbum(title).setName(newTitle);
            title = newTitle;
        }
        if (!newArtist.isEmpty() && !newArtist.equals(controller.library.getAlbum(title).getArtist()))   {
            controller.library.getAlbum(title).setArtist(newArtist);
        }
        if (!newYear.isEmpty() && Integer.parseInt(newYear) != controller.library.getAlbum(title).getYear())   {
            controller.library.getAlbum(title).setYear(Integer.parseInt(newYear));
        }

        setLabels(title,
                controller.library.getAlbum(title).getArtist(),
                String.valueOf(controller.library.getAlbum(title).getYear()));
    }

    @FXML
    void plusAction(){
        if(albumListView.getSelectionModel().getSelectedIndex() != 0) {
            songs.modifyQueue(
                    (albumListView.getSelectionModel().getSelectedIndex() + songs.getSongNumber() + 1) % getSongSequence().size(),
                    (albumListView.getSelectionModel().getSelectedIndex() + songs.getSongNumber()) % getSongSequence().size());
            Collections.swap(songList,
                    albumListView.getSelectionModel().getSelectedIndex(),
                    albumListView.getSelectionModel().getSelectedIndex() - 1);
            albumListView.getSelectionModel().selectIndices(albumListView.getSelectionModel().getSelectedIndex() - 1);
        }
    }
    @FXML
    void minusAction(){
        if(albumListView.getSelectionModel().getSelectedIndex() != albumListView.getItems().size() - 1) {
            songs.modifyQueue(
                    (albumListView.getSelectionModel().getSelectedIndex() + songs.getSongNumber() + 1) % getSongSequence().size(),
                    (albumListView.getSelectionModel().getSelectedIndex() + songs.getSongNumber() + 2) % getSongSequence().size());
            Collections.swap(songList,
                    albumListView.getSelectionModel().getSelectedIndex(),
                    albumListView.getSelectionModel().getSelectedIndex() + 1);
            albumListView.getSelectionModel().selectIndices(albumListView.getSelectionModel().getSelectedIndex() + 1);

        }
    }

    @FXML
    void removeAction(){
        controller.library.getAlbum(title).removeSong(controller.library.getIndex(albumListView.getSelectionModel().getSelectedItem()));
        initialize();
        setSongs(controller, title);
    }

    @FXML void saveAction() {
        IntStream.range(0, songList.size()).forEach(i -> Song.setTrack(controller.library.getSong(controller.library.getIndex(songList.get(i))), i + 1));
        controller.library.getAlbum(title).refreshOrder();
    }

    public void setLabels(String title, String artist, String year) {
        titleLabel.setText(title);
        artistLabel.setText(artist);
        yearLabel.setText(year);
    }

    public void setSongs(MusicPlayerOverviewController controller, String title) {
        initialize();
        this.controller = controller;
        this.title = title;
        this.songs = new SongQueue(controller.library.getAlbum(title).getSongURIs());
        IntStream.range(0, getSongSequence().size()).forEach(i -> songList.add(songs.getSongNames().get((i + songs.getSongNumber()) % getSongSequence().size())));
        albumListView.setItems(songList);
        setLabels(title,
                controller.library.getAlbum(title).getArtist(),
                String.valueOf(controller.library.getAlbum(title).getYear()));
    }

    public ObservableList<Integer> getSongSequence(){
        return this.songs.getSongSequence();
    }

    private void setImage(Button button, String uri){
        ImageView thisImageView =
                new ImageView( new Image(Objects.requireNonNull(getClass().getResourceAsStream(uri))));
        thisImageView.setFitHeight(40);
        thisImageView.setFitWidth(35.0);
        button.setGraphic(thisImageView);
    }
}
