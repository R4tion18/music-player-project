package com.oopproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

public class QueueViewController{
    @FXML ListView<String> queueListView;
    @FXML ScrollPane queueScrollPane;
    @FXML Button plusButton;
    @FXML Button minusButton;

    MusicPlayerOverviewController controller;
    SongQueue songs;
    ObservableList<String> queue;

    @FXML
    public void initialize() {
        queue = FXCollections.observableArrayList();
    }

    @FXML
    void plusAction(){
        if(queueListView.getSelectionModel().getSelectedIndex() != 0) {
            System.out.println(queueListView.getSelectionModel().getSelectedIndex());
            songs.modifyQueue(
                    (queueListView.getSelectionModel().getSelectedIndex() + songs.getSongNumber() + 1) % getSongSequence().size(),
                    (queueListView.getSelectionModel().getSelectedIndex() + songs.getSongNumber()) % getSongSequence().size());
            Collections.swap(queue,
                    queueListView.getSelectionModel().getSelectedIndex(),
                    queueListView.getSelectionModel().getSelectedIndex() - 1);
            queueListView.getSelectionModel().selectIndices(queueListView.getSelectionModel().getSelectedIndex() - 1);
        }
    }
    @FXML
    void minusAction(){
        if(queueListView.getSelectionModel().getSelectedIndex() != queueListView.getItems().size() - 1) {
            songs.modifyQueue(
                    (queueListView.getSelectionModel().getSelectedIndex() + songs.getSongNumber() + 1) % getSongSequence().size(),
                    (queueListView.getSelectionModel().getSelectedIndex() + songs.getSongNumber() + 2) % getSongSequence().size());
            Collections.swap(queue,
                    queueListView.getSelectionModel().getSelectedIndex(),
                    queueListView.getSelectionModel().getSelectedIndex() + 1);
            queueListView.getSelectionModel().selectIndices(queueListView.getSelectionModel().getSelectedIndex() + 1);

        }
    }

    public void setSongs(SongQueue songs) {
        this.songs = songs;
        IntStream.range(0, getSongSequence().size()).forEach(i -> queue.add(songs.getSongNames().get((i + songs.getSongNumber() + 1) % getSongSequence().size())));
        queueListView.setItems(queue);
    }

    public ArrayList<Integer> getSongSequence(){
        return this.songs.getSongSequence();
    }

    public void next(){
        queue.add(queue.remove(0));
    }
    public void previous(){
        queue.add(0, queue.remove(queue.size() - 1));
    }

    public void shuffle(){
        queue.clear();
        IntStream.range(0, getSongSequence().size()).forEach(i -> queue.add(songs.getSongNames().get((i + songs.getSongNumber() + 1) % getSongSequence().size())));
    }
}
