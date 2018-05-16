package main;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;

class View{

    private VBox filesPane;
    private BorderPane borderPane;
    private Stage primaryStage;
    private Controller controller;
    private boolean editingFiles = false;
    private String path = "D:";

    View(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    void setController(Controller controller) {
        this.controller = controller;
    }

    void start(){
        ScrollPane sp = new ScrollPane();
        borderPane = new BorderPane();
        filesPane = new VBox();
        createMenu();
        sp.setContent(filesPane);
        borderPane.setCenter(sp);
        primaryStage.setTitle("FileNameEditor");
        primaryStage.setScene(new Scene(borderPane, 300, 200));
        primaryStage.show();
    }

    private void changeDirectory(String path){
        controller.setPath(path);
        controller.getFiles();
    }

    private void createMenu(){
        HBox menu = new HBox();
        VBox vBox = new VBox();
        vBox.getChildren().add(menu);
        TextField textField = new TextField();
        textField.setText(path);
        textField.setEditable(false);
        //make text color in the disable TextField black
        textField.setStyle("-fx-opacity: 1.0;");
        textField.setOnMouseClicked(event -> textField.setEditable(true));
        textField.setOnKeyPressed(event -> {
            if(event.getCode()== KeyCode.ENTER){
                changeDirectory(textField.getText());
            }
        });
        vBox.getChildren().add(textField);
        Button backButton = new Button();
        backButton.setStyle("-fx-pref-height: 28px");
        backButton.setStyle("-fx-pref-width: 28px");
        menu.getChildren().add(backButton);
        Button editAllButton = new Button();
        editAllButton.setStyle("-fx-pref-height: 28px");
        editAllButton.setStyle("-fx-pref-width: 28px");
        editAllButton.setOnMouseClicked(event -> {
            editingFiles=!editingFiles;
            controller.getFiles();
        });
        menu.getChildren().add(editAllButton);
        borderPane.setTop(vBox);
    }

    /**
     * creates input field for the filesystem path
     * @param path current filesystem path
     */
    private void createPathField(){
        TextField textField = new TextField();
        textField.setText(path);
        textField.setEditable(false);
        //make text color in the disable TextField black
        textField.setStyle("-fx-opacity: 1.0;");
        textField.setOnMouseClicked(event -> textField.setEditable(true));
        textField.setOnKeyPressed(event -> {
            if(event.getCode()== KeyCode.ENTER){
                changeDirectory(textField.getText());
            }
        });
        filesPane.getChildren().add(textField);
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
