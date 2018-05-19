package nodes;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.io.File;

public class SelectableFileLabel extends Label {
    private boolean isSelected = false;
    private File file;

    public SelectableFileLabel(File file) {
        super();
        this.file=file;
        setText(file.getName());
    }

    public File getFile() {
        return file;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        if(selected) setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        else setBackground(null);
        isSelected = selected;
    }

    public boolean areCoordinatesInsideNode(double x, double y){
        Bounds Bounds = localToScene(getBoundsInLocal());
        double minX = Bounds.getMinX();
        double maxX = Bounds.getMaxX();
        double maxY = Bounds.getMaxY();
        double minY = Bounds.getMinY();
        return x>minX && x<maxX && y>minY && y<maxY;
    }
}
