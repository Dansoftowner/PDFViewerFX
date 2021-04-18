package com.dansoftware.pdfdisplayer;

public enum PdfJSVersion {
    _2_2_228("/pdfjs_2.2.228/web/viewer.html"),
    _2_7_570("/pdfjs_2.7.570/web/viewer.html");

    private final String home;

    PdfJSVersion(String home) {
        this.home = home;
    }

    public String getHome() {
        return home;
    }

    public static PdfJSVersion latest() {
        return _2_7_570;
    }
}
