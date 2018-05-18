package main;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class View implements Initializable {

    @FXML private ScrollPane scrollPaneA;
    @FXML private ScrollPane scrollPaneB;
    private Controller controller;
    @FXML private VBox filesA;
    @FXML private VBox filesB;
    @FXML private TextField filePathA;
    @FXML private TextField filePathB;
    private TextField[] filePaths = new TextField[2];
    private VBox[] fileLists= new VBox[2];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller = new Controller(this);
        fileLists[0] = filesA;
        fileLists[1] = filesB;
        filePaths[0]= filePathA;
        filePaths[1]= filePathB;
        controller.start();
    }

    void displayPath(String path, int whichList){
        filePaths[whichList].setText(path);
    }

    @FXML
    private void onPathChangeA(KeyEvent event){
        if (event.getCode()== KeyCode.ENTER){
            changeDirectory(filePathB.getText(), 0);
        }
    }

    @FXML
    private void onPathChangeB(KeyEvent event){
        if (event.getCode()== KeyCode.ENTER){
            changeDirectory(filePathB.getText(), 1);
        }
    }

    private void changeDirectory(String path, int whichList){
        controller.changeDirectory(path, whichList);
    }

    private void editFiles(File[] files){
        for (File file : files) {
            TextField textField = new TextField();
            textField.setText(file.getName());
            textField.setPadding(new Insets(0,0,0,0));
            filesA.getChildren().add(textField);
        }
    }

    private void viewFiles(File[] files, int whichList){
        for (File file : files) {
            Label label = new Label();
            label.setText(file.getName());
            label.setOnMouseClicked(event -> {
                //remove background from all other nodes
                label.getParent().getChildrenUnmodifiable().forEach(n -> {
                    if(n instanceof Label && !label.equals(n)) ((Label)n).setBackground(null);
                });
                //check if this node was already selected
                //if it was change path
                if(label.getBackground()!=null){
                    if (file.isDirectory()) changeDirectory(file.getPath(), whichList);
                }
                //change node background if it was not selected yet
                label.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
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
            viewFiles(files, whichList);
        } else {
            Label label = new Label();
            label.setText("directory does not exist");
            fileLists[whichList].getChildren().add(label);
        }
    }

}
