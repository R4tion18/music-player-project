package com.oopproject;

public class Album extends Playlist  {
    private String artist;

    public Album(String artist, String name, Library library) {
        super(name, library);
        this.artist = artist;
    }

    @Override
    public void addSong(int index) {
        
    }
}
