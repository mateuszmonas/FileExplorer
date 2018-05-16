package main;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class View implements Initializable {

    private Controller controller;
    @FXML private VBox filesA;
    @FXML private VBox filesB;
    @FXML private TextField filePathA;
    @FXML private TextField filePathB;
    private TextField[] filePaths = new TextField[2];
    private VBox[] fileLists= new VBox[2];
    @FXML private BorderPane root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller = new Controller(this);
        fileLists[0] = filesA;
        fileLists[1] = filesB;
        filePaths[0]= filePathA;
        filePaths[1]= filePathB;
        controller.start();
    }



    private void changeDirectory(String path, int whichList){
        controller.changeDirectory(path, whichList);
    }

    private void editFiles(File[] files){
        for (File file : files) {
            TextField textField = new TextField();
            textField.setText(file.getName());
            textField.setStyle("-fx-padding: 0 0 0 0;");
            filesA.getChildren().add(textField);
        }
    }


    private void viewFiles(File[] files, int whichList){
        for (File file : files) {
            Text label = new Text();
            label.setText(file.getName());
            label.setOnMouseClicked(event -> {
                if (file.isDirectory()) changeDirectory(file.getPath(), whichList);
            });
            fileLists[whichList].getChildren().add(label);
        }
    }

    /**
     * displays the names of all the files in the given path
     * @param files array of files to display
     */
    void displayFiles(File[] files, int whichList){
        fileLists[whichList].getChildren().clear();
        if(files!=null) {
            filePaths[whichList].setText(files[0].getParent());
            viewFiles(files, whichList);
        } else {
            Label label = new Label();
            label.setText("directory does not exist");
            filesA.getChildren().add(label);
        }
    }

}
