import com.dansoftware.pdfdisplayer.JSLogListener;
import com.dansoftware.pdfdisplayer.PDFDisplayer;
import com.dansoftware.pdfdisplayer.ProcessListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class Demo extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        PDFDisplayer displayer = new PDFDisplayer();

        Stage alertStage = new Stage();
        alertStage.setScene(new Scene(new StackPane(new Label("Processing"))));
        alertStage.setAlwaysOnTop(true);
        alertStage.show();
        ProcessListener prl = (finished) -> {
            Platform.runLater(() -> {
                if (finished)
                    alertStage.close();
                else if (!alertStage.isShowing())
                    alertStage.show();
            });

        };

        displayer.setProcessListener(prl);

        Button btn = new Button("Load");
        btn.setOnAction(e -> {
            new Thread(() -> {
                try {
                    displayer.displayPdf(new URL("https://www.tutorialspoint.com/jdbc/jdbc_tutorial.pdf"));

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        primaryStage.setScene(new Scene(new VBox(btn, displayer.toNode())));
        primaryStage.show();

        new Thread(() -> {
            try {
                displayer.displayPdf(new URL("https://www.tutorialspoint.com/jdbc/jdbc_tutorial.pdf"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        displayer.setSecondaryToolbarToggleVisibility(true);
        displayer.getStylesSheets().add("style.css");

        displayer.executeScript("document.getElementById('secondaryToolbarToggle').style.backgroundColor = 'blue';");

        ///JSLogListener.setOutputStream(System.err);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
