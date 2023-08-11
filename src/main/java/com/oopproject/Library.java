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
                .mapToInt(Integer::parseInt)    //may not work, not all are ints, fix
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
                addNewSong(Song.getString(file), getHighestIndex() + 1);
            }

            Arrays.stream(properties.getProperty(Song.getIndexString(file)).split("\n"))    //look over with new library
                    .forEach(playlist -> {
                        playlists.putIfAbsent(playlist, new Playlist(playlist, this));
                        playlists.get(playlist)
                                .addSong(Song.getIndex(file));
                    });
        });
    }

    public void createPlaylist(String name, File folder)    {
        createPlaylist(name);
        addFromFolder(folder.toURI().toString()).forEach(index -> playlists.get(name).addSong(index));
    }

    public void createPlaylist(String name, CopyOnWriteArrayList<File> files)   {
        createPlaylist(name);
        addMultipleFiles(files).forEach(index -> playlists.get(name).addSong(index));
    }

    public void createPlaylist(String name)    {
        playlists.putIfAbsent(name, new Playlist(name, this));
    }

    public void putInPlaylist(int index, String playlist)  {
        if (!properties.getProperty(String.valueOf(index)).contains(playlist))   {
            properties.setProperty(Integer.toString(index), playlist + "\n");
        }
    }

    public void removeFromPlaylist(int index, String playlist)  {
        StringBuilder playlists = new StringBuilder(properties.getProperty(String.valueOf(index)));
        playlists.delete(playlists.indexOf(playlist), playlists.lastIndexOf(playlist) + 1);

        if (playlists.toString().startsWith("\n"))    {
            playlists.deleteCharAt(0);
        }   else {
            playlists.delete(playlists.indexOf("\n\n"),playlists.lastIndexOf("\n\n"));
        }

        properties.setProperty(String.valueOf(index), playlists.toString());
    }

    public void createAlbum(String name, String artist, File folder)   {
        albums.putIfAbsent(name, new Album(name, this));
        albums.get(name).setArtist(artist);
        Vector<Integer> indexes = addFromFolder(folder.toURI().toString());
        albums.get(name).addFrom(indexes);
    }

    public Vector<Integer> addFromFolder(String folderUri) {
        File folder = null;
        try {
            folder = new File(new URI(folderUri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);  //change exception handling
        }

        return addMultipleFiles(new CopyOnWriteArrayList<File>(Objects.requireNonNull(folder.listFiles())));
    }

    public Vector<Integer> addMultipleFiles(CopyOnWriteArrayList<File> files)  {
        Vector<Integer> indexes = new Vector<>();
        for (File file : files) {
            indexes.add(addSongFile(file.toURI().toString()));
        }
        return indexes;
    }

    public int addSongFile(String uri)    {
        File oldFile = null;
        try {
            oldFile = new File(new URI(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);  //change exception handling
        }
        File song = new File(properties.getProperty("libraryFolder"), uri);
        boolean delete = oldFile.delete();
        return addNewSong(Song.getString(song), getHighestIndex() + 1);
    }

    public int addNewSong(String uri, int index)  {
        if(Song.getIndexString(uri) == null) {
            Song.setIndex(uri, index);
            properties.setProperty(String.valueOf(index), "");
            songs.put(index, uri);
        }

        String album = Song.getAlbum(uri);
        if (album != null) {
            albums.putIfAbsent(album, new Album(album, this));
            albums.get(album).addSong(Song.getIndex(uri));
        }

        return index;
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
