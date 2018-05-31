package FileExplorer.nodes;

import javafx.geometry.Bounds;

import java.io.File;

public interface FileNodeSelectable {

    File getFile();
    boolean isSelected();
    void setSelected(boolean selected);
    boolean contains(double x, double y);
    Bounds localToScene(Bounds bounds);
    Bounds getBoundsInLocal();
    void onMouseHoverEnter();
    void onMouseHoverLeave();
}
