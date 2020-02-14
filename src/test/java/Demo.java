import com.dansoftware.pdfdisplayer.PDFDisplayer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

        primaryStage.setScene(new Scene(displayer.toNode()));
        primaryStage.show();

        displayer.displayPdf(new URL("https://www.tutorialspoint.com/jdbc/jdbc_tutorial.pdf"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
