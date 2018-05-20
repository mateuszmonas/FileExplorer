package main;

import java.io.File;
import java.util.List;

public interface FileEventHelper {

    interface PasteFilesFromClipboardEvent {
        void pasteFilesFromClipboardEvent(int whichList);
    }

    interface CutFilesEvent {
        void cutFilesEvent(int whichList);
    }

    interface MoveFilesEvent {
        void moveFilesEvent(List<File> files, String path);
        void moveFilesEvent(List<File> files, int whichList);
    }

    interface CopyFilesToCpilboardEvent {
        void copyFilesToClipboardEvent(int whichList);
    }

}


