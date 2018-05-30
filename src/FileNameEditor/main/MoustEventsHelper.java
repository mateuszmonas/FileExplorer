package FileNameEditor.main;

import FileNameEditor.nodes.FileNodeSelectable;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import FileNameEditor.nodes.FileLabelSelectable;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * sole purpose of this class is containing all the code responsible for the selection rectangle
 */
class MoustEventsHelper {

    final private Pane drawingPane;
    final private VBox fileLists[];
    final private Rectangle selectionRectangle = new Rectangle();
    final private Delta dragDelta = new Delta();
    final private FileEventHelper fileEventHelper;

    MoustEventsHelper(Pane drawingPane, VBox fileLists[], FileEventHelper fileEventHelper) {
        this.drawingPane = drawingPane;
        this.fileLists=fileLists;
        this.fileEventHelper=fileEventHelper;

    }

    private void setSelectionRectangleDimensions(double startX, double startY, double width, double height){
        selectionRectangle.setX(startX);
        selectionRectangle.setY(startY);
        selectionRectangle.setWidth(width);
        selectionRectangle.setHeight(height);
    }

    void handleContextMenu(int whichList){
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem cutContextItem = new MenuItem("Cut");
        cutContextItem.setOnAction(event -> fileEventHelper.cutFilesEvent(0));
        MenuItem copyContextItem = new MenuItem("Copy");
        copyContextItem.setOnAction(event -> fileEventHelper.copyFilesToClipboardEvent(whichList));
        MenuItem pasteContextItem = new MenuItem("Paste");
        pasteContextItem.setOnAction(event -> fileEventHelper.pasteFilesFromClipboardEvent(whichList));
        MenuItem deleteContextItem = new MenuItem("Delete");
        deleteContextItem.setOnAction(event -> fileEventHelper.moveFilesToTrash(whichList));
        MenuItem renameContextItem = new MenuItem("Rename");
        renameContextItem.setOnAction(event -> {
            FileNodeSelectable clickedNode = fileLists[whichList].getChildrenUnmodifiable().stream()
                    .filter(node -> node instanceof FileNodeSelectable && node.localToScreen(node.getBoundsInLocal()).contains(contextMenu.getAnchorX(), contextMenu.getAnchorY()))
                    .map(node -> (FileNodeSelectable)node)
                    .findAny().orElse(null);
        });
        Menu newItemMenu = new Menu("New");

        MenuItem folder = new MenuItem("folder");
        folder.setOnAction(event -> fileEventHelper.createNewFile("new folder", whichList));
        newItemMenu.getItems().addAll(folder);

        MenuItem txt = new MenuItem(".txt");
        txt.setOnAction(event -> fileEventHelper.createNewFile("new file.txt", whichList));
        newItemMenu.getItems().addAll(txt);

        contextMenu.getItems().addAll(cutContextItem, copyContextItem, pasteContextItem, deleteContextItem, renameContextItem, newItemMenu);

        MousePosition pressedMousePosition = MousePosition.ZERO;
        fileLists[whichList].addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            pressedMousePosition.setPosition(event.getSceneX(), event.getSceneY());
            if(contextMenu.isShowing()){
                contextMenu.hide();
            }
        });
        fileLists[whichList].addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(pressedMousePosition.equals(event.getSceneX(), event.getSceneY()) && event.getButton()==MouseButton.SECONDARY){
                contextMenu.show(fileLists[whichList], event.getScreenX(), event.getScreenY());
            }
        });
    }

    void handleSelectionRectangle(int whichList){
        selectionRectangle.setOpacity(0.5);
        selectionRectangle.setFill(Color.BLUE);
        final ArrayList<FileNodeSelectable> nodesSelectedBeforeDrawing = new ArrayList<>();
        final ArrayList<FileNodeSelectable> draggedNodes = new ArrayList<>();
        final ArrayList<FileNodeSelectable> onePaneChildNodes = new ArrayList<>();
        final ArrayList<FileNodeSelectable> allNodes = new ArrayList<>();
        fileLists[whichList].addEventHandler(MouseEvent.MOUSE_PRESSED, event->{
            dragDelta.startX = event.getSceneX();
            dragDelta.startY = event.getSceneY();
            onePaneChildNodes.addAll(fileLists[whichList].getChildrenUnmodifiable().stream().filter(node -> node instanceof FileNodeSelectable).map(node -> (FileNodeSelectable)node).collect(Collectors.toList()));
            boolean selectedNodeWasClicked = onePaneChildNodes.stream().anyMatch(
                    node -> node.contains(dragDelta.startX, dragDelta.startY) && node.isSelected());
            FileNodeSelectable clickedNode = onePaneChildNodes.stream().filter(node -> node.contains(event.getSceneX(), event.getSceneY())).findAny().orElse(null);
            //if control is pressed or if clicked node was already selected
            //don't remove the selection from previously selected FileNameEditor.nodes
            if(clickedNode!=null) {
                if (event.isControlDown() || event.isShiftDown() || clickedNode.isSelected()) {
                    nodesSelectedBeforeDrawing.addAll(onePaneChildNodes.stream().filter(FileNodeSelectable::isSelected).collect(Collectors.toList()));
                    if (selectedNodeWasClicked) {
                        allNodes.addAll(Stream.concat(fileLists[0].getChildrenUnmodifiable().stream(), fileLists[1].getChildrenUnmodifiable().stream())
                                .filter(node -> node instanceof FileNodeSelectable)
                                .map(node -> (FileNodeSelectable) node)
                                .collect(Collectors.toList()));
                        draggedNodes.addAll(nodesSelectedBeforeDrawing);
                    }
                } else if(event.getButton()==MouseButton.SECONDARY) {
                    onePaneChildNodes.forEach(node -> {
                        if(node!=clickedNode){
                            node.setSelected(false);
                        }
                    });
                }
            }else if(event.getButton()==MouseButton.PRIMARY){
                onePaneChildNodes.forEach(node -> node.setSelected(false));
            }
        });
        fileLists[whichList].addEventHandler(MouseEvent.DRAG_DETECTED, event -> {
            if(event.getButton()==MouseButton.PRIMARY && !draggedNodes.isEmpty()){
                fileLists[whichList].getScene().setCursor(Cursor.CLOSED_HAND);
            }
        });
        //dirty hack used to around lambda's need for final variables
        FileNodeSelectable[] f = new FileLabelSelectable[1];
        fileLists[whichList].addEventHandler(MouseEvent.MOUSE_DRAGGED, event-> {
            //check if first node clicked was not already selected
            //drawing selection grid and selecting FileNameEditor.nodes
            if(nodesSelectedBeforeDrawing.stream().noneMatch(
                    node -> node.contains(dragDelta.startX, dragDelta.startY)) && event.getButton()==MouseButton.PRIMARY) {
                ObservableList<Node> nodes = drawingPane.getChildren();
                nodes.remove(selectionRectangle);
                dragDelta.x = event.getSceneX();
                dragDelta.y = event.getSceneY();
                //we have to calculate where the current position of the cursor
                //is relative to where it was clicked
                if (dragDelta.x > dragDelta.startX && dragDelta.y > dragDelta.startY) {
                    setSelectionRectangleDimensions(dragDelta.startX, dragDelta.startY, dragDelta.x - dragDelta.startX, dragDelta.y - dragDelta.startY);
                } else if (dragDelta.x > dragDelta.startX && dragDelta.y < dragDelta.startY) {
                    setSelectionRectangleDimensions(dragDelta.startX, dragDelta.y, dragDelta.x - dragDelta.startX, dragDelta.startY - dragDelta.y);
                } else if (dragDelta.x < dragDelta.startX && dragDelta.y > dragDelta.startY) {
                    setSelectionRectangleDimensions(dragDelta.x, dragDelta.startY, dragDelta.startX - dragDelta.x, dragDelta.y - dragDelta.startY);
                } else if (dragDelta.x < dragDelta.startX && dragDelta.y < dragDelta.startY) {
                    setSelectionRectangleDimensions(dragDelta.x, dragDelta.y, dragDelta.startX - dragDelta.x, dragDelta.startY - dragDelta.y);
                }
                nodes.add(selectionRectangle);
                onePaneChildNodes.forEach(node -> {
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
                        if (selectionMinY <= nodeMaxY && selectionMaxY >= nodeMinY && selectionMinX <= nodeMaxX && selectionMaxX >= nodeMinX) {
                            node.setSelected(true);
                        } else if (!nodesSelectedBeforeDrawing.contains(node)) {
                            node.setSelected(false);
                        }
                });
            }else {
                //code responsible for highlighting nodes hovered over
                //it is pretty ugly, but it works
                Object fa = event.getPickResult().getIntersectedNode();
                if(fa instanceof FileNodeSelectable){
                    if(fa!=f[0]){
                        ((FileNodeSelectable) fa).onMouseHoverEnter();
                        if(f[0]!=null) {
                            f[0].onMouseHoverLeave();
                        }
                        f[0]=(FileNodeSelectable) fa;
                    }
                } else if(f[0]!=null) {
                    f[0].onMouseHoverLeave();
                }
            }
        });
        //after the mouse is released remove the rectangle and clear its position
        fileLists[whichList].setOnMouseReleased(event -> {
            if(!draggedNodes.isEmpty()){
                FileNodeSelectable currentNode = allNodes.stream()
                        .filter(node -> node.contains(event.getSceneX(), event.getSceneY()))
                        .findAny().orElse(null);
                if (currentNode != null && !draggedNodes.contains(currentNode)) {
                    fileEventHelper.moveFilesEvent(draggedNodes.stream().map(FileNodeSelectable::getFile).collect(Collectors.toList()), currentNode.getFile().getPath());
                } else {
                    for (int i = 0; i < 2; i++) {
                        if(fileLists[i]!=fileLists[whichList] && fileLists[i].contains(fileLists[i].sceneToLocal(event.getSceneX(), event.getSceneY()))){
                            fileEventHelper.moveFilesEvent(draggedNodes.stream().map(FileNodeSelectable::getFile).collect(Collectors.toList()), i);
                            break;
                        }
                    }
                }
            }
            drawingPane.getChildren().remove(selectionRectangle);
            setSelectionRectangleDimensions(0,0,0,0);
            dragDelta.reset();
            nodesSelectedBeforeDrawing.clear();
            draggedNodes.clear();
            onePaneChildNodes.clear();
            allNodes.clear();
            fileLists[whichList].getScene().setCursor(Cursor.DEFAULT);
        });
    }

    private class Delta{
        double startX = 0;
        double startY = 0;
        double x = 0;
        double y = 0;
        void reset(){
            startY=0;
            startX=0;
            x=0;
            y=0;
        }
    }
}
