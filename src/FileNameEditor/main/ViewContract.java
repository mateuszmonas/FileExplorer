package FileNameEditor.main;

import java.io.File;
import java.util.List;

public interface ViewContract {

    interface Presenter{
        public void start();
        public void deleteFiles(List<File> files);
        public void moveFiles(List<File> files, int whichList);
        public void moveFiles(List<File> files, String path);
        public void copyFilesToClipboard(List<File> files);
        public void pasteFilesFromClipboard(int whichList);
        public void moveFilesToTrash(List<File> files);
        public void changeDirectory(String path, int whichList);
    }

    interface View{
        public void displayPath(String path, int whichList);
        public void displayFiles(File[] files, int whichList);
    }

}
