package com.oopproject;

import com.oopproject.mp3agic.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

public record Song(File file) {
    public static String getString(URI uri) {
        return uri.toString();
    }

    public static String getString(File file) {
        return getString(getURI(file));
    }

    public static URI getURI(File file) {
        return file.toURI();
    }

    public static URI getURI(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);  //change exception handling
        }
    }

    public static File getFile(URI uri) {
        return new File(uri);
    }

    public static File getFile(String uri) {
        return getFile(getURI(uri));
    }


    public static Mp3File getMp3File(File file) {
        Mp3File mp3 = null;
        try {
            mp3 = new Mp3File(file);
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            throw new RuntimeException(e);
        }

        return mp3;
    }

    public static void save(Mp3File mp3)    {
        File mp3File = new File(mp3.getFilename());
        File directory = mp3File.getParentFile().getParentFile();
        File tempFile = new File(directory.getPath() + mp3File.getName());

        try {
            mp3.save(tempFile.getPath());
        } catch (IOException | NotSupportedException e) {
            throw new RuntimeException(e);
        }

        Mp3File tempMp3 = getMp3File(tempFile);
        try {
            tempMp3.save(mp3File.getPath());
        } catch (IOException | NotSupportedException e) {
            throw new RuntimeException(e);
        }

        boolean deleted = tempFile.delete();
    }


    public static Optional<ID3v2> getTag(String uri) {
        return getTag(getFile(uri));
    }

    public static Optional<ID3v2> getTag(File file) {
        return getTag(getMp3File(file));
    }

    public static Optional<ID3v2> getTag(Mp3File mp3) {
        if (mp3.hasId3v2Tag()) {
            return Optional.of(mp3.getId3v2Tag());
        }

        return Optional.empty();
    }

    public static Mp3File createTag(File file) {
        Mp3File mp3 = getMp3File(file);

        if (getTag(mp3).isEmpty()) {
            mp3.setId3v2Tag(new ID3v24Tag());
        }

        return mp3;
    }

    public String getIndexString() {
        return getIndexString(file);
    }


    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static String getIndexString(File file) {
        createTag(file);
        if (getTag(file).get().getComment() == null)  {
            return null;
        }   else {
            return getTag(file).get().getComment().split(",")[0];
        }
    }

    public int getIndex() {
        return getIndex(file);
    }

    public static int getIndex(String uri) {
        return getIndex(getFile(uri));
    }

    public static int getIndex(File file) {
        return Integer.parseInt(Objects.requireNonNull(getIndexString(file)));
    }

    public void setIndex(Integer index) {
        setIndex(file, index);
    }


    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void setIndex(File file, Integer index) {
        Mp3File mp3 = createTag(file);
        getTag(mp3).get().setComment(index.toString());
        save(mp3);
    }


    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static String getConsecutiveString(File file) {
        createTag(file);
        if (getTag(file).get().getComment() == null) {
            return null;
        }   else {
            return getTag(file).get().getComment().split(",")[1];
        }
    }


    public String getTitle() {
        return getTitle(file);
    }

    public static String getTitle(String uri) {
        return getTitle(getFile(uri));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static String getTitle(File file) {
        createTag(file);

        if (getTag(file).get().getTitle() == null)  {
            return file.getName();
        }
        return getTag(file).get().getTitle();
    }

    public void setTitle(String title)  {
        setTitle(file, title);
    }


    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void setTitle(File file, String title)  {
        Mp3File mp3 = createTag(file);
        getTag(mp3).get().setTitle(title);
        save(mp3);
    }

    public String getAlbum() {
        return getAlbum(file);
    }

    public static String getAlbum(String uri) {
        return getAlbum(getFile(uri));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static String getAlbum(File file) {
        createTag(file);
        return getTag(file).get().getAlbum();
    }

    public void setAlbum(String album)  {
        setAlbum(file, album);
    }

    public static void setAlbum(String uri, String album)   {
        setAlbum(getFile(uri), album);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void setAlbum(File file, String album)    {
        Mp3File mp3 = createTag(file);
        getTag(mp3).get().setAlbum(album);
        save(mp3);
    }

    public String getAlbumArtist() {
        return getAlbumArtist(file);
    }

    public static String getAlbumArtist(String uri) {
        return getAlbumArtist(getFile(uri));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static String getAlbumArtist(File file) {
        createTag(file);
        return getTag(file).get().getAlbumArtist();
    }

    public void setAlbumArtist(String artist)   {
        setAlbumArtist(file, artist);
    }

    public static void setAlbumArtist(String uri, String artist)    {
        setAlbumArtist(getFile(uri), artist);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void setAlbumArtist(File file, String artist) {
        Mp3File mp3 = createTag(file);
        getTag(mp3).get().setAlbumArtist(artist);
        save(mp3);
    }

    public String getArtist() {
        return getArtist(file);
    }

    public static String getArtist(String uri) {
        return getArtist(getFile(uri));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static String getArtist(File file) {
        createTag(file);
        return getTag(file).get().getArtist();
    }

    public void setArtist(String artist)    {
        setArtist(file, artist);
    }

    public static void setArtist(String uri, String artist) {
        setArtist(getFile(uri), artist);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void setArtist(File file, String artist)  {
        Mp3File mp3 = createTag(file);
        getTag(mp3).get().setArtist(artist);
        save(mp3);
    }

    public int getYear() {
        return getYear(file);
    }

    public static int getYear(String uri) {
        return getYear(getFile(uri));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static int getYear(File file) {
        createTag(file);
        try {
            return Integer.parseInt(getTag(file).get().getYear());
        }   catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setYear(int year)   {
        setYear(file, year);
    }

    public static void setYear(String uri, int year)    {
        setYear(getFile(uri), year);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void setYear(File file, int year) {
        Mp3File mp3 = createTag(file);
        getTag(mp3).get().setYear(String.valueOf(year));
        save(mp3);
    }

    public int getTrack() {
        return getTrack(file);
    }

    public static int getTrack(String uri) {
        return getTrack(getFile(uri));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static int getTrack(File file) {
        createTag(file);
        try {
            return Integer.parseInt(getTag(file).get().getTrack());
        }   catch (NumberFormatException e) {
            return 0;
        }
    }


    public static void setTrack(String uri, int track) {
        setTrack(getFile(uri), track);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void setTrack(File file, int track)   {
        Mp3File mp3 = createTag(file);
        getTag(mp3).get().setTrack(String.valueOf(track));
        save(mp3);
    }
}
