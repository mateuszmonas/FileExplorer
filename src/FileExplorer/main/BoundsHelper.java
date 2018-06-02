package FileExplorer.main;


import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;

public class BoundsHelper {

    public static Bounds getVisibleBounds(Node aNode)
    {
        // If node not visible, return empty bounds
        if(!aNode.isVisible()) return new BoundingBox(0,0,-1,-1);

        // If node has clip, return clip bounds in node coords
        if(aNode.getClip()!=null) return aNode.getClip().getBoundsInParent();

        // If node has parent, get parent visible bounds in node coords
        Bounds bounds = aNode.getParent()!=null? getVisibleBounds(aNode.getParent()) : null;
        if(bounds!=null && !bounds.isEmpty()) bounds = aNode.parentToLocal(bounds);
        return bounds;
    }
}
