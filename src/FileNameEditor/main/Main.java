package FileNameEditor.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

// TODO: 2018-05-22 creating new files
public class Main extends Application{

    @Override
    public void start(Stage stage) throws IOException {
        StackPane root = FXMLLoader.load(getClass().getResource("../layout/fxml_layout.fxml"));
        stage.setTitle("FileNameEditor");
        stage.setScene(new Scene(root, 690, 490));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

