package com.dansoftware.pdfdisplayer;

import java.io.IOException;
import java.io.OutputStream;

public class JSLogListener {
    private static OutputStream out = System.out;

    JSLogListener(){
    }

    public void log(String message) throws IOException {
        if (message != null && out != null)
           out.write((message + "\n").getBytes());
    }

    public static void setOutputStream(OutputStream outputStream){
        out = outputStream;
    }
}
