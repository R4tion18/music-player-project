package com.oopproject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SongQueue {
    //This shit can be changed, songs must be a list of something (String, File, URI) that represent
    //the song queue and can be converted to URI.toString for the media player
    private File[] files;
    private ArrayList<File> songs;


  //This shit is important and cannot be changed
    private int songNumber = 0;
    private ArrayList<Integer> songSequence;
    private boolean isShuffle = false;



    public SongQueue(File directory) {
        files = directory.listFiles();
        songs = new ArrayList<>();
        try {
            if (files != null) {
                songs.addAll(Arrays.asList(files));
            } else {
                throw new IllegalArgumentException();
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        songSequence = new ArrayList<>();
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

    public ArrayList<String> getSongNames(){
        return songSequence.stream()
                .map(i -> songs.get(i).getName())
                .collect(Collectors.toCollection(ArrayList::new));
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
        return this.getSong();
    }
    public String getPreviousSong(){
        if (songNumber == 0) {
            songNumber = songs.size() - 1;
        } else {
            songNumber--;
        }
        return this.getSong();
    }


    public void setSongSequence(ArrayList<Integer> songSequence) {
        this.songSequence = songSequence;
    }
    public void setShuffle() {
        if(!isShuffle){
            Collections.shuffle(songSequence);
            isShuffle = true;
        } else{
            songNumber = songSequence.get(songNumber);
            songSequence = new ArrayList<>();
            IntStream.
                    range(0, songs.size()).
                    forEach(i -> songSequence.add(i));
            isShuffle = false;
        }
    }

    public int nextSongIndex(int songRealIndex){
        return songSequence.get(songRealIndex);
    }
}
