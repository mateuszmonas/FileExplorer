package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.net.URL;
import java.util.ResourceBundle;

public class View implements Initializable {

    private Stage primaryStage;
    private Controller controller;
    @FXML private VBox cur_files;
    @FXML VBox menu;
    @FXML Button a;

    @FXML protected void test(ActionEvent event) {
        System.out.println("lollolol");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller = new Controller(this);
        controller.start();
    }

    private void changeDirectory(String path){
    }

    private void editFiles(File[] files){
        for (File file : files) {
            TextField textField = new TextField();
            textField.setText(file.getName());
            textField.setStyle("-fx-padding: 0 0 0 0;");
            cur_files.getChildren().add(textField);
        }
    }


    private void viewFiles(File[] files){
        for (File file : files) {
            Text label = new Text();
            label.setText(file.getName());
            label.setOnMouseClicked(event -> {
                if (file.isDirectory()) changeDirectory(file.getPath());
            });
            cur_files.getChildren().add(label);
        }
    }

    /**
     * displays the names of all the files in the given path
     * @param files array of files to display
     */
    void displayFiles(File[] files){
        if(files!=null) {
            viewFiles(files);
        } else {
            Label label = new Label();
            label.setText("directory does not exist");
            cur_files.getChildren().add(label);
        }
    }

}
