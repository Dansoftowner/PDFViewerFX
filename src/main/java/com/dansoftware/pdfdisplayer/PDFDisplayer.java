package com.dansoftware.pdfdisplayer;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.Parent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.io.*;
import java.net.URL;
import java.util.Base64;

public class PDFDisplayer {

    private boolean pdfJsLoaded;

    private ProcessListener processListener;

    private WebView nodeValue;
    private String loadScript;
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
                    if (!pdfJsLoaded) loadScript = js;
                }
            });

        } finally {
            inputStream.close();
        }

    }


    @SuppressWarnings("all")
    public void setSecondaryToolbarToggleVisibility(boolean value) {
        setVisibilityOf("secondaryToolbarToggle", value);

        String js;
        if (value){
            js = new StringBuilder()
                    .append("var element = document.getElementsByClassName('verticalToolbarSeparator')[0];")
                    .append("element.style.display = 'inherit';")
                    .append("element.style.visibility = 'inherit';")
                    .toString();
        } else {
            js = new StringBuilder()
                    .append("var element = document.getElementsByClassName('verticalToolbarSeparator')[0];")
                    .append("element.style.display = 'none';")
                    .append("element.style.visibility = 'hidden';")
                    .toString();
        }

        try {
            nodeValue.getEngine().executeScript(js);
        } catch (Exception ex){
            if (!pdfJsLoaded) toExecuteWhenPDFJSLoaded += js;
        }
    }

    @SuppressWarnings("all")
    public void setVisibilityOf(String id, boolean value){
        String css;
        if (value) {
            css = new StringBuilder()
                    .append("document.getElementById('" + id + "').style.display = 'inherit';")
                    .append("document.getElementById('" + id + "').style.visibility = 'inherit';")
                    .toString();
        } else {
            css = new StringBuilder()
                    .append("document.getElementById('" + id + "').style.display = 'none';")
                    .append("document.getElementById('" + id + "').style.visibility = 'hidden';")
                    .toString();
        }

        try {
            nodeValue.getEngine().executeScript(css);
        } catch (Exception ex) {
            if (!pdfJsLoaded) this.toExecuteWhenPDFJSLoaded += css;
        }
    }

    public int getActualPageNumber(){
        try {
            return (int) nodeValue.getEngine().executeScript("PDFViewerApplication.page;");
        } catch (Exception e) {
            return 0;
        }
    }

    public int getTotalPageCount(){
        try {
            return (int) nodeValue.getEngine().executeScript("PDFViewerApplication.pagesCount;");
        } catch (Exception e) {
            return 0;
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

    public void executeScript(String js) {
        try {
            this.nodeValue.getEngine().executeScript(js);
        } catch (Exception ex) {
            if (!pdfJsLoaded) toExecuteWhenPDFJSLoaded += String.format("%s;", js);
        }
    }

    private void updateProcessListener(boolean val) {
        if (processListener != null && pdfJsLoaded) processListener.listen(val);
    }

    private WebView createWebView() {
        WebView webView = new WebView();
        webView.setContextMenuEnabled(false);
        webView.getStylesheets().add("/com/dansoftware/pdfdisplayer/base.css");

        WebEngine engine = webView.getEngine();
        String url = getClass().getResource("/pdfjs/web/viewer.html").toExternalForm();

        engine.setJavaScriptEnabled(true);
        engine.load(url);

        if (processListener != null) processListener.listen(false);


        engine.getLoadWorker()
                .stateProperty()
                .addListener(
                        new ChangeListener<Worker.State>() {
                            @Override
                            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                                JSObject window = (JSObject) engine.executeScript("window");
                                window.setMember("java", new JSLogListener());
                                engine.executeScript("console.log = function(message){ try {java.log(message);} catch(e) {} };");

                                if (newValue == Worker.State.SUCCEEDED) {
                                    try {
                                        if (processListener != null) processListener.listen(pdfJsLoaded = true);

                                        if (loadScript != null)
                                            engine.executeScript(loadScript);

                                        engine.executeScript(toExecuteWhenPDFJSLoaded);
                                        toExecuteWhenPDFJSLoaded = null;
                                        observable.removeListener(this);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        });

        return webView;

    }



    public Parent toNode() {
        if (nodeValue == null)
            return nodeValue = createWebView();
        else
            return nodeValue;
    }
}
