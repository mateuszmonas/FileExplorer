package main;

import com.sun.istack.internal.NotNull;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.File;

class View{

    private GridPane pane;
    private Stage primaryStage;
    private Controller controller;

    View(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    void setController(Controller controller) {
        this.controller = controller;
    }

    void start(){
        pane = new GridPane();
        pane.setGridLinesVisible(true);
        pane.setGridLinesVisible(true);
        primaryStage.setTitle("FileNameEditor");
        primaryStage.setScene(new Scene(pane, 300, 275));
        primaryStage.show();
    }

    private void changeDirectory(String path){
        controller.setPath(path);
    }

    private void createPathField(String path){
        TextField textField = new TextField();
        textField.setText(path);
        textField.setEditable(false);
        textField.setStyle("-fx-opacity: 1.0;");
        textField.setOnMouseClicked(event -> textField.setEditable(true));
        textField.setOnKeyPressed(event -> {
            if(event.getCode()== KeyCode.ENTER){
                changeDirectory(textField.getText());
            }
        });
        pane.add(textField, 0,0,2,1);
    }

    void showFiles(@NotNull File[] files, String path){
        pane.getChildren().clear();
        createPathField(path);
        if(files!=null) {
            int i = 0;
            for (File file : files) {
                Label label = new Label();
                label.setText(file.getName());
                label.setOnMouseClicked(event -> {
                    if (file.isDirectory()) changeDirectory(file.getPath());
                });
                pane.add(label, 0, i + 1);
                i++;
            }
        } else {
            Label label = new Label();
            label.setText("directory does not exist");
            pane.add(label,0,1);
        }
    }

}
