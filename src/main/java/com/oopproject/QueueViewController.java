package com.oopproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.ArrayList;

public class QueueViewController{
    @FXML ListView<String> queueListView;
    SongQueue songs;
    ObservableList<String> queue;

    @FXML
    public void initialize() {
        queue = FXCollections.observableArrayList();
    }

    public SongQueue getSongs() {
        return songs;
    }

    public void setSongs(SongQueue songs) {
        this.songs = songs;
        update();
    }

    public ArrayList<Integer> getSongSequence(){
        return this.songs.getSongSequence();
    }

    public void update(){
        for(int i = 0; i < songs.getSongSequence().size(); i++){
            queue.add(songs.getSongNames().get((i + songs.getSongNumber()) % songs.getSongSequence().size()));
        }
        queueListView.setItems(queue);
    }
}
