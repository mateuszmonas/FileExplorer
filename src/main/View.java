package main;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nodes.SelectableFileLabel;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class View implements Initializable {

    @FXML private ScrollPane scrollPaneA;
    @FXML private ScrollPane scrollPaneB;
    private Controller controller;
    @FXML private VBox filesA;
    @FXML private VBox filesB;
    @FXML private TextField filePathA;
    @FXML private TextField filePathB;
    @FXML private Button copyFilesA;
    @FXML private Button copyFilesB;
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
        handleMouse(fileLists[0]);
        handleMouse(fileLists[1]);
        copyFilesA.setOnMouseClicked(event -> copyFilesButtonClicked(0));
        copyFilesB.setOnMouseClicked(event -> copyFilesButtonClicked(1));
        controller.start();
    }

    private void copyFilesButtonClicked(int whichList){
        List<File> files = fileLists[whichList].getChildrenUnmodifiable().stream().filter(node ->
            node instanceof SelectableFileLabel && ((SelectableFileLabel) node).isSelected()
        ).map(node -> ((SelectableFileLabel)node).getFile()
        ).collect(Collectors.toList());
        controller.copyFilesToClipboard(files);
    }

    void displayPath(String path, int whichList){
        filePaths[whichList].setText(path);
    }

    @FXML
    private void onPathChangeA(KeyEvent event){
        if (event.getCode()== KeyCode.ENTER){
            changeDirectory(filePathA.getText(), 0);
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

    //function responsible for drawing a selection rectangle and selecting files with it
    private void handleMouse(Pane pane){
        final Delta dragDelta = new Delta();
        selectionRectangle = new Rectangle();
        selectionRectangle.setOpacity(0.5);
        selectionRectangle.setFill(Color.BLUE);
        final ArrayList<Node> nodesSelectedBeforeDrawing = new ArrayList<>();
        pane.setOnMousePressed(event->{
            //if control is pressed don't remove the selection from previously selected nodes
            if (event.isControlDown()) {
                nodesSelectedBeforeDrawing.clear();
                nodesSelectedBeforeDrawing.addAll(pane.getChildrenUnmodifiable().stream().filter(node -> node instanceof SelectableFileLabel && ((SelectableFileLabel) node).isSelected()).collect(Collectors.toList()));
            } else {
                pane.getChildrenUnmodifiable().forEach(node -> {
                    if(node instanceof SelectableFileLabel && !((SelectableFileLabel)node).areCoordinatesInsideThenode(event.getSceneX(), event.getSceneY()))
                        ((SelectableFileLabel)node).setSelected(false);
                });
            }
            dragDelta.startX = event.getSceneX();
            dragDelta.startY = event.getSceneY();
        });
        pane.setOnMouseDragged(event-> {
            ObservableList<Node> nodes = drawingPane.getChildren();
            nodes.remove(selectionRectangle);
            dragDelta.x=event.getSceneX();
            dragDelta.y=event.getSceneY();
            //we have to calculate where the current position of the cursor
            //is relative to where it was clicked
            if(dragDelta.x>dragDelta.startX && dragDelta.y>dragDelta.startY){
                setSelectionRectangleDimensions(dragDelta.startX,dragDelta.startY, dragDelta.x-dragDelta.startX, dragDelta.y-dragDelta.startY);
            }else if (dragDelta.x>dragDelta.startX && dragDelta.y<dragDelta.startY){
                setSelectionRectangleDimensions(dragDelta.startX,dragDelta.y, dragDelta.x-dragDelta.startX, dragDelta.startY-dragDelta.y);
            }else if (dragDelta.x<dragDelta.startX && dragDelta.y>dragDelta.startY){
                setSelectionRectangleDimensions(dragDelta.x,dragDelta.startY, dragDelta.startX-dragDelta.x, dragDelta.y-dragDelta.startY);
            }else if (dragDelta.x<dragDelta.startX && dragDelta.y<dragDelta.startY){
                setSelectionRectangleDimensions(dragDelta.x,dragDelta.y, dragDelta.startX-dragDelta.x, dragDelta.startY-dragDelta.y);
            }
            nodes.add(selectionRectangle);
            pane.getChildrenUnmodifiable().forEach(node -> {
                if(node instanceof SelectableFileLabel) {
                    Bounds nodeBounds = node.localToScene(node.getBoundsInLocal());
                    double nodeMinX = nodeBounds.getMinX();
                    double nodeMaxX = nodeBounds.getMaxX();
                    double nodeMaxY = nodeBounds.getMaxY();
                    double nodeMinY = nodeBounds.getMinY();
                    double selectionMinX = dragDelta.startX < dragDelta.x ? dragDelta.startX : dragDelta.x;
                    double selectionMaxX = dragDelta.startX > dragDelta.x ? dragDelta.startX : dragDelta.x;
                    double selectionMinY = dragDelta.startY < dragDelta.y ? dragDelta.startY : dragDelta.y;
                    double selectionMaxY = dragDelta.startY > dragDelta.y ? dragDelta.startY : dragDelta.y;
                    //check if node is in the selection rectangle
                    if (selectionMinY < nodeMaxY && selectionMaxY > nodeMinY && selectionMinX < nodeMaxX && selectionMaxX > nodeMinX) {
                        ((SelectableFileLabel) node).setSelected(true);
                    } else {
                        if (!nodesSelectedBeforeDrawing.contains(node)) {
                            ((SelectableFileLabel) node).setSelected(false);
                        }
                    }
                }
            });
        });
        //after the mouse is released remove the rectangle and clear its position
        pane.setOnMouseReleased(event -> {
                drawingPane.getChildren().remove(selectionRectangle);
                setSelectionRectangleDimensions(0,0,0,0);
            }
        );
    }

    private void viewFiles(File[] files, int whichList){
        for (File file : files) {
            SelectableFileLabel label = new SelectableFileLabel(file);
            label.setMinWidth(200);
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
                        if (n instanceof SelectableFileLabel && ((SelectableFileLabel)n).isSelected()) {
                            break;
                        }
                        firstSelectedItemPosition++;
                    }
                    //get last selected item position
                    while (lastSelectedItemPosition>firstSelectedItemPosition){
                        if (nodes.get(lastSelectedItemPosition) instanceof SelectableFileLabel && ((SelectableFileLabel)nodes.get(lastSelectedItemPosition)).isSelected()) {
                            break;
                        }
                        lastSelectedItemPosition--;
                    }
                    //if control is not pressed down then clear all selections
                    if(!event.isControlDown()) {
                        nodes.forEach(n -> {
                            if (n instanceof SelectableFileLabel && !label.equals(n)) ((SelectableFileLabel) n).setSelected(false);
                        });
                    }
                    //if clicked item is higher than both last and first selected item
                    //then select all items between clicked item and last selected item
                    if (clickedItemPosition < lastSelectedItemPosition && clickedItemPosition < firstSelectedItemPosition) {
                        for (int i = clickedItemPosition; i <= lastSelectedItemPosition; i++) {
                            ((SelectableFileLabel) nodes.get(i)).setSelected(true);
                        }
                    }
                    //if clicked item is lower than first selected item
                    //then select all items between clicked item and first selected item
                    else{
                        for (int i = firstSelectedItemPosition; i <= clickedItemPosition; i++) {
                            ((SelectableFileLabel) nodes.get(i)).setSelected(true);
                        }
                    }

                }else if(event.isControlDown()){
                    label.setSelected(!label.isSelected());
                }else {
                    if(label.isSelected()) changeDirectory(label.getFile().getPath(), whichList);
                    else{
                        nodes.forEach(n -> {
                            if (n instanceof SelectableFileLabel && !label.equals(n)) ((SelectableFileLabel) n).setSelected(false);
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
