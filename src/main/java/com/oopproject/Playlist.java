package com.oopproject;

import javafx.scene.media.Media;
import javafx.util.Duration;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

public class Playlist {
    private String name;
    private Duration totalDuration;
    protected CopyOnWriteArrayList<Integer> songs = new CopyOnWriteArrayList<>();
    protected final Library library;

    public Playlist(String name, Library library) {
        this.name = name;
        this.library = library;
    }

    public Playlist(String name, Library library, File folder)  {
        this.library = library;
        new Playlist(name, library, folder.listFiles());
    }

    public Playlist(String name, Library library, File[] files) {
        this.library = library;
        new Playlist(name, library);
        library.addMultipleFiles(new CopyOnWriteArrayList<>(files)).forEach(this::addSong);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CopyOnWriteArrayList<Integer> getSongs() {
        return songs;
    }

    public Library getLibrary() {
        return library;
    }

    public void addSong(int index)  {
        if(!songs.contains(index))   {
            songs.add(index);
        }

        library.putInPlaylist(index, getName());
    }

    public void removeSong(int index)   {
        songs.remove(Integer.valueOf(index));
        library.removeFromPlaylist(index, getName());
    }

    public void play(boolean shuffle)   {
        SongQueue queue = new SongQueue(getSongs(), getLibrary());
        queue.play(shuffle);
    }

    public Media getMedia(int index)    {
        return new Media(library.getSong(index));
    }
}
