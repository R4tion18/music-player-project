//package com.oopproject;
//
// import javafx.scene.media.Media;
// import javafx.util.Duration;
//
// import java.util.concurrent.CopyOnWriteArrayList;
//
// public abstract class Compilation {
//     private String name;
//     private Duration totalDuration;
//     private final CopyOnWriteArrayList<Integer> songs = new CopyOnWriteArrayList<>();
//     private final Library library;
//
//     public Compilation(String name, Library library) {
//         this.name = name;
//         this.library = library;
//     }
//
//     public void addSong(int index)  {
//         songs.add(index);
//         totalDuration = totalDuration.add(getMedia(index).getDuration());
//     }
//
//     public void removeSong(int index)   {
//         songs.remove(Integer.valueOf(index));
//         totalDuration = totalDuration.subtract(getMedia(index).getDuration());
//     }
//
//     public Media getMedia(int index)    {
//         return new Media(library.getSong(index));
//     }
// }
