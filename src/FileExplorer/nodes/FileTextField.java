package FileExplorer.nodes;


import javafx.scene.control.TextField;

public class FileTextField extends TextField {

    private final String oldText;

    public FileTextField(String oldText){
        super(oldText);
        this.oldText = oldText;
    }

    public String getOldText() {
        return oldText;
    }
}
