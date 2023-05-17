package com.oopproject;

import javafx.scene.media.Media;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Library {
    private final ConcurrentHashMap<Integer, String> songs = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Playlist> playlists = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Album> albums = new ConcurrentHashMap<>();

    public Library(Properties props, boolean isFirstSetup) {
        File directory = new File(props.getProperty("libraryFolder"));
        CopyOnWriteArrayList<File> files = new CopyOnWriteArrayList<>(List.of(Objects.requireNonNull(directory.listFiles())));

        if (!isFirstSetup)  {
            new Library(props, files);
        }   else {
            files.stream()
                    .sorted()
                    .collect(Collectors.toCollection(() -> files));

            for (int i = 0; i < files.size(); ++i) {
                addNewSong(files.get(i).getAbsolutePath(), i, props);
            }
        }
    }

    public Library(Properties props, CopyOnWriteArrayList<File> files) {
        for (File file : files) {
            if (Song.getIndex(file.getAbsolutePath()).matches(""))  {
                addNewSong(file.getAbsolutePath(), props.size(), props);
            }

            String[] pls = props.getProperty(Song.getIndex(file.getAbsolutePath())).split("\n");
            for (String pl : pls)   {
                playlists.putIfAbsent(pl, new Playlist(pl));
                playlists.get(pl).addSong(Integer.parseInt(Song.getIndex(file.getAbsolutePath())));
            }

            String album = new Media(file.getAbsolutePath()).getMetadata().get("album").toString();
            if (!album.matches(""))    {
                albums.putIfAbsent(album, new Album(album));
                albums.get(album).addSong(Integer.parseInt(Song.getIndex(file.getAbsolutePath())));
            }
        }
    }

    public void addNewSong(String path, int index, Properties props)  {
        Song.setIndex(path, index);
        props.setProperty(String.valueOf(index), "");
        songs.put(index, path);
    }
}
