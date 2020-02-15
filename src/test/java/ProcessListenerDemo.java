import com.dansoftware.pdfdisplayer.PDFDisplayer;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ProcessListenerDemo extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        //create a progressBar
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(500);
        progressBar.setProgress(0);

        //create a pdfDisplayer
        PDFDisplayer pdfDisplayer = new PDFDisplayer();
        //set the process listener of it
        pdfDisplayer.setProcessListener(finished ->
            Platform.runLater(() -> {
                if (finished) {
                    //when the process finished
                    progressBar.setProgress(0);
                } else {
                    //when the process started
                    progressBar.setProgress(Timeline.INDEFINITE);
                }
            })
        );

        //create a btn for loading the pdf
        Button loaderBtn = new Button("Load");
        loaderBtn.setOnAction(e ->
            //start a new thread for load the pdf document
            new Thread(() -> {
                try {
                    pdfDisplayer.displayPdf(new URL("https://www.tutorialspoint.com/javafx/javafx_tutorial.pdf"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start()
        );

        primaryStage.setScene(new Scene(new VBox(new StackPane(loaderBtn), pdfDisplayer.toNode(), new StackPane(progressBar))));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
