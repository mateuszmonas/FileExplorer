package FileExplorer.nodes;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.File;

public class FileLabelSelectable extends Label implements FileNodeSelectable {
    private boolean selected = false;
    private File file;

    public FileLabelSelectable(File file) {
        super();
        this.file=file;
        setText(file.getName());
        setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        setMinWidth(200);
    }

    @Override
    public void onMouseHoverEnter() {
        if(!selected){
            setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    @Override
    public void onMouseHoverLeave() {
        if(!selected){
            setBackground(null);
        }
    }

    public File getFile() {
        return file;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if(selected) {
            setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        }
        else {
            setBackground(null);
            setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        }
        this.selected = selected;
    }

    @Override
    public boolean contains(double x, double y){
        Bounds Bounds = localToScene(getBoundsInLocal());
        double minX = Bounds.getMinX();
        double maxX = Bounds.getMaxX();
        double maxY = Bounds.getMaxY();
        double minY = Bounds.getMinY();
        return x>minX && x<maxX && y>minY && y<maxY;
    }

}
