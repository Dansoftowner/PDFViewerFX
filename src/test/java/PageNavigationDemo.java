import com.dansoftware.pdfdisplayer.PDFDisplayer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

public class PageNavigationDemo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        PDFDisplayer displayer = new PDFDisplayer(new URL("https://www.tutorialspoint.com/javafx/javafx_tutorial.pdf"));

        Button goToPage10 = new Button("Go to page 10");
        goToPage10.setOnAction(e -> {
            displayer.navigateByPage(10);
        });

        primaryStage.setScene(new Scene(new VBox(displayer.toNode(), new StackPane(goToPage10))));
        primaryStage.show();

        //
    }

    public static void main(String[] args) {
        launch(args);
    }
}
