package FileNameEditor.main;

import java.io.File;
import java.util.List;

public interface FileEventHelper {

    @FunctionalInterface
    interface DeleteFilesEvent{
        void  deleteFilesEvent(int whichList);
    }

    @FunctionalInterface
    interface MoveFilesToTrash {
        void moveFilesToTrash(int whichList);
    }

    @FunctionalInterface
    interface PasteFilesFromClipboardEvent {
        void pasteFilesFromClipboardEvent(int whichList);
    }

    @FunctionalInterface
    interface CutFilesEvent {
        void cutFilesEvent(int whichList);
    }

    interface MoveFilesEvent {
        void moveFilesEvent(List<File> files, String path);
        void moveFilesEvent(List<File> files, int whichList);
    }

    @FunctionalInterface
    interface CopyFilesToCpilboardEvent {
        void copyFilesToClipboardEvent(int whichList);
    }

    @FunctionalInterface
    interface CreateNewFile{
        void createNewFile(String fileName, int whichList);
    }

}


