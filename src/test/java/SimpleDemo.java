import com.dansoftware.pdfdisplayer.JSLogListener;
import com.dansoftware.pdfdisplayer.PDFDisplayer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

public class SimpleDemo extends Application {
    private boolean visible;
    @Override
    public void start(Stage primaryStage) throws Exception {

        PDFDisplayer displayer = new PDFDisplayer(new URL("https://www.tutorialspoint.com/javafx/javafx_tutorial.pdf"));
        displayer.setSecondaryToolbarToggleVisibility(visible);
        displayer.setVisibilityOf("sidebarToggle", false);

        Button btn = new Button("Hide/Show");
        btn.setOnAction(event -> {
            displayer.setSecondaryToolbarToggleVisibility(visible = !visible);
        });

        JSLogListener.setOutputStream(System.err);

        primaryStage.setScene(new Scene(new VBox(displayer.toNode(), btn)));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
