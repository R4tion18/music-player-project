package com.oopproject;

import java.util.concurrent.CopyOnWriteArrayList;

public class SongQueue {
    private final CopyOnWriteArrayList<Integer> queue;
    private CopyOnWriteArrayList<Integer> order;
    private Library library;

    public SongQueue(CopyOnWriteArrayList<Integer> queue) {
        this.queue = queue;
    }

    public void addTop(int index)  {

    }

    public void addBottom(int index)    {
        order.add(order.size(), order.size());
        queue.add(queue.size(), index);
    }

    public void shuffle()   {

    }

    public void play(boolean shuffle)  {

    }
}
