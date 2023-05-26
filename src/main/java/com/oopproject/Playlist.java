package com.oopproject;

import javafx.scene.media.Media;
import javafx.util.Duration;

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
        Playlist(name, library, folder.listFiles());

    }

    public Playlist(String name, Library library, File[] files) {
        Playlist(name, library);
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
        songs.add(index);
        library.putPlaylist(index, name);
        totalDuration = totalDuration.add(getMedia(index).getDuration());
    }

    public void removeSong(int index)   {
        songs.remove(Integer.valueOf(index));
        totalDuration = totalDuration.subtract(getMedia(index).getDuration());
    }

    public void play(boolean shuffle)   {
        SongQueue queue = new SongQueue(getSongs(), getLibrary());
        queue.play(shuffle);
    }

    public Media getMedia(int index)    {
        return new Media(library.getSong(index));
    }
}
