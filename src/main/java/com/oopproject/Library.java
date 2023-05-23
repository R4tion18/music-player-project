package com.oopproject;

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

    public int getHighestIndex()    {
        return properties.stringPropertyNames()
                .stream()
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
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

    public Library(Properties properties, boolean isFirstSetup) {
        this.properties = properties;
        File directory = null;
        try {
            directory = new File(new URI(this.properties.getProperty("libraryFolder")));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);  //change exception handling
        }
        CopyOnWriteArrayList<File> files = getFiles(directory);

        if (!isFirstSetup)  {
            new Library(files);
        } else {
            CopyOnWriteArrayList<File> sorted = new CopyOnWriteArrayList<>();
            files.stream()
                    .sorted()
                    .collect(Collectors.toCollection(() -> sorted));

            IntStream.range(0, sorted.size())
                    .forEachOrdered(i -> addNewSong(Song.getString(sorted.get(i)), i));
        }
    }

    public Library(CopyOnWriteArrayList<File> files) {
        files.forEach(file -> {
            if (Song.getIndexString(file) == null) {
                addNewSong(Song.getString(file), getHighestIndex());
            }
            Arrays.stream(properties.getProperty(Song.getIndexString(file)).split("\n"))    //look over with new library
                    .forEach(playlist -> {
                        playlists.putIfAbsent(playlist, new Playlist(playlist, this));
                        playlists.get(playlist)
                                .addSong(Song.getIndex(file));
                    });
        });
    }

    public void addFromFolder(String folderUri) {
        File folder = null;
        try {
            folder = new File(new URI(folderUri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);  //change exception handling
        }

        addMultipleFiles(new CopyOnWriteArrayList<File>(Objects.requireNonNull(folder.listFiles())));
    }

    public void addMultipleFiles(CopyOnWriteArrayList<File> files)  {
        for (File file : files) {
            addSongFile(file.toURI().toString());
        }
    }

    public void addSongFile(String uri)    {
        File oldFile = null;
        try {
            oldFile = new File(new URI(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);  //change exception handling
        }
        File song = new File(properties.getProperty("libraryFolder"), uri);
        boolean delete = oldFile.delete();
        addNewSong(Song.getString(song), properties.size());
    }

    public void addNewSong(String uri, int index)  {
        Song.setIndex(uri, index);
        properties.setProperty(String.valueOf(index), "");
        songs.put(index, uri);

        String album = Song.getAlbum(uri);
        if (!(album == null)) {
            albums.putIfAbsent(album, new Album(album, this));
            albums.get(album).addSong(Song.getIndex(uri));
        }
    }

    public void deleteSong(int index)   {
        Arrays.stream(properties.getProperty(String.valueOf(index)).split("\n"))
                .forEach(playlist -> playlists.get(playlist).removeSong(index));

        String album = Song.getAlbum(getSong(index));
        if (!(album == null)) {
            albums.get(album).removeSong(index);
        }

        File song = new File(songs.get(index));
        boolean delete = song.delete();
        songs.remove(index);
        properties.remove(String.valueOf(index));
    }
}
