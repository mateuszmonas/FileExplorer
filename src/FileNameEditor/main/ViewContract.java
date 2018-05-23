package FileNameEditor.main;

import java.io.File;
import java.util.List;

public interface ViewContract {

    interface Presenter{
        void start();
        void deleteFiles(List<File> files);
        void moveFiles(List<File> files, int whichList);
        void moveFiles(List<File> files, String path);
        void copyFilesToClipboard(List<File> files);
        void pasteFilesFromClipboard(int whichList);
        void moveFilesToTrash(List<File> files);
        void changeDirectory(String path, int whichList);
        void createFiles(String name, int whichList);
    }

    interface View{
        void displayPath(String path, int whichList);
        void displayFiles(File[] files, int whichList);
    }

}
