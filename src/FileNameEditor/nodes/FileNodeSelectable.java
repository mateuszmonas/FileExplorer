package FileNameEditor.nodes;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;

import java.io.File;

public interface FileNodeSelectable {

    File getFile();
    boolean isSelected();
    void setSelected(boolean selected);
    boolean contains(double x, double y);
    Bounds localToScene(Bounds bounds);
    Bounds getBoundsInLocal();
}
