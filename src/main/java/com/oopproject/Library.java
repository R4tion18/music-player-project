package com.oopproject;

import javafx.scene.media.Media;

import java.io.File;
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

    public Library(Properties props, boolean isFirstSetup) {
        this.properties = props;
        File directory = new File(properties.getProperty("libraryFolder"));
        CopyOnWriteArrayList<File> files = new CopyOnWriteArrayList<>(List.of(Objects.requireNonNull(directory.listFiles())));

        if (!isFirstSetup)  {
            new Library(files);
        }   else {
            files.stream()
                    .sorted()
                    .collect(Collectors.toCollection(() -> files));

            IntStream
                    .range(0, files.size())
                    .forEachOrdered(i -> addNewSong(files.get(i).getAbsolutePath(), i));
        }
    }

    public Library(CopyOnWriteArrayList<File> files) {
        files.forEach(file -> {
            if (Song.getIndex(file.getAbsolutePath()).matches("")) {
                addNewSong(file.getAbsolutePath(), properties.size());
            }
            Arrays.stream(properties.getProperty(Song.getIndex(file.getAbsolutePath())).split("\n"))
                    .forEach(playlist -> {
                        playlists.putIfAbsent(playlist, new Playlist(playlist, this));
                        playlists.get(playlist).addSong(Integer.parseInt(Song.getIndex(file.getAbsolutePath())));
                    });
        });
    }

    public String getSong(int index) {
        return songs.get(index);
    }

    public void addSongFile(String path)    {
        File oldFile = new File(path);
        File song = new File(properties.getProperty("libraryFolder"), path);
        boolean delete = oldFile.delete();
        addNewSong(song.getAbsolutePath(), properties.size());
    }

    public void addNewSong(String path, int index)  {
        Song.setIndex(path, index);
        properties.setProperty(String.valueOf(index), "");
        songs.put(index, path);
        String album = new Media(path).getMetadata().get("album").toString();
        if (!album.matches("")) {
            albums.putIfAbsent(album, new Album(new Media(path).getMetadata().get("album artist").toString(), album, this));
            albums.get(album).addSong(Integer.parseInt(Song.getIndex(path)));
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
