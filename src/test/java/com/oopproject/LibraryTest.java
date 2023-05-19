package com.oopproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {
    Library library;
    Properties props;

    @BeforeEach
    void setUp() {
        Properties def = new Properties();
        def.setProperty("libraryFolder", "");
        props = new Properties(def);
    }

    @Test
    void Library()   {
        props.setProperty("libraryFolder", "/Users/Francesco/IdeaProjects/oop-project/src/test/resources/test-songs");
        library = new Library(props, true);
        assertEquals(55, props.size());
        assertEquals(54, library.getLibrarySize());
        assertEquals(0, library.getNumberOfPlaylists());
        //assertEquals(2, library.getNumberOfAlbums());
    }

    @Test
    void getSong() {
    }

    @Test
    void addSongFile() {
    }

    @Test
    void addNewSong() {
    }

    @Test
    void deleteSong() {
    }
}