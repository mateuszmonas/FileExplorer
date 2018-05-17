package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application{

    @Override
    public void start(Stage stage) throws IOException {
        VBox root = FXMLLoader.load(getClass().getResource("fxml_layout.fxml"));
        stage.setTitle("FileNameEditor");
        stage.setScene(new Scene(root, 690, 490));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
