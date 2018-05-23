package FileNameEditor.main;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import FileNameEditor.nodes.FileLabelSelectable;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class View implements Initializable, ViewContract.View{

    @FXML private ScrollPane scrollPaneA;
    @FXML private ScrollPane scrollPaneB;
    private ViewContract.Presenter controller;
    @FXML private VBox fileListA;
    @FXML private VBox fileListB;
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
        fileLists[0] = fileListA;
        fileLists[1] = fileListB;
        filePaths[0]= filePathA;
        filePaths[1]= filePathB;
        SelectionRectangleHelper helper = new SelectionRectangleHelper(drawingPane, fileLists);
        helper.handleSelectionRectangle(fileLists[0], move);
        helper.handleSelectionRectangle(fileLists[1], move);
        handleKeyEvents(scrollPaneA, 0);
        handleKeyEvents(scrollPaneB, 1);
        filePathA.setOnKeyPressed(event -> { if(event.getCode()==KeyCode.ENTER){
            changeDirectory(filePaths[0].getText(), 0);
            fileLists[0].requestFocus();
        } });
        filePathB.setOnKeyPressed(event -> { if(event.getCode()==KeyCode.ENTER) {
            changeDirectory(filePaths[1].getText(), 1);
            fileLists[1].requestFocus();
        } });
        copyFilesA.setOnMouseClicked(event -> copy.copyFilesToClipboardEvent(0));
        copyFilesB.setOnMouseClicked(event -> copy.copyFilesToClipboardEvent(1));
        controller.start();
    }

    private void handleKeyEvents(Control pane, int whichList){
        pane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN).match(event)) {
                copy.copyFilesToClipboardEvent(whichList);
            }
            else if (new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN).match(event)) {
                paste.pasteFilesFromClipboardEvent(whichList);
            }
            else if (new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN).match(event)) {
                cut.cutFilesEvent(whichList);
            }
            else if (new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN).match(event)) {
                fileLists[whichList].getChildrenUnmodifiable().forEach(node -> {
                    if(node instanceof FileLabelSelectable) ((FileLabelSelectable) node).setSelected(true);
                });
            }
            else if (new KeyCodeCombination(KeyCode.DELETE, KeyCombination.SHIFT_DOWN).match(event)){
                moveToTrash.moveFilesToTrash(whichList);
            }
            else if (event.getCode()==KeyCode.DELETE){
                delete.deleteFilesEvent(whichList);
            }
        });
    }

    private FileEventHelper.DeleteFilesEvent delete = whichList -> controller.deleteFiles(fileLists[whichList].getChildrenUnmodifiable().stream()
            .filter(file -> file instanceof FileLabelSelectable && ((FileLabelSelectable) file).isSelected())
            .map(file -> ((FileLabelSelectable) file).getFile()).collect(Collectors.toList()));

    private FileEventHelper.MoveFilesToTrash moveToTrash = whichList -> controller.deleteFiles(
            fileLists[whichList].getChildrenUnmodifiable().stream()
                    .filter(file -> file instanceof FileLabelSelectable && ((FileLabelSelectable) file).isSelected())
                    .map(file -> ((FileLabelSelectable) file).getFile()).collect(Collectors.toList())
    );

    private FileEventHelper.PasteFilesFromClipboardEvent paste = whichList -> controller.pasteFilesFromClipboard(whichList);

    private FileEventHelper.CutFilesEvent cut = whichList -> {};

    private FileEventHelper.MoveFilesEvent move = new FileEventHelper.MoveFilesEvent() {
        @Override
        public void moveFilesEvent(List<File> files, String path) {
            controller.moveFiles(files, path);
        }

        @Override
        public void moveFilesEvent(List<File> files, int whichList) {
            controller.moveFiles(files, whichList);
        }
    };

    private FileEventHelper.CopyFilesToCpilboardEvent copy = whichList ->  {
            List<File> files = fileLists[whichList].getChildrenUnmodifiable().stream().filter(node ->
                    node instanceof FileLabelSelectable && ((FileLabelSelectable) node).isSelected()
            ).map(node -> ((FileLabelSelectable)node).getFile()
            ).collect(Collectors.toList());
            controller.copyFilesToClipboard(files);
    };

    @Override
    public void displayPath(String path, int whichList){
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
            fileListA.getChildren().add(textField);
        }
    }

    private void createLabelContextMenu(Label label, int whichList){
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem cutContextItem = new MenuItem("Cut");
        cutContextItem.setOnAction(event -> cut.cutFilesEvent(0));
        MenuItem copyContextItem = new MenuItem("Copy");
        copyContextItem.setOnAction(event -> copy.copyFilesToClipboardEvent(whichList));
        MenuItem pasteContextItem = new MenuItem("Paste");
        pasteContextItem.setOnAction(event -> paste.pasteFilesFromClipboardEvent(whichList));
        MenuItem deleteContextItem = new MenuItem("Delete");
        deleteContextItem.setOnAction(event -> moveToTrash.moveFilesToTrash(whichList));
        MenuItem renameContextItem = new MenuItem("Rename");
        renameContextItem.setOnAction(event -> {});
        Menu newItemMenu = new Menu("New");

        MenuItem folder = new MenuItem("folder");
        newItemMenu.getItems().addAll(folder);

        MenuItem txt = new MenuItem(".txt");
        newItemMenu.getItems().addAll(txt);

        contextMenu.getItems().addAll(cutContextItem, copyContextItem, pasteContextItem, deleteContextItem, renameContextItem, newItemMenu);
        label.setContextMenu(contextMenu);
    }

    private void viewFiles(File[] files, int whichList){
        for (File file : files) {
            FileLabelSelectable label = new FileLabelSelectable(file);
            createLabelContextMenu(label, whichList);
            //class used to check if mouse position while released is same as while pressed
            final MousePosition pressedMousePosition = MousePosition.ZERO;
            label.setOnMousePressed(event -> pressedMousePosition.setPosition(event.getSceneX(), event.getSceneY()));
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
                            if (n instanceof FileLabelSelectable && ((FileLabelSelectable) n).isSelected()) {
                                break;
                            }
                            firstSelectedItemPosition++;
                        }
                        //get last selected item position
                        while (lastSelectedItemPosition > firstSelectedItemPosition) {
                            if (nodes.get(lastSelectedItemPosition) instanceof FileLabelSelectable && ((FileLabelSelectable) nodes.get(lastSelectedItemPosition)).isSelected()) {
                                break;
                            }
                            lastSelectedItemPosition--;
                        }
                        //if control is not pressed down then clear all selections
                        if (!event.isControlDown()) {
                            nodes.forEach(n -> {
                                if (n instanceof FileLabelSelectable && !label.equals(n))
                                    ((FileLabelSelectable) n).setSelected(false);
                            });
                        }
                        //if clicked item is higher than both last and first selected item
                        //then select all items between clicked item and last selected item
                        if (clickedItemPosition < lastSelectedItemPosition && clickedItemPosition < firstSelectedItemPosition) {
                            for (int i = clickedItemPosition; i <= lastSelectedItemPosition; i++) {
                                ((FileLabelSelectable) nodes.get(i)).setSelected(true);
                            }
                        }
                        //if clicked item is lower than first selected item
                        //then select all items between clicked item and first selected item
                        else {
                            for (int i = firstSelectedItemPosition; i <= clickedItemPosition; i++) {
                                ((FileLabelSelectable) nodes.get(i)).setSelected(true);
                            }
                        }
                    } else if (event.isControlDown()) {
                        label.setSelected(!label.isSelected());
                    } else {
                        if (label.isSelected()) {
                            changeDirectory(label.getFile().getPath(), whichList);
                        } else {
                            nodes.forEach(n -> {
                                if (n instanceof FileLabelSelectable && !label.equals(n))
                                    ((FileLabelSelectable) n).setSelected(false);
                            });
                            label.setSelected(true);
                        }
                    }
                }
            });
            fileLists[whichList].addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
                if (event.getPickResult().getIntersectedNode().equals(label) && label.isSelected()) {
                    label.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
                }
            });
            label.setOnMouseEntered(event -> label.onMouseHoverEnter());
            label.setOnMouseExited(event -> label.onMouseHoverLeave());
            fileLists[whichList].getChildren().add(label);
        }
    }


    /**
     * displays the names of all the files in the given path
     * @param files array of files to display
     */
    @Override
    public void displayFiles(File[] files, int whichList){
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
