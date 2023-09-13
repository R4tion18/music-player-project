package com.oopproject;

import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * A Playlist to store and play songs.
 *
 * @see SongQueue
 */
public class Playlist {
    /**
     * The name of the Playlist.
     */
    private final String name;

    /**
     * The indexes of the songs in the Playlist.
     */
    protected CopyOnWriteArrayList<Integer> songs = new CopyOnWriteArrayList<>();

    /**
     * The Library the Playlist is stored in.
     */
    private final Library library;

    /**
     * Creates a Playlist from a given name.
     * @param name the name of the Playlist.
     * @param library the Library the Playlist is stored in.
     */
    public Playlist(String name, Library library) {
        this.name = name;
        this.library = library;
    }

    /**
     * Returns the name of the Playlist.
     * @return the name of the Playlist.
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the nameof the Playlist.
     * @param name the new name.
     */
    public void setName(String name) {
        library.createPlaylist(name);
        songs.forEach(library.getPlaylist(name)::addSong);
        songs.forEach(this::removeSong);
        library.deletePlaylist(this.name, false);
    }

    /**
     * Returns a list of the indexes of the songs.
     * @return the list of indexes.
     */
    public CopyOnWriteArrayList<Integer> getSongIndexes() {
        return songs;
    }

    /**
     * Returns a list of URI strings representing the songs.
     * @return the list of URI strings.
     */
    public CopyOnWriteArrayList<String> getSongURIs() {
        return this.songs.stream()
                .map(library::getSong)
                .sorted(Comparator.comparingInt(Song::getTrack))
                .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    }

    /**
     * Returns the Library where the Playlist is stored.
     * @return the Library.
     */
    public Library getLibrary() {
        return library;
    }

    /**
     * Adds the song with the given index to the Playlist.
     * @param index the index of the song.
     */
    public void addSong(int index)  {
        if(!songs.contains(index))   {
            songs.add(index);
        }

        getLibrary().putInPlaylist(index, getName());
    }

    /**
     * Removes the song with the given index from the Playlist.
     * @param index the index of the song.
     */
    public void removeSong(int index)   {
        songs.remove(Integer.valueOf(index));
        getLibrary().removeFromPlaylist(index, getName());
    }
}
