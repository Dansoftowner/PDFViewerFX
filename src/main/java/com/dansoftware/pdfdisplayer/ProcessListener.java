package com.dansoftware.pdfdisplayer;

@FunctionalInterface
public interface ProcessListener {
    void listen(boolean finished);
}
