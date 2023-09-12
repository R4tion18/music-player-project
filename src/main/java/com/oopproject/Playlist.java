package com.oopproject;

import java.io.File;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Playlist {
    private final String name;
    protected CopyOnWriteArrayList<Integer> songs = new CopyOnWriteArrayList<>();
    private final Library library;

    public Playlist(String name, Library library) {
        this.name = name;
        this.library = library;
    }

    public Playlist(String name, Library library, File folder)  {
        this(name, library, folder.listFiles());
    }

    public Playlist(String name, Library library, File[] files) {
        this(name, library);
        library.addMultipleFiles(new CopyOnWriteArrayList<>(files)).forEach(this::addSong);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        library.createPlaylist(name);
        songs.forEach(library.getPlaylist(name)::addSong);
        songs.forEach(this::removeSong);
        library.deletePlaylist(this.name, false);
    }

    public CopyOnWriteArrayList<Integer> getSongIndexes() {
        return songs;
    }

    public CopyOnWriteArrayList<String> getSongURIs() {
        return this.songs.stream()
                .map(library::getSong)
                .sorted(Comparator.comparingInt(Song::getTrack))
                .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    }

    public Library getLibrary() {
        return library;
    }

    public void addSong(int index)  {
        if(!songs.contains(index))   {
            songs.add(index);
        }

        getLibrary().putInPlaylist(index, getName());
    }

    public void removeSong(int index)   {
        songs.remove(Integer.valueOf(index));
        getLibrary().removeFromPlaylist(index, getName());
    }
}
