package com.oopproject;

public class Album extends Playlist  {
    private String artist = null;
    private int year = 0;

    public Album(String name, Library library) {
        super(name, library);
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public void addSong(int index) {
        if (getArtist() == null)  {
            setArtist(Song.getArtist(library.getSong(index)));
        }

         if (getYear() == 0)    {
             setYear(Song.getYear(library.getSong(index)));
         }

        songs.stream()
                .mapToInt(song -> song)
                .filter(song ->
                        Song.getTrack(library.getSong(song)) >
                                Song.getTrack(library.getSong(index)))
                                .findFirst().ifPresent(song -> songs.add(songs.indexOf(song), index));
    }
}
