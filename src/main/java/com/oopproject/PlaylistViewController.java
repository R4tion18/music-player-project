package com.oopproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;

import java.util.stream.IntStream;

public class PlaylistViewController {
    @FXML ListView<String> playlistListView;
    @FXML ScrollPane playlistScrollPane;
    @FXML Label nameLabel;

    MusicPlayerOverviewController controller;
    String name;
    SongQueue songs;
    ObservableList<String> songList;

    @FXML
    public void initialize() {
        songList = FXCollections.observableArrayList();
    }

    @FXML
    void editAction()   {
        String newName;
        try {
            newName = controller.textInput("Current name: ", name, "New name: ", "Edit name");
        } catch (IllegalAccessException e) {
            newName = "";
        }

        if (!newName.isEmpty() && !newName.equals(name)) {
            while ( controller.library.getPlaylistNames().contains(newName)) {
                newName += " ";
            }
            controller.library.getPlaylist(name).setName(newName);

            name = newName;
            setNameLabel(name);
            controller.playlistListView.setItems(controller.library.getPlaylistNames());
        }
    }

    @FXML
    void removeAction(){
        controller.library.getPlaylist(name).removeSong(controller.library.getIndex(playlistListView.getSelectionModel().getSelectedItem()));
        initialize();
        setSongs(controller, name);
    }

    public void setNameLabel(String nameLabel)    {
        this.nameLabel.setText(nameLabel);
    }

    public void setSongs(MusicPlayerOverviewController controller, String name) {
        initialize();
        this.controller = controller;
        this.name = name;
        this.songs = new SongQueue(controller.library.getPlaylist(name).getSongURIs());
        IntStream.range(0, getSongSequence().size()).forEach(i -> songList.add(songs.getSongNames().get((i + songs.getSongNumber()) % getSongSequence().size())));
        playlistListView.setItems(songList);
        setNameLabel(name);
    }

    public ObservableList<Integer> getSongSequence(){
        return this.songs.getSongSequence();
    }
}
