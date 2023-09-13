package com.oopproject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static java.lang.Integer.compare;

public class Album extends Playlist  {
    private int year = 0;
    private String artist = null;

    public Album(String name, Library library) {
        super(name, library);
    }

    public Album(String name, Library library, File folder) {
        this(name, library, folder.listFiles());
    }

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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
        songs.forEach(index -> Song.setAlbumArtist(getLibrary().getSong(index), artist));
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
        songs.forEach(index -> {
            if (Song.getYear(getLibrary().getSong(index)) == 0) {
                Song.setYear(getLibrary().getSong(index), year);
            }
        });
    }

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

    public void refreshOrder()  {
        Integer[] indexes = getSongIndexes().toArray(new Integer[0]);
        songs.forEach(index -> indexes[Song.getTrack(getLibrary().getSong(index)) - 1] = index);
        songs = new CopyOnWriteArrayList<>(indexes);
    }
}
