package com.dansoftware.pdfdisplayer;

@FunctionalInterface
public interface ProcessListener {
    void listen(int actualProcessed, boolean finished);
}
