package com.oopproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A Library to organise songs into playlists and albums.
 *
 * @see Playlist
 * @see Album
 */
public class Library {
    /**
     * A map matching the titles of the songs in the Library with their index.
     */
    public ConcurrentHashMap<String, Integer> titleIndex;

    /**
     * The properties of the Library.
     * The first property indicates the folder where the songs are stored,
     * and the rest are the indexes of the songs matched with the playlists they are in.
     */
    private final Properties properties;

    /**
     * A map matching the indexes of the songs with a URI string representing their file.
     */
    private final ConcurrentHashMap<Integer, String> songs = new ConcurrentHashMap<>();

    /**
     * A map matching the names of the playlists in the Library with their corresponding Playlist object.
     */
    private final ConcurrentHashMap<String, Playlist> playlists = new ConcurrentHashMap<>();

    /**
     * A map matching the names of the albums in the Library with their corresponding Album object.
     */
    private final ConcurrentHashMap<String, Album> albums = new ConcurrentHashMap<>();

    /**
     * Initialises a Library at the start of the application.
     * @param properties contains the name of the directory where the Library files are stored,
     *                   and the indexes of the songs stored in the directory.
     * @param isFirstSetup specifies if this is the first time the application was started on this system.
     * @throws RuntimeException if there is an error when opening the directory.
     */
    public Library(Properties properties, boolean isFirstSetup) {
        this.properties = properties;
        File directory;
        try {
            directory = new File(new URI(this.properties.getProperty("libraryFolder")));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        CopyOnWriteArrayList<File> files = getFiles(directory);

        if (!isFirstSetup)  {
            fillWith(files);
        } else {
            CopyOnWriteArrayList<File> sorted = new CopyOnWriteArrayList<>();
            files.stream()
                    .sorted()
                    .collect(Collectors.toCollection(() -> sorted));
            IntStream.range(0, sorted.size())
                    .forEachOrdered(i -> addNewSong(Song.getString(sorted.get(i)), i));
        }
    }

    /**
     * Populates the Library from a list of files.
     * @param files is the list of song files in the Library.
     */
    public void fillWith(CopyOnWriteArrayList<File> files) {
        files.forEach(file -> {
            Song song = new Song(file);
            if (song.getIndexString() == null) {
                addNewSong(Song.getString(file), getHighestIndex() + 1);
            }   else {
                addSong(file);
            }

            if (!properties.getProperty(song.getIndexString()).isEmpty())  {
                Arrays.stream(properties.getProperty(song.getIndexString()).split("\n"))
                        .forEach(playlist -> {
                            playlists.putIfAbsent(playlist, new Playlist(playlist, this));
                            playlists.get(playlist)
                                    .addSong(song.getIndex());
                        });
            }
        });
    }

    /**
     * Returns the highest index of a song in the Library.
     * @return the index of the song.
     */
    public int getHighestIndex()    {
        return this.properties.stringPropertyNames()
                .stream()
                .filter(string -> !string.contentEquals("libraryFolder"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
    }

    /**
     * Returns a list of the song titles in the Library.
     * @return the list of titles.
     */
    public ObservableList<String> getSongTitles() {
        updateIndex();
        return FXCollections.observableArrayList(titleIndex.keySet());
    }

    /**
     * Updates the index of song titles.
     */
    public void updateIndex()  {
        titleIndex = new ConcurrentHashMap<>();
        for (String uri : getSongURIs()) {
            String title = Song.getTitle(uri);
            Integer songIndex = Song.getIndex(uri);
            while (titleIndex.containsKey(title))    {
                title += " ";
            }
            titleIndex.putIfAbsent(title, songIndex);
        }
    }

    /**
     * Returns the list of files contained in the specified directory.
     * @param directory the directory from which to get the files.
     * @return the list of files.
     */
    public CopyOnWriteArrayList<File> getFiles(File directory)   {
        return new CopyOnWriteArrayList<>(List.of(Objects.requireNonNull(directory.listFiles())));
    }

    /**
     * Returns the list of URI strings of the songs in the Library.
     * @return the list of URI strings.
     */
    public CopyOnWriteArrayList<String> getSongURIs()   {
        return new CopyOnWriteArrayList<>(songs.values());
    }

    /**
     * Returns the list of names of the playlists contained in the Library.
     * @return the list of names.
     */
    public ObservableList<String> getPlaylistNames()    {
        return FXCollections.observableArrayList(playlists.keySet());
    }

    /**
     * Returns the list of names of the albums contained in the Library.
     *      * @return the list of names.
     */
    public ObservableList<String> getAlbumNames()   {
        return FXCollections.observableArrayList(albums.keySet());
    }

    /**
     * Returns the index of the song with the given title.
     * @param title the title of the song.
     * @return the index of the song.
     */
    public int getIndex(String title)   {
        return titleIndex.get(title);
    }

    /**
     * Returns the URI string of the song with the  given index.
     * @param index the index of the song.
     * @return the URI string of the song.
     */
    public String getSong(int index) {
        return songs.get(index);
    }

    /**
     * Returns the Playlist with the given name.
     * @param name the name of the playlist.
     * @return the Playlist object.
     */
    public Playlist getPlaylist(String name)    {
        return playlists.get(name);
    }

    /**
     * Returns the Album with the given name.
     * @param name the name of the album.
     * @return the Album object.
     */
    public Album getAlbum(String name)  {
        return albums.get(name);
    }

    /**
     * Adds a list of song files to the Library.
     * @param files the list of files to add.
     * @return a Vector containing the indexes of the added songs.
     */
    public Vector<Integer> addMultipleFiles(CopyOnWriteArrayList<File> files)  {
        Vector<Integer> indexes = new Vector<>();
        for (File file : files) {
            indexes.add(addSongFile(file.toURI().toString()));
        }
        return indexes;
    }

    /**
     * Adds a song file to the Library.
     * @param uri the URI string representing the file.
     * @return the index of the added song.
     */
    public int addSongFile(String uri)    {
        File oldFile;
        String directoryPath;
        try {
            oldFile = new File(new URI(uri));
            directoryPath = new File(new URI(properties.getProperty("libraryFolder"))).getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        File newFile = new File(directoryPath, oldFile.getName());
        try {
            Files.copy(oldFile.toPath(), newFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return addNewSong(Song.getString(newFile), getHighestIndex() + 1);
    }

    /**
     * Adds a song to the Library with the given index.
     * @param uri the URI string representing the song file.
     * @param index the index of the song.
     * @return the index of the song.
     */
    public int addNewSong(String uri, int index)  {
        Song song = new Song(Song.getFile(uri));
        song.setIndex(index);
        properties.setProperty(String.valueOf(index), "");
        songs.put(index, uri);

        String albumName = song.getAlbum();
        if (albumName != null) {
            createAlbum(albumName);
            albums.get(albumName).addSong(song.getIndex());
        }

        return index;
    }

    /**
     * Adds a song file to the Library.
     * @param file the song file.
     */
    public void addSong(File file)  {
        Song song = new Song(file);
        songs.putIfAbsent(song.getIndex(), Song.getString(file));

        String albumName = song.getAlbum();
        if (albumName != null) {
            createAlbum(albumName);
            albums.get(albumName).addSong(song.getIndex());
        }
    }

    /**
     * Removes a song from the Library.
     * @param index the index of the song to remove.
     */
    public void deleteSong(int index)   {
        Arrays.stream(properties.getProperty(String.valueOf(index)).split("\n"))
                .forEach(playlist -> {
                    if (playlists.get(playlist) != null)   {
                        playlists.get(playlist).removeSong(index);
                    }
                });

        String album = Song.getAlbum(getSong(index));
        if (!(album == null)) {
            albums.get(album).removeSong(index);
        }

        File song;
        try {
            song = new File(new URI(songs.get(index)));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            Files.delete(song.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        songs.remove(index);
        properties.remove(String.valueOf(index));
    }

    /**
     * Creates a new Playlist with the given name in the library.
     * @param name the name of the Playlist.
     */
    public void createPlaylist(String name)    {
        playlists.putIfAbsent(name, new Playlist(name, this));
    }

    /**
     * Adds a given song to a given Playlist.
     * @param index the index of the song.
     * @param playlist the name of the Playlist.
     */
    public void putInPlaylist(int index, String playlist)  {
        if (!properties.getProperty(String.valueOf(index)).contains(playlist))   {
            properties.setProperty(Integer.toString(index), properties.getProperty(String.valueOf(index)) + playlist + "\n");
        }
    }

    /**
     * Removes a given song from the given Playlist.
     * @param index the index of the song.
     * @param playlist the name of the Playlist.
     */
    public void removeFromPlaylist(int index, String playlist)  {
        StringBuilder songPlaylists = new StringBuilder(properties.getProperty(String.valueOf(index)));
        songPlaylists.delete(songPlaylists.indexOf(playlist), songPlaylists.lastIndexOf(playlist) + playlist.length());

        if (songPlaylists.toString().startsWith("\n"))    {
            songPlaylists.deleteCharAt(0);
        }   else {
            if (songPlaylists.toString().contains("\n\n"))   {
                songPlaylists.delete(songPlaylists.indexOf("\n\n"), songPlaylists.lastIndexOf("\n\n"));
            }
        }

        properties.setProperty(String.valueOf(index), songPlaylists.toString());
    }

    /**
     * Deletes a Playlist from the Library.
     * @param playlist the name of the Playlist.
     * @param fromLibrary indicates whether the songs in the Playlist should also be deleted.
     */
    public void deletePlaylist(String playlist, Boolean fromLibrary)    {
        if (fromLibrary)    {
            playlists.get(playlist).getSongIndexes().forEach(this::deleteSong);
        }   else {
            playlists.get(playlist).getSongIndexes().forEach(i -> removeFromPlaylist(i, playlist));
        }

        playlists.remove(playlist);
    }

    /**
     * Creates a new Album with the given name and artist from the given folder.
     * @param name the name of the Album.
     * @param artist the Album artist.
     * @param folder the folder containing the songs of the Album.
     */
    public void createAlbum(String name, String artist, File folder)   {
        albums.putIfAbsent(name, new Album(name, this, folder));
        albums.get(name).setArtist(artist);
    }

    /**
     * Creates a new Album with the given name and artist.
     * @param name the name of the Album.
     * @param artist the Album artist.
     */
    public void createAlbum(String name, String artist) {
        albums.putIfAbsent(name, new Album(name, this));
        albums.get(name).setArtist(artist);
    }

/**
 * Creates a new Album with the given name.
 * @param name the name of the Album.
 */
    public void createAlbum(String name)    {
        albums.putIfAbsent(name, new Album(name, this));
    }

    /**
     * Deletes an Album from the Library.
     * @param album the name of the Album.
     * @param fromLibrary indicates whether the songs on the Album should also be deleted.
     */
    public void deleteAlbum(String album, Boolean fromLibrary)   {
        albums.get(album).getSongIndexes().forEach(index -> Song.setAlbum(getSong(index), ""));

        if (fromLibrary)    {
            albums.get(album).getSongIndexes().forEach(this::deleteSong);
        }

        albums.remove(album);
    }
}
