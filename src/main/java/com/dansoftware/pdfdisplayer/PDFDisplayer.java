package com.dansoftware.pdfdisplayer;

import javafx.application.Platform;
import javafx.collections.ObservableList;
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

    private boolean pdfJsLoaded;

    private ProcessListener processListener;

    private WebView nodeValue;
    private String toExecuteWhenPDFJSLoaded = "";


    public PDFDisplayer() {
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

        if (inputStream == null)
            return;

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            updateProcessListener(false);

            byte[] buffer = new byte[4096];

            int actualByteCount;
            while (true) {
                try {
                    if ((actualByteCount = inputStream.read(buffer)) == -1) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                outputStream.write(buffer, 0, actualByteCount);
            }

            updateProcessListener(true);

            byte[] data = outputStream.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(data);
            // call JS function from Java code
            String js = "openFileFromBase64('" + base64 + "');";

            Platform.runLater(() -> {
                try {
                    nodeValue.getEngine().executeScript(js);
                } catch (Exception ex) {
                    if (!pdfJsLoaded) toExecuteWhenPDFJSLoaded += js;
                }
            });

        } finally {
            inputStream.close();
        }

    }

    @SuppressWarnings("all")
    public void setSecondaryToolbarToggleVisibility(boolean value) {
        String css;
        if (value){
            css = new StringBuilder()
                    .append("document.getElementById('secondaryToolbarToggle').style.display = 'inherit';")
                    .append("document.getElementById('secondaryToolbarToggle').style.visibility = 'inherit';")
                    .toString();
        } else {
            css = new StringBuilder()
                    .append("document.getElementById('secondaryToolbarToggle').style.display = 'none';")
                    .append("document.getElementById('secondaryToolbarToggle').style.visibility = 'hidden';")
                    .toString();
        }

        try {
            nodeValue.getEngine().executeScript(css);
        } catch (Exception ex){
            if (!pdfJsLoaded) this.toExecuteWhenPDFJSLoaded += css;
        }

    }

    public void navigateByPage(int pageNum) {
        String jsCommand = "goToPage(" + pageNum + ");";
        try {
            nodeValue.getEngine().executeScript(jsCommand);
        } catch (Exception ex) {
            if (!pdfJsLoaded) toExecuteWhenPDFJSLoaded += jsCommand;
        }
    }

    public void setProcessListener(ProcessListener listener) {
        this.processListener = listener;
    }

    private void updateProcessListener(boolean val) {
        if (processListener != null && pdfJsLoaded) processListener.listen(val);
    }

    private WebView createWebView() {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        String url = getClass().getResource("/pdfjs/web/viewer.html").toExternalForm();

        engine.setJavaScriptEnabled(true);
        engine.load(url);

        if (processListener != null) processListener.listen(false);
        engine.getLoadWorker()
                .stateProperty()
                .addListener((observable, oldValue, newValue) -> {
                    JSObject window = (JSObject) engine.executeScript("window");
                    window.setMember("java", new JSLogListener());
                    engine.executeScript("console.log = function(message){ try {java.log(message);} catch(e) {} };");

                    if (newValue == Worker.State.SUCCEEDED) {
                        try {
                            if (processListener != null) processListener.listen(pdfJsLoaded = true);

                            engine.executeScript(toExecuteWhenPDFJSLoaded);
                            toExecuteWhenPDFJSLoaded = null;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

        return webView;

    }

    public ObservableList<String> getStylesSheets(){
        return nodeValue.getStylesheets();
    }

    public void executeScript(String js) {
        try {
            this.nodeValue.getEngine().executeScript(js);
        } catch (Exception ex) {
            if (!pdfJsLoaded) toExecuteWhenPDFJSLoaded += String.format("%s;", js);
        }
    }

    public Parent toNode() {
        if (nodeValue == null)
            return nodeValue = createWebView();
        else
            return nodeValue;
    }
}
