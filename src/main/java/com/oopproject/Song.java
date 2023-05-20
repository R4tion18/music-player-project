package com.oopproject;

import com.mpatric.mp3agic.*;

import javafx.scene.media.Media;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Song {
    public static Mp3File getMp3File(String uri)    {
        return getMp3File(getFileFromString(uri));
    }

    public static Mp3File getMp3File(URI uri)   {
        return getMp3File(getFileFromURI(uri));
    }

    public static Mp3File getMp3File(File file)    {
        Mp3File mp3 = null;
        try {
            mp3 = new Mp3File(file);
        } catch (IOException e) {
            e.printStackTrace();    //change exception handling
        } catch (UnsupportedTagException e) {
            e.getCause().printStackTrace(); //change exception handling
        } catch (InvalidDataException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);  //change exception handling
        }

        return mp3;
    }

    public static void setIndex(String uri, Integer index)  {
        setIndex(getFileFromString(uri), index);
    }

    public static void setIndex(URI uri, Integer index) {
        setIndex(getFileFromURI(uri), index);
    }

    public static void setIndex(File file, Integer index) {
        Mp3File song = getMp3File(file);

        assert song != null;
        ID3v2 tag;
        if (song.hasId3v2Tag()) {
            tag = song.getId3v2Tag();
        }   else {
            tag = new ID3v24Tag();
            song.setId3v2Tag(tag);
        }
        tag.setComment(index.toString());
    }

    public static String getIndexString(String uri)    {
        return getIndexString(getFileFromString(uri));
    }

    public static String getIndexString(URI uri)    {
        return getIndexString(getFileFromURI(uri));
    }

    public static String getIndexString(File file)    {
        return getMp3File(file).getId3v2Tag().getComment(); //fix for consecutives
    }

    public static int getIndex(String uri) {
        return getIndex(getFileFromString(uri));
    }

    public static int getIndex(URI uri) {
        return getIndex(getFileFromURI(uri));
    }

    public static int getIndex(File file)  {
        return Integer.parseInt(getIndexString(file));
    }

    public static String getStringFromURI(URI uri)  {
        return uri.toString();
    }

    public static String getStringFromFile(File file)    {
        return getURIFromFile(file).toString();
    }

    public static URI getURIFromFile(File file) {
        return file.toURI();
    }

    public static URI getURIFromString(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);  //change exception handling
        }
    }

    public static File getFileFromURI(URI uri)  {
        return new File(uri);
    }

    public static File getFileFromString(String uri) {
        try {
            return new File(new URI(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);  //change exception handling
        }
    }

    public static String getTitle(File file) {
        return getMp3File(file).getId3v2Tag().getTitle();
    }

    public static String getAlbum(File file) {
        return getMp3File(file).getId3v2Tag().getAlbum();
    }

    public static String getAlbumArtist(File file)   {
        return getMp3File(file).getId3v2Tag().getAlbumArtist();
    }

    public static String getArtist(File file)    {
        return getMp3File(file).getId3v2Tag().getArtist();
    }
}
