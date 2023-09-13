package com.oopproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A SongQueue that saves all the songs you want to play
 */
public class SongQueue {

    private final ArrayList<String> queue;
    private final ObservableList<Integer> songSequence;
    private int songNumber = 0;
    private boolean isShuffle = false;

    /**
     * Constructor without parameters
     */
    public SongQueue()  {
        queue = new ArrayList<>();
        songSequence = FXCollections.observableArrayList();
    }

    /**
     * Constructor with a new list of songs to play
     * @param newSongs contains the file's uri of the new queue
     */
    public SongQueue(CopyOnWriteArrayList<String> newSongs) {
        queue = new ArrayList<>();
        queue.addAll(newSongs);
        songSequence = FXCollections.observableArrayList();
        IntStream.
                range(0, queue.size()).
                forEach(songSequence::add);
    }

    /**
     * Getter for the index
     * @return queue index
     */
    public int getSongNumber() {
        return songNumber;
    }

    /**
     * Getter for the sequence in which the song are to play
     * @return List of integer related to this.queue
     */
    public ObservableList<Integer> getSongSequence() {
        return songSequence;
    }

    /**
     * Getter for the names of the songs
     * @return List of names
     */
    public ArrayList<String> getSongNames(){
        return songSequence.stream()
                .map(i -> Song.getTitle(queue.get(i)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Getter for the song in play now
     * @return the song's URI
     */
    public String getSong() {
        return queue.get(nextSongIndex(songNumber));
    }

    /**
     * Getter for the next song in the queue
     * @return the song at the next index URI, if it's the end of the queue it loops back
     */
    public String getNextSong(){
        if (songNumber == queue.size() - 1) {
            songNumber = 0;
        } else {
            songNumber++;
        }
        return this.getSong();
    }

    /**
     * Getter for the previous song in the queue
     * @return the song at the previous index URI, if it's the first of the queue it takes the last
     */
    public String getPreviousSong(){
        if (songNumber == 0) {
            songNumber = queue.size() - 1;
        } else {
            songNumber--;
        }
        return this.getSong();
    }

    /**
     * Add a new song at the end of the queue.
     * @param newSong the song's URI converted into string
     */
    public void addSong(String newSong){
        songSequence.add(queue.size());
        queue.add(newSong);
    }

    /**
     * Add a list of new songs at the end of the queue.
     * @param newSongs a List of the songs URIs converted into string
     */
    public void addSongs(ArrayList<String> newSongs){
        songSequence.addAll(IntStream.range(queue.size(), queue.size() + newSongs.size()).boxed().toList());
        queue.addAll(newSongs);
    }

    /**
     * Remove the song at the given index in the sequence
     * @param index Index of the song in songSequence
     */
    public void removeSong(int index){
        int realIndex = songSequence.get(index);
        queue.remove(realIndex);
        songSequence.remove(index);
        for(int i = 0; i < songSequence.size(); i++){
            if (songSequence.get(i) > realIndex){
               songSequence.set(i, songSequence.get(i) - 1);
            }
        }
    }

    /**
     * Shuffle or unshuffle songSequence without touching the queue
     */
    public void setShuffle() {
        if(!isShuffle){
            Collections.shuffle(songSequence);
            isShuffle = true;
        } else{
            songNumber = songSequence.get(songNumber);
            songSequence.clear();
            IntStream.
                    range(0, queue.size()).
                    forEach(songSequence::add);
            isShuffle = false;
        }
    }

    /**
     * Convert the index in songSequence with the index in queue and vice-versa
     * @param songRealIndex the index in songSequence/queue
     * @return the index in queue/songSequence
     */
    public int nextSongIndex(int songRealIndex){
        return songSequence.get(songRealIndex);
    }

    /**
     * Swaps two songs in songSequence
     * @param firstIndex the index of the first song
     * @param secondIndex the index of the second song
     */
    public void modifyQueue(int firstIndex, int secondIndex){
        Collections.swap(songSequence, firstIndex, secondIndex);
    }
}
