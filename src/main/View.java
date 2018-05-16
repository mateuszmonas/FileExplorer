package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class View extends Application {

    private VBox filesPane;
    private BorderPane borderPane;
    private Stage primaryStage;
    private Controller controller;
    private String path = "D:";

    View(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void start(Stage stage) throws IOException{
        BorderPane root = FXMLLoader.load(getClass().getResource("fxml_layout.fxml"));
        primaryStage.setTitle("FileNameEditor");
        primaryStage.setScene(new Scene(root, 300, 200));
        primaryStage.show();
    }

    private void changeDirectory(String path){
        controller.setPath(path);
        controller.getFiles();
    }

    private void editFiles(File[] files){
        for (File file : files) {
            TextField textField = new TextField();
            textField.setText(file.getName());
            textField.setStyle("-fx-padding: 0 0 0 0;");
            filesPane.getChildren().add(textField);
        }
    }


    private void viewFiles(File[] files){
        for (File file : files) {
            Text label = new Text();
            label.setText(file.getName());
            label.setOnMouseClicked(event -> {
                if (file.isDirectory()) changeDirectory(file.getPath());
            });
            filesPane.getChildren().add(label);
        }
    }

    /**
     * displays the names of all the files in the given path
     * @param files array of files to display
     */
    void displayFiles(File[] files){
        filesPane.getChildren().clear();
        if(files!=null) {
            if(editingFiles) editFiles(files);
            else viewFiles(files);
        } else {
            Label label = new Label();
            label.setText("directory does not exist");
            filesPane.getChildren().add(label);
        }
    }

}
