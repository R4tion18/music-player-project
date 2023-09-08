package com.oopproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SongQueue {

    private ArrayList<String> queue;
    private int songNumber = 0;
    private ArrayList<Integer> songSequence;
    private boolean isShuffle = false;



    public SongQueue(CopyOnWriteArrayList<String> newSongs) {
        queue = new ArrayList<>();
        queue.addAll(newSongs);
        songSequence = new ArrayList<>();
        IntStream.
                range(0, queue.size()).
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
                .map(i -> Song.getFile(queue.get(i)).getName())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public String getSong() {
        return queue.get(nextSongIndex(songNumber));
    }

    public String getName(){
        return Song.getFile(queue.get(nextSongIndex(songNumber))).getName();
    }
    public String getNextSong(){
        if (songNumber == queue.size() - 1) {
            songNumber = 0;
        } else {
            songNumber++;
        }
        return this.getSong();
    }
    public String getPreviousSong(){
        if (songNumber == 0) {
            songNumber = queue.size() - 1;
        } else {
            songNumber--;
        }
        return this.getSong();
    }
    public void addSong(String newSong){
        songSequence.add(queue.size());
        queue.add(newSong);
    }
    public void addSongs(ArrayList<String> newSongs){
        songSequence.addAll(IntStream.range(queue.size(), queue.size() + newSongs.size() - 1).boxed().toList());
        queue.addAll(newSongs);
    }
    public void removeSong(int index){
        queue.remove(index);
    }
    public void setShuffle() {
        if(!isShuffle){
            Collections.shuffle(songSequence);
            isShuffle = true;
        } else{
            songNumber = songSequence.get(songNumber);
            songSequence = new ArrayList<>();
            IntStream.
                    range(0, queue.size()).
                    forEach(i -> songSequence.add(i));
            isShuffle = false;
        }
    }
    public int nextSongIndex(int songRealIndex){
        return songSequence.get(songRealIndex);
    }

    public void modifyQueue(int firstIndex, int secondIndex){
        System.out.println("firstIndex = " + firstIndex + "\nsecondIndex = " + secondIndex);
        Collections.swap(songSequence, firstIndex, secondIndex);
    }
}
