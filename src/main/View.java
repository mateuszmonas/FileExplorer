package main;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

class View{

    private VBox filesPane;
    private BorderPane borderPane;
    private Stage primaryStage;
    private Controller controller;

    View(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    void setController(Controller controller) {
        this.controller = controller;
    }

    void start(){
        borderPane = new BorderPane();
        filesPane = new VBox();
        borderPane.setCenter(filesPane);
        primaryStage.setTitle("FileNameEditor");
        primaryStage.setScene(new Scene(borderPane, 300, 275));
        primaryStage.show();
    }

    private void changeDirectory(String path){
        controller.setPath(path);
    }

    private void createMenu(){
        HBox menu = new HBox();
        Button button1 = new Button();
        button1.setStyle("-fx-pref-height: 28px");
        button1.setStyle("-fx-pref-width: 28px");
        Button button2 = new Button();
        button2.setStyle("-fx-pref-height: 28px");
        button2.setStyle("-fx-pref-width: 28px");
        menu.getChildren().add(button1);
        menu.getChildren().add(button2);
        borderPane.setTop(menu);
    }

    /**
     * creates input field for the filesystem path
     * @param path current filesystem path
     */
    private void createPathField(String path){
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

    /**
     * displays the names of all the files in the given path
     * @param files array of files to display
     * @param path  filesystem path
     */
    void showFiles(File[] files, String path){
        filesPane.getChildren().clear();
        createMenu();
        createPathField(path);
        if(files!=null) {
            int i = 0;
            for (File file : files) {
                Label label = new Label();
                label.setText(file.getName());
                label.setOnMouseClicked(event -> {
                    if (file.isDirectory()) changeDirectory(file.getPath());
                });
                filesPane.getChildren().add(label);
                i++;
            }
        } else {
            Label label = new Label();
            label.setText("directory does not exist");
            filesPane.getChildren().add(label);
        }
    }

}
