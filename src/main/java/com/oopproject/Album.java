package com.oopproject;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class Album extends Playlist  {
    private String artist = null;
    private int year = 0;

    public Album(String name, Library library) {
        super(name, library);
    }

    public Album(String name, Library library, File folder) {
        super(name, library, folder);
    }

    public Album(String name, Library library, File[] files) {
        super(name, library, files);
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void addFrom(Vector<Integer> indexes)    {
        if (songs.size() == 0)  {
            songs = new CopyOnWriteArrayList<Integer>(new ArrayList<>(indexes.size()));
        }
        for (int index : indexes)   {
            songs.add(Song.getTrack(library.getSong(index)), index);
        }
    }

    @Override
    public void addSong(int index) {
        if (getArtist() == null)  {
            setArtist(Song.getArtist(library.getSong(index)));
        }

         if (getYear() == 0)    {
             setYear(Song.getYear(library.getSong(index)));
         }

        songs.stream()
                .mapToInt(song -> song)
                .filter(song ->
                        Song.getTrack(library.getSong(song)) >
                                Song.getTrack(library.getSong(index)))
                .findFirst()
                .ifPresent(song -> songs.add(songs.indexOf(song), index));
    }

    @Override
    public void removeSong(int index) {
        songs.remove(index);
    }
}
