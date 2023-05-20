package com.oopproject;

import javafx.scene.media.Media;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Library {
    private Properties properties;
    private final ConcurrentHashMap<Integer, String> songs = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Playlist> playlists = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Album> albums = new ConcurrentHashMap<>();

    public int getLibrarySize() {
        return songs.size();
    }

    public int getNumberOfPlaylists()   {
        return playlists.size();
    }

    public int getNumberOfAlbums()  {
        return albums.size();
    }

    public CopyOnWriteArrayList<File> getFiles(File directory)   {
        return new CopyOnWriteArrayList<>(List.of(Objects.requireNonNull(directory.listFiles())));
    }

    public Playlist getPlaylist(String name)    {
        return playlists.get(name);
    }

    public Album getAlbum(String name)  {
        return albums.get(name);
    }

    public String getSong(int index) {
        return songs.get(index);
    }

    public Library(Properties properties, boolean isFirstSetup) throws URISyntaxException {
        this.properties = properties;
        File directory = new File(new URI(this.properties.getProperty("libraryFolder")));
        CopyOnWriteArrayList<File> files = getFiles(directory);

        if (!isFirstSetup)  {
            new Library(files);
        } else {
            CopyOnWriteArrayList<File> sorted = new CopyOnWriteArrayList<>();
            files.stream()
                    .sorted()
                    .collect(Collectors.toCollection(() -> sorted));

            IntStream.range(0, sorted.size())
                    .forEachOrdered(i -> addNewSong(Song.getStringFromFile(sorted.get(i)), i));
        }
    }

    public Library(CopyOnWriteArrayList<File> files) {
        files.forEach(file -> {
            if (Song.getIndexString(file) == null) {    //look over with new library
                addNewSong(Song.getStringFromFile(file), properties.size());
            }
            Arrays.stream(properties.getProperty(Song.getIndexString(file)).split("\n"))    //look over with new library
                    .forEach(playlist -> {
                        playlists.putIfAbsent(playlist, new Playlist(playlist, this));
                        playlists.get(playlist)
                                .addSong(Song.getIndex(file));
                    });
        });
    }

    public void addSongFile(String uri)    {
        File oldFile = new File(uri);
        File song = new File(properties.getProperty("libraryFolder"), uri);
        boolean delete = oldFile.delete();
        addNewSong(Song.getStringFromFile(song), properties.size());
    }

    public void addNewSong(String uri, int index)  {
        Song.setIndex(uri, index);
        properties.setProperty(String.valueOf(index), "");
        songs.put(index, uri);

        Object album = new Media(uri).getMetadata().get("album");
        if (!(album == null)) {
            albums.putIfAbsent(album.toString(), new Album(new Media(uri).getMetadata().get("album artist").toString(), album.toString(), this));
            albums.get(album.toString()).addSong(Integer.parseInt(Song.getIndexString(uri)));
        }
    }

    public void deleteSong(int index)   {
        Arrays.stream(properties.getProperty(String.valueOf(index)).split("\n"))
                .forEach(playlist -> playlists.get(playlist).removeSong(index));

        String album = new Media(songs.get(index)).getMetadata().get("album").toString();
        if (!album.matches("")) {
            albums.get(album).removeSong(index);
        }

        File song = new File(songs.get(index));
        boolean delete = song.delete();
        songs.remove(index);
        properties.remove(String.valueOf(index));
    }
}
