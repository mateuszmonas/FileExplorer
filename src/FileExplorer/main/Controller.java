package FileExplorer.main;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import FileExplorer.nodes.FileLabelSelectable;
import FileExplorer.nodes.FileNodeSelectable;
import FileExplorer.nodes.FileTextField;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class Controller implements Initializable, ViewContract.Controller {

    @FXML
    private ScrollPane scrollPaneA;
    @FXML
    private ScrollPane scrollPaneB;
    private ViewContract.Model model;
    @FXML
    private VBox fileListA;
    @FXML
    private VBox fileListB;
    @FXML
    private TextField filePathA;
    @FXML
    private TextField filePathB;
    @FXML
    private Button goToParentButtonA;
    @FXML
    private Button goToParentButtonB;
    @FXML
    private Button copyFilesButtonA;
    @FXML
    private Button copyFilesButtonB;
    @FXML
    private Button renameAllButtonA;
    @FXML
    private Button renameAllButtonB;
    @FXML
    private Pane drawingPane;
    private TextField[] filePaths = new TextField[2];
    private VBox[] fileLists = new VBox[2];
    private FileEventHelper fileEventHelper = new FileEventHelper() {
        @Override
        public void deleteFilesEvent(int whichList) {
            model.deleteFiles(fileLists[whichList].getChildrenUnmodifiable().stream()
                    .filter(file -> file instanceof FileLabelSelectable && ((FileLabelSelectable) file).isSelected())
                    .map(file -> ((FileLabelSelectable) file).getFile()).collect(Collectors.toList()));
        }

        @Override
        public void moveFilesToTrash(int whichList) {
            model.moveFilesToTrash(
                    fileLists[whichList].getChildrenUnmodifiable().stream()
                            .filter(file -> file instanceof FileLabelSelectable && ((FileLabelSelectable) file).isSelected())
                            .map(file -> ((FileLabelSelectable) file).getFile()).collect(Collectors.toList()));
        }

        @Override
        public void pasteFilesFromClipboardEvent(int whichList) {
            model.pasteFilesFromClipboard(whichList);
        }

        @Override
        public void cutFilesEvent(int whichList) {
            List<File> files = fileLists[whichList].getChildrenUnmodifiable().stream().filter(node ->
                    node instanceof FileLabelSelectable && ((FileLabelSelectable) node).isSelected()
            ).map(node -> ((FileLabelSelectable) node).getFile()
            ).collect(Collectors.toList());
            model.cutFiles(files);
        }

        @Override
        public void moveFilesEvent(List<File> files, String path) {
            model.moveFiles(files, path);
        }

        @Override
        public void moveFilesEvent(List<File> files, int whichList) {
            model.moveFiles(files, whichList);
        }

        @Override
        public void copyFilesToClipboardEvent(int whichList) {
            List<File> files = fileLists[whichList].getChildrenUnmodifiable().stream().filter(node ->
                    node instanceof FileLabelSelectable && ((FileLabelSelectable) node).isSelected()
            ).map(node -> ((FileLabelSelectable) node).getFile()
            ).collect(Collectors.toList());
            model.copyFilesToClipboard(files);
        }

        @Override
        public void createNewFileEvent(int whichList, String extension) {
            //add new node to the list
            long l = fileLists[whichList].getChildrenUnmodifiable().stream()
                    .filter(node -> node instanceof FileNodeSelectable && ((FileNodeSelectable) node).getFile().getName().startsWith("new File") &&
                            ((FileNodeSelectable) node).getFile().getName().endsWith(extension))
                    .count();
            TextField tf = new TextField("new File " + ++l + extension);
            tf.setPadding(new Insets(0));
            tf.setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    model.createFile(tf.getText(), whichList);
                }
            });
            tf.focusedProperty().addListener((observableValue, oldPropertyValue, newPropertyValue) -> {
                if (!newPropertyValue) {
                    //whe field goes out of focus without clicking enter we restore the old label
                    fileLists[whichList].getChildren().remove(tf);
                }
            });
            fileLists[whichList].getChildren().add(tf);
            tf.requestFocus();
            tf.selectRange(0, tf.getText().length() - extension.length());
        }

        @Override
        public void renameFileEvent(int whichList) {
            startRenamingFiles(whichList);
        }
    };

    private void startRenamingFiles(int whichList){
        List<FileNodeSelectable> nodesToRename = fileLists[whichList].getChildrenUnmodifiable()
                .stream().filter(node -> node instanceof FileNodeSelectable && ((FileNodeSelectable) node).isSelected())
                .map(node -> (FileNodeSelectable)node)
                .collect(Collectors.toList());
        if(nodesToRename.size()>0) {
            FileTextField firstTextField = replaceLabelWithTextField(nodesToRename.get(0), whichList);
            final List<FileTextField> nodes = new ArrayList<>();
            nodes.add(firstTextField);
            for (int i = 1; i < nodesToRename.size(); i++) {
                nodes.add(replaceLabelWithTextField(nodesToRename.get(i), whichList));
            }
            firstTextField.requestFocus();
            firstTextField.selectAll();
            boolean[] alreadyBound = new boolean[1];
            alreadyBound[0]=false;
            firstTextField.setOnKeyReleased(event -> {
                if(!alreadyBound[0]) {
                    for (int i = 1; i < nodes.size(); i++) {
                        nodes.get(i).textProperty().bind(firstTextField.textProperty());
                    }
                    alreadyBound[0]=false;
                } if (event.getCode()==KeyCode.ENTER){
                    //rename all nodes
                    for (int i = 0; i < nodes.size(); i++) {
                        String newName;
                        if(nodes.get(i).getText().contains("?")) {
                            newName = nodes.get(i).getText().replace("?", String.format("%02d", i+1));
                        } else {
                            newName=Integer.toString(i);
                        }
                        if(!nodes.get(i).getText().isEmpty()){
                            model.renameFile(nodes.get(i).getOldText(), newName, whichList);
                        } else{
                            model.getFiles();
                        }
                    }
                }
            });
            firstTextField.focusedProperty().addListener((observableValue, oldPropertyValue, newPropertyValue) -> {
                if (!newPropertyValue) {
                    //replace each node value with previous one
                    model.getFiles();
                }
            });
        }
    }

    private FileTextField replaceLabelWithTextField(FileNodeSelectable nodeToRename, int whichList){
        FileTextField tf = new FileTextField(nodeToRename.getFile().getName());
        tf.setPadding(new Insets(0));
        replaceNode(nodeToRename, tf, whichList);
        return tf;
    }

    Controller(ViewContract.Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileLists[0] = fileListA;
        fileLists[1] = fileListB;
        filePaths[0] = filePathA;
        filePaths[1] = filePathB;
        //on my distro the window is shifted to the left a bit and hides the names of the files
        //so we add padding
        if (System.getProperty("os.name").equals("Linux")) {
            fileListA.setPadding(new Insets(0, 0, 0, 5));
        }
        MouseEventsHelper helper = new MouseEventsHelper(drawingPane, fileLists, fileEventHelper);
        helper.handleSelectionRectangle(0);
        helper.handleSelectionRectangle(1);
        helper.handleContextMenu(0);
        helper.handleContextMenu(1);
        handleKeyEvents(scrollPaneA, 0);
        handleKeyEvents(scrollPaneB, 1);
        handleFilePath(filePathA, 0);
        handleFilePath(filePathB, 1);
        renameAllButtonA.setOnMouseClicked(mouseEvent -> startRenamingFiles(0));
        renameAllButtonB.setOnMouseClicked(mouseEvent -> startRenamingFiles(1));
        copyFilesButtonA.setOnMouseClicked(event -> fileEventHelper.copyFilesToClipboardEvent(0));
        copyFilesButtonB.setOnMouseClicked(event -> fileEventHelper.copyFilesToClipboardEvent(1));
        goToParentButtonA.setOnMouseClicked(event -> goToParentDirectory(0));
        goToParentButtonB.setOnMouseClicked(event -> goToParentDirectory(1));
    }

    private void handleFilePath(TextField filePath, int whichList) {
        String[] prevText = new String[1];
        filePath.focusedProperty().addListener((observableValue, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                prevText[0] = filePath.getText();
                filePath.positionCaret(filePath.getText().length());
            } else {
                filePath.setText(prevText[0]);
            }
        });
        filePath.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                pathChanged(filePath.getText(), whichList);
                filePath.requestFocus();
            }
        });
    }

    private void goToParentDirectory(int whichList) {
        model.goToParentDirectory(whichList);
    }

    private void handleKeyEvents(Control pane, int whichList) {
        pane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN).match(event)) {
                fileEventHelper.copyFilesToClipboardEvent(whichList);
            } else if (new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN).match(event)) {
                fileEventHelper.pasteFilesFromClipboardEvent(whichList);
            } else if (new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN).match(event)) {
                fileEventHelper.cutFilesEvent(whichList);
            } else if (new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN).match(event)) {
                fileLists[whichList].getChildrenUnmodifiable().forEach(node -> {
                    if (node instanceof FileLabelSelectable)
                        ((FileLabelSelectable) node).setSelected(true);
                });
            } else if (new KeyCodeCombination(KeyCode.DELETE, KeyCombination.SHIFT_DOWN).match(event)) {
                fileEventHelper.deleteFilesEvent(whichList);
            } else if (event.getCode() == KeyCode.DELETE) {
                fileEventHelper.moveFilesToTrash(whichList);
            }
        });
    }

    private void replaceNode(Object nodeToReplace, Node nodeToReplaceWith, int whichList) {
        ObservableList<Node> nodes = fileLists[whichList].getChildren();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) == nodeToReplace) {
                nodes.set(i, nodeToReplaceWith);
                break;
            }
        }
    }

    @Override
    public void displayPath(String path, int whichList) {
        filePaths[whichList].setText(path);
    }

    private void pathChanged(String path, int whichList) {
        model.enterDirectory(path, whichList);
    }

    private void viewFiles(File[] files, int whichList) {
        for (File file : files) {
            FileLabelSelectable label = new FileLabelSelectable(file);
            //createLabelContextMenu(label, whichList);
            //class used to check if mouse position while released is same as while pressed
            final MousePosition pressedMousePosition = MousePosition.ZERO;
            label.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> pressedMousePosition.setPosition(event.getSceneX(), event.getSceneY()));
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                ObservableList<Node> nodes = label.getParent().getChildrenUnmodifiable();
                if (pressedMousePosition.equals(event.getSceneX(), event.getSceneY())) {
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
                        if (event.getButton() == MouseButton.PRIMARY && label.isSelected()) {
                            pathChanged(label.getFile().getPath(), whichList);
                        } else {
                            if (event.getButton() != MouseButton.SECONDARY) {
                                nodes.forEach(n -> {
                                    if (n instanceof FileLabelSelectable && !label.equals(n))
                                        ((FileLabelSelectable) n).setSelected(false);
                                });
                            }
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
     *
     * @param files array of files to display
     */
    @Override
    public void displayFiles(File[] files, int whichList) {
        fileLists[whichList].getChildren().clear();
        if (files != null) {
            viewFiles(files, whichList);
        } else {
            Label label = new Label();
            label.setText("directory does not exist");
            fileLists[whichList].getChildren().add(label);
        }
    }

}
