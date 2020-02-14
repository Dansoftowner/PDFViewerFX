package com.dansoftware.pdfdisplayer;

import javafx.concurrent.Worker;
import javafx.scene.Parent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

public class PDFDisplayer {


    private WebView nodeValue;
    private String toExecuteWhenPDFJSLoaded = "";


    public PDFDisplayer(){
    }

    public PDFDisplayer(File file) throws IOException {
        displayPdf(file);
    }

    public PDFDisplayer(URL url) throws IOException {
        displayPdf(url);
    }

    public PDFDisplayer(InputStream inputStream) throws IOException {
        displayPdf(inputStream);
    }


    public void displayPdf(File file) throws IOException {
        displayPdf(new BufferedInputStream(new FileInputStream(file)));
    }

    public void displayPdf(URL url) throws IOException {
        displayPdf(new BufferedInputStream(url.openConnection().getInputStream()));
    }

    public void displayPdf(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];

            int actualByteCount;
            while ((actualByteCount = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, actualByteCount);
            }

            byte[] data = outputStream.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(data);
            // call JS function from Java code
            String js = "openFileFromBase64('" + base64 + "');";

            try {
                nodeValue.getEngine().executeScript(js);
            } catch (Exception ex){
                toExecuteWhenPDFJSLoaded += js;
            }

        } finally {
             if(inputStream != null) inputStream.close();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(10);
        byte[] arr = byteArrayOutputStream.toByteArray();
    }


    public void navigateByPage(int pageNum){
        String jsCommand = "goToPage(" + pageNum + ");";
        try {
            nodeValue.getEngine().executeScript(jsCommand);
        } catch (Exception ex){
            toExecuteWhenPDFJSLoaded += jsCommand;
        }
    }

    private WebView createWebView(){
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        String url = getClass().getResource("/pdfjs/web/viewer.html").toExternalForm();

        engine.setJavaScriptEnabled(true);
        engine.load(url);


        engine.getLoadWorker()
                .stateProperty()
                .addListener((observable, oldValue, newValue) -> {
                    JSObject window = (JSObject) engine.executeScript("window");
                    window.setMember("java", new JSLogListener());
                    engine.executeScript("console.log = function(message){ java.log(message); };");

                    if (newValue == Worker.State.SUCCEEDED) {
                        try {
                            engine.executeScript(toExecuteWhenPDFJSLoaded);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        return webView;

    }



    public Parent toNode(){
        if (nodeValue == null)
            return nodeValue = createWebView();
        else
            return nodeValue;
    }
}
