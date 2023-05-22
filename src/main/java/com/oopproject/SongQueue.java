package com.oopproject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

public class SongQueue {
    private File[] files;
    private ArrayList<File> songs;
    private int songNumber = 0;
    private ArrayList<Integer> songSequence;
    private boolean isRandom = false;



    public SongQueue(File directory) {
        files = directory.listFiles();
        songs = new ArrayList<>();
        if (files != null) {
            songs.addAll(Arrays.asList(files));
        }
        songSequence = new ArrayList<>(songs.size());
        IntStream.
                range(0, songs.size()).
                forEach(i -> songSequence.add(i));
    }

    public int getSongNumber() {
        return songNumber;
    }

    public ArrayList<Integer> getSongSequence() {
        return songSequence;
    }

    public String getSong() {
        return songs.get(nextSongIndex(songNumber)).toURI().toString();
    }

    public String getName(){
        return songs.get(nextSongIndex(songNumber)).getName();
    }
    public String getNextSong(){
        if (songNumber == songs.size() - 1) {
            songNumber = 0;
        } else {
            songNumber++;
        }
        return songs.get(nextSongIndex(songNumber)).toURI().toString();
    }
    public String getPreviousSong(){
        if (songNumber == 0) {
            songNumber = songs.size() - 1;
        } else {
            songNumber--;
        }
        return songs.get(nextSongIndex(songNumber)).toURI().toString();
    }

    public void setRandom() {
        if(!isRandom){
            Collections.shuffle(songSequence);
            isRandom = true;
        } else{
            isRandom = false;
        }
    }

    private int nextSongIndex(int songRealIndex){
        if(!isRandom){
            return songRealIndex;
        }else{
            return songSequence.get(songRealIndex);
        }
    }
}
