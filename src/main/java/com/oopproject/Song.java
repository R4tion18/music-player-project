package com.oopproject;

import com.mpatric.mp3agic.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Song {
    public static Mp3File getMp3File(String uri)    {
        return getMp3File(getFile(uri));
    }

    public static Mp3File getMp3File(URI uri)   {
        return getMp3File(getFile(uri));
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
        setIndex(getFile(uri), index);
    }

    public static void setIndex(URI uri, Integer index) {
        setIndex(getFile(uri), index);
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
        return getIndexString(getFile(uri));
    }

    public static String getIndexString(URI uri)    {
        return getIndexString(getFile(uri));
    }

    public static String getIndexString(File file)    {
        return getMp3File(file).getId3v2Tag().getComment().split(",")[0];
    }

    public static int getIndex(String uri) {
        return getIndex(getFile(uri));
    }

    public static int getIndex(URI uri) {
        return getIndex(getFile(uri));
    }

    public static int getIndex(File file)  {
        return Integer.parseInt(getIndexString(file));
    }

    public static String getString(URI uri)  {
        return uri.toString();
    }

    public static String getString(File file)    {
        return getURI(file).toString();
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

    public static File getFile(URI uri)  {
        return new File(uri);
    }

    public static File getFile(String uri) {
        try {
            return new File(new URI(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);  //change exception handling
        }
    }

    public static void play(String uri)   {
        play(getFile(uri));
    }

    public static void play(URI uri)    {
        play(getFile(uri));
    }

    public static void play(File file)  {

    }

    public static String getTitle(String uri)   {
        return getTitle(getFile(uri));
    }

    public static String getTitle(URI uri) {
        return getTitle(getFile(uri));
    }

    public static String getTitle(File file) {
        return getMp3File(file).getId3v2Tag().getTitle();
    }

    public static String getAlbum(String uri)   {
        return getAlbum(getFile(uri));
    }

    public static String getAlbum(URI uri)  {
        return getAlbum(getFile(uri));
    }

    public static String getAlbum(File file) {
        return getMp3File(file).getId3v2Tag().getAlbum();
    }

    public static String getAlbumArtist(String uri) {
        return getAlbumArtist(getFile(uri));
    }

    public static String getAlbumArtist(URI uri)    {
        return getAlbumArtist(getFile(uri));
    }

    public static String getAlbumArtist(File file)   {
        return getMp3File(file).getId3v2Tag().getAlbumArtist();
    }

    public static String getArtist(String uri)  {
        return getArtist(getFile(uri));
    }

    public static String getArtist(URI uri) {
        return getArtist(getFile(uri));
    }

    public static String getArtist(File file)    {
        return getMp3File(file).getId3v2Tag().getArtist();
    }
}
