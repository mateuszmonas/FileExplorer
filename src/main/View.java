package main;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import nodes.SelectableFileLabel;

import java.io.File;
import java.net.URL;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller = new Controller(this);
        fileLists[0] = filesA;
        fileLists[1] = filesB;
        filePaths[0]= filePathA;
        filePaths[1]= filePathB;
        SelectionRectangleHelper.handleSelectionRectangle(fileLists[0], drawingPane);
        SelectionRectangleHelper.handleSelectionRectangle(fileLists[1], drawingPane);
        handleKeyEvents(scrollPaneA, 0);
        handleKeyEvents(scrollPaneB, 1);
        filePathA.setOnKeyPressed(event -> { if(event.getCode()==KeyCode.ENTER) changeDirectory(filePaths[0].getText(), 0); });
        filePathB.setOnKeyPressed(event -> { if(event.getCode()==KeyCode.ENTER) changeDirectory(filePaths[1].getText(), 1); });
        copyFilesA.setOnMouseClicked(event -> copyFilesEvent(0));
        copyFilesB.setOnMouseClicked(event -> copyFilesEvent(1));
        controller.start();
    }

    private void handleKeyEvents(Control pane, int whichList){
        pane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY).match(event)) {
                copyFilesEvent(whichList);
            }
            if (new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY).match(event)) {
                pasteFilesEvent(whichList);
            }
            if (new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_ANY).match(event)) {
                cutFilesEvent(whichList);
            }
            if (new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_ANY).match(event)) {
                fileLists[whichList].getChildrenUnmodifiable().forEach(node -> {
                    if(node instanceof SelectableFileLabel) ((SelectableFileLabel) node).setSelected(true);
                });
            }
        });
    }

    private void pasteFilesEvent(int whichList){
        controller.pasteFilesFromClipboard(whichList);
    }

    private void cutFilesEvent(int whichList){

    }

    private void copyFilesEvent(int whichList){
        List<File> files = fileLists[whichList].getChildrenUnmodifiable().stream().filter(node ->
            node instanceof SelectableFileLabel && ((SelectableFileLabel) node).isSelected()
        ).map(node -> ((SelectableFileLabel)node).getFile()
        ).collect(Collectors.toList());
        controller.copyFilesToClipboard(files);
    }

    void displayPath(String path, int whichList){
        filePaths[whichList].setText(path);
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
            SelectableFileLabel label = new SelectableFileLabel(file);
            label.setMinWidth(200);
            //class used to check if mouse position while released is same as while pressed
            final MousePosition pressedMousePosition = new MousePosition();
            label.setOnMousePressed(event -> pressedMousePosition.set(event.getSceneX(), event.getSceneY()));
            label.setOnMouseClicked(event -> {
                ObservableList<Node> nodes = label.getParent().getChildrenUnmodifiable();
                if(pressedMousePosition.equals(event.getSceneX(), event.getSceneY())) {
                    if (event.isShiftDown()) {
                        int clickedItemPosition = 0;
                        int firstSelectedItemPosition = 0;
                        int lastSelectedItemPosition = nodes.size() - 1;
                        //get clicked item position
                        for (Node n : nodes) {
                            if (label.equals(n)) {
                                break;
                            }
                            clickedItemPosition++;
                        }
                        //get first selected item position
                        for (Node n : nodes) {
                            if (n instanceof SelectableFileLabel && ((SelectableFileLabel) n).isSelected()) {
                                break;
                            }
                            firstSelectedItemPosition++;
                        }
                        //get last selected item position
                        while (lastSelectedItemPosition > firstSelectedItemPosition) {
                            if (nodes.get(lastSelectedItemPosition) instanceof SelectableFileLabel && ((SelectableFileLabel) nodes.get(lastSelectedItemPosition)).isSelected()) {
                                break;
                            }
                            lastSelectedItemPosition--;
                        }
                        //if control is not pressed down then clear all selections
                        if (!event.isControlDown()) {
                            nodes.forEach(n -> {
                                if (n instanceof SelectableFileLabel && !label.equals(n))
                                    ((SelectableFileLabel) n).setSelected(false);
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
                        else {
                            for (int i = firstSelectedItemPosition; i <= clickedItemPosition; i++) {
                                ((SelectableFileLabel) nodes.get(i)).setSelected(true);
                            }
                        }
                    } else if (event.isControlDown()) {
                        label.setSelected(!label.isSelected());
                    } else {
                        if (label.isSelected()) {
                            changeDirectory(label.getFile().getPath(), whichList);
                        } else {
                            nodes.forEach(n -> {
                                if (n instanceof SelectableFileLabel && !label.equals(n))
                                    ((SelectableFileLabel) n).setSelected(false);
                            });
                            label.setSelected(true);
                        }
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
