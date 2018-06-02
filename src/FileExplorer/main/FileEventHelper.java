package FileExplorer.main;

import java.io.File;
import java.util.List;

import FileExplorer.nodes.FileNodeSelectable;

public interface FileEventHelper {

    void deleteFilesEvent(int whichList);

    void moveFilesToTrash(int whichList);

    void pasteFilesFromClipboardEvent(int whichList);

    void cutFilesEvent(int whichList);

    void moveFilesEvent(List<File> files, String path);

    void moveFilesEvent(List<File> files, int whichList);

    void copyFilesToClipboardEvent(int whichList);

    void createNewFile(int whichList, String extension);

    void renameFile(FileNodeSelectable node, int whichList);

}


