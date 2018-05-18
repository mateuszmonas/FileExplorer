package main;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
    @FXML private Pane drawingPane;
    private TextField[] filePaths = new TextField[2];
    private VBox[] fileLists= new VBox[2];
    private Rectangle selectionRectangle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller = new Controller(this);
        fileLists[0] = filesA;
        fileLists[1] = filesB;
        filePaths[0]= filePathA;
        filePaths[1]= filePathB;
        handleMouse();
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

    @FXML
    private void test(MouseEvent me){
        //drawingPane.getChildren().add(new Rectangle(me.getSceneX(), me.getSceneY(), 20,20));
    }

    private class Delta{
        double startX = 0;
        double startY = 0;
        double x = 0;
        double y = 0;
    }

    private void setSelectionRectangleDimensions(double startX, double startY, double width, double height){
        selectionRectangle.setX(startX);
        selectionRectangle.setY(startY);
        selectionRectangle.setWidth(width);
        selectionRectangle.setHeight(height);
    }

    private void handleMouse(){
        final Delta dragDelta = new Delta();
        selectionRectangle = new Rectangle();
        selectionRectangle.setOpacity(0.5);
        selectionRectangle.setFill(Color.LIGHTBLUE);
        filesA.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dragDelta.startX = event.getSceneX();
                dragDelta.startY = event.getSceneY();
            }
        });
        filesA.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ObservableList<Node> nodes = drawingPane.getChildren();
                nodes.remove(selectionRectangle);
                dragDelta.x=event.getSceneX();
                dragDelta.y=event.getSceneY();
                if(dragDelta.x>dragDelta.startX && dragDelta.y>dragDelta.startY){
                    setSelectionRectangleDimensions(dragDelta.startX,dragDelta.startY, dragDelta.x-dragDelta.startX, dragDelta.y-dragDelta.startY);
                    nodes.add(selectionRectangle);
                }else if (dragDelta.x>dragDelta.startX && dragDelta.y<dragDelta.startY){
                    setSelectionRectangleDimensions(dragDelta.startX,dragDelta.y, dragDelta.x-dragDelta.startX, dragDelta.startY-dragDelta.y);
                    nodes.add(selectionRectangle);
                }else if (dragDelta.x<dragDelta.startX && dragDelta.y>dragDelta.startY){
                    setSelectionRectangleDimensions(dragDelta.x,dragDelta.startY, dragDelta.startX-dragDelta.x, dragDelta.y-dragDelta.startY);
                    nodes.add(selectionRectangle);
                }else if (dragDelta.x<dragDelta.startX && dragDelta.y<dragDelta.startY){
                    setSelectionRectangleDimensions(dragDelta.x,dragDelta.y, dragDelta.startX-dragDelta.x, dragDelta.startY-dragDelta.y);
                    nodes.add(selectionRectangle);
                }
            }
        });
        filesA.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                drawingPane.getChildren().remove(selectionRectangle);
            }
        });
    }

    private void viewFiles(File[] files, int whichList){
        for (File file : files) {
            FileLabel label = new FileLabel();
            label.setText(file.getName());
            label.setOnMouseClicked(event -> {
                ObservableList<Node> nodes = label.getParent().getChildrenUnmodifiable();
                if(event.isShiftDown()) {

                    int clickedItemPosition = 0;
                    int firstSelectedItemPosition = 0;
                    int lastSelectedItemPosition = nodes.size()-1;
                    //get clicked item position
                    for (Node n : nodes) {
                        if (label.equals(n)) {
                            break;
                        }
                        clickedItemPosition++;
                    }
                    //get first selected item position
                    for (Node n : nodes) {
                        if (n instanceof FileLabel && ((FileLabel)n).isSelected()) {
                            break;
                        }
                        firstSelectedItemPosition++;
                    }
                    //get last selected item position
                    while (lastSelectedItemPosition>firstSelectedItemPosition){
                        if (nodes.get(lastSelectedItemPosition) instanceof FileLabel && ((FileLabel)nodes.get(lastSelectedItemPosition)).isSelected()) {
                            break;
                        }
                        lastSelectedItemPosition--;
                    }
                    //if control is not pressed down then clear all selections
                    if(!event.isControlDown()) {
                        nodes.forEach(n -> {
                            if (n instanceof FileLabel && !label.equals(n)) ((FileLabel) n).setSelected(false);
                        });
                    }
                    //if clicked item is higher than both last and first selected item
                    //then select all items between clicked item and last selected item
                    if (clickedItemPosition < lastSelectedItemPosition && clickedItemPosition < firstSelectedItemPosition) {
                        for (int i = clickedItemPosition; i <= lastSelectedItemPosition; i++) {
                            ((FileLabel) nodes.get(i)).setSelected(true);
                        }
                    }
                    //if clicked item is lower than first selected item
                    //then select all items between clicked item and first selected item
                    else{
                        for (int i = firstSelectedItemPosition; i <= clickedItemPosition; i++) {
                            ((FileLabel) nodes.get(i)).setSelected(true);
                        }
                    }

                }else if(event.isControlDown()){
                    label.setSelected(!label.isSelected());
                }else {
                    if(label.isSelected()) changeDirectory(file.getPath(), whichList);
                    else{
                        nodes.forEach(n -> {
                            if (n instanceof FileLabel && !label.equals(n)) ((FileLabel) n).setSelected(false);
                        });
                        label.setSelected(true);
                    }
                }
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
