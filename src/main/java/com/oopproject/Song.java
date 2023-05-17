package com.oopproject;

import javafx.scene.media.Media;

public class Song {
    public static String getIndex(String source)    {
        Media tmp = new Media(source);
        return tmp.getMetadata().get("Comment-0").toString();
    }

    public static void setIndex(String source, Integer index)    {
        Media tmp = new Media(source);
        //tmp.getMetadata().put("Comment-0", index.toString()); doesn't work, research library
    }
}
