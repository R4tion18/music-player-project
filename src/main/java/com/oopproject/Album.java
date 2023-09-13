package com.oopproject;

import java.io.File;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An Album to store and play songs from the same album.
 *
 * @see Playlist
 */
public class Album extends Playlist  {
    /**
     * The year of release of the Album.
     */
    private int year = 0;

    /**
     * The artist of the Album.
     */
    private String artist = null;

    /**
     * Creates an Album with the given.
     * @param name the Album name.
     * @param library the Library the Album is stored in.
     */
    public Album(String name, Library library) {
        super(name, library);
    }

    /**
     * Creates an Album with the given name from the given folder.
     * @param name the name of the Album.
     * @param library the Library the Album is stored in.
     * @param folder the folder where the songs of the Album are stored.
     */
    public Album(String name, Library library, File folder) {
        this(name, library, folder.listFiles());
    }

    /**
     * Creates an Album with the given name from the given list of files.
     * @param name the name of the Album.
     * @param library the Library the Album is stored in.
     * @param files the files of the songs on the Album.
     */
    public Album(String name, Library library, File[] files) {
        this(name, library);
        getLibrary().addMultipleFiles(new CopyOnWriteArrayList<>(files)).forEach(this::addSong);
    }

    @Override
    public void setName(String name)    {
        getLibrary().createAlbum(name, getArtist());
        getLibrary().getAlbum(name).addFrom(new Vector<>(songs));
        getLibrary().deleteAlbum(getName(), false);
    }

    /**
     * Returns the artist of the Album.
     * @return the artist of the Album.
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Changes the artist on the Album.
     * @param artist the new artist.
     */
    public void setArtist(String artist) {
        this.artist = artist;
        songs.forEach(index -> Song.setAlbumArtist(getLibrary().getSong(index), artist));
    }

    /**
     * Returns the year of release of the Album.
     * @return the year of release of the Album.
     */
    public int getYear() {
        return year;
    }

    /**
     * Changes the year of release of the Album.
     * @param year the new year of release.
     */
    public void setYear(int year) {
        this.year = year;
        songs.forEach(index -> {
            if (Song.getYear(getLibrary().getSong(index)) == 0) {
                Song.setYear(getLibrary().getSong(index), year);
            }
        });
    }

    /**
     *  `Adds songs to the Album from a list of indexes.
     * @param indexes the list of indexes.
     */
    public void addFrom(Vector<Integer> indexes)    {
        if (songs.isEmpty())  {
            songs = new CopyOnWriteArrayList<>(indexes);
        }
        indexes.forEach(index -> {
            if (Song.getTrack(getLibrary().getSong(index)) >= 0) {
                songs.add(Song.getTrack(getLibrary().getSong(index)) - 1, index);
            } else {
                songs.add(index);
            }
            Song.setAlbum(getLibrary().getSong(index), getName());
        });
    }

    @Override
    public void addSong(int index) {
        Song song = new Song(Song.getFile(getLibrary().getSong(index)));
        song.setAlbum(getName());

        if (getArtist() == null)  {
            setArtist(song.getArtist());
        }   else if (song.getArtist() == null)    {
            song.setArtist(getArtist());
        }

         if (getYear() == 0)    {
             setYear(song.getYear());
         }  else if (song.getYear() == 0) {
             song.setYear(getYear());
         }

        songs.stream()
                .mapToInt(i -> i)
                .filter(s ->
                        Song.getTrack(getLibrary().getSong(s)) > song.getTrack())
                .findFirst()
                .ifPresent(s -> songs.add(songs.indexOf(s), index));

        if(!songs.contains(index))   {
            songs.add(index);
        }
    }


    @Override
    public void removeSong(int index) {
        songs.remove(Integer.valueOf(index));

        songs.stream()
                .filter(s ->
                        Song.getTrack(getLibrary().getSong(s)) >= Song.getTrack(getLibrary().getSong(index)))
                .forEach(s ->
                        Song.setTrack(getLibrary().getSong(s), Song.getTrack(getLibrary().getSong(s)) - 1));

        Song.setAlbum(getLibrary().getSong(index), "");
    }

    /**
     * Refreshes the order of the songs on the Album.
     */
    public void refreshOrder()  {
        Integer[] indexes = getSongIndexes().toArray(new Integer[0]);
        songs.forEach(index -> indexes[Song.getTrack(getLibrary().getSong(index)) - 1] = index);
        songs = new CopyOnWriteArrayList<>(indexes);
    }
}
