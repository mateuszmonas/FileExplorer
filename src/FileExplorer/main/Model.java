package FileExplorer.main;

import org.apache.commons.io.FileUtils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import FileExplorer.file.FileTransferable;

public class Model implements ViewContract.Model {

    private ViewContract.Controller controller;
    private DialogHelper dialogHelper;
    private String[] paths = new String[2];
    private boolean cuttingFiles = false;
    private ClipboardOwner lostOwnership = (clipboard, contents) -> {
        System.out.println("Lost ownership");
        cuttingFiles = false;
    };

    public Model() {
        if (System.getProperty("os.name").equals("Linux")) {
            paths[0] = "/media/storage/tests";
            paths[1] = "/media/storage/tests";
        } else if (System.getProperty("os.name").startsWith("Windows")) {
            paths[0] = "D:\\";
            paths[1] = "D:\\";
        }
    }

    private void changePath(String path, int whichList) {
        paths[whichList] = path;
        getFiles();
    }

    @Override
    public void goToParentDirectory(int whichList) {
        String parentPath = new File(paths[whichList]).getParent();
        if (parentPath != null) {
            changePath(parentPath, whichList);
        }
    }

    @Override
    public void enterDirectory(String path, int whichList) {
        if (new File(path).isDirectory()) {
            changePath(path, whichList);
        } else if (!new File(path).isFile()) {
            dialogHelper.destinationDirectoryDoesNotExistDialog();
        }
        getFiles();
    }

    @Override
    public void start(ViewContract.Controller controller, DialogHelper dialogHelper) {
        this.dialogHelper = dialogHelper;
        this.controller = controller;
        getFiles();
    }

    @Override
    public void cutFiles(List<File> files) {
        //set cutting files to true
        cuttingFiles = true;
        //add files to clipboard
        FileTransferable ft = new FileTransferable(files);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, lostOwnership);
    }

    @Override
    public void renameFile(File oldFile, String newName) {
        File newFile = new File(oldFile.getParent() + File.separator + newName);
        if (!newFile.exists()) {
            try {
                if (oldFile.isFile()) {
                    FileUtils.moveFile(oldFile, new File(oldFile.getParent() + File.separator + newName));
                }
                if (oldFile.isDirectory()) {
                    FileUtils.moveDirectory(oldFile, new File(oldFile.getParent() + File.separator + newName));
                }
            } catch (IOException e) {
                dialogHelper.somethingWentWrongDialog();
                e.printStackTrace();
            }
        } else {
            dialogHelper.fileAlreadyExistsDialog();
        }
        getFiles();
    }

    @Override
    public void renameFile(String oldName, String newName, int whichList) {
        File newFile = new File(paths[whichList] + File.separator + newName);
        File oldFile = new File(paths[whichList] + File.separator + oldName);
        if (!newFile.exists()) {
            try {
                if (oldFile.isFile()) {
                    FileUtils.moveFile(oldFile, new File(oldFile.getParent() + File.separator + newName));
                }
                if (oldFile.isDirectory()) {
                    FileUtils.moveDirectory(oldFile, new File(oldFile.getParent() + File.separator + newName));
                }
            } catch (IOException e) {
                dialogHelper.somethingWentWrongDialog();
                e.printStackTrace();
            }
        } else {
            dialogHelper.fileAlreadyExistsDialog();
        }
        getFiles();
    }

    @Override
    public void deleteFiles(List<File> files) {
        files.forEach(
                file -> {
                    try {
                        FileUtils.forceDelete(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        dialogHelper.fileCouldNotBeDeletedDialog();
                    }
                }
        );
        getFiles();
    }

    @Override
    public void moveFilesToTrash(List<File> files) {
        if (com.sun.jna.platform.FileUtils.getInstance().hasTrash()) {
            try {
                com.sun.jna.platform.FileUtils.getInstance().moveToTrash(files.toArray(new File[0]));
            } catch (IOException e) {
                dialogHelper.somethingWentWrongDialog();
                e.printStackTrace();
            }
        } else {
            dialogHelper.fineCouldNotBeMovedToTrashDialog();
        }
        getFiles();
    }

    @Override
    public void createFile(String name, int whichList) {
        File newFile = new File(paths[whichList] + File.separator + name);
        if (!newFile.exists()) {
            if (!newFile.mkdir()) {
                dialogHelper.fileWasNotCreatedDialog();
            }
        } else {
            dialogHelper.fileAlreadyExistsDialog();
        }
        getFiles();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void pasteFilesFromClipboard(int whichList) {
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = c.getContents(this);
        File dest = new File(paths[whichList]);
        if (t == null || !dest.exists())
            return;
        try {
            List<File> filesFromClipboard = new ArrayList<>();
            if (Arrays.stream(t.getTransferDataFlavors()).anyMatch(dataFlavor -> dataFlavor.equals(DataFlavor.javaFileListFlavor))) {
                filesFromClipboard.addAll((List<File>) t.getTransferData(DataFlavor.javaFileListFlavor));
            }
            if (cuttingFiles) {
                moveFiles(filesFromClipboard, whichList);
            } else {
                filesFromClipboard.forEach(file -> {
                    try {
                        if (file.isDirectory()) {
                            FileUtils.copyDirectoryToDirectory(file, dest);
                        } else if (file.isFile()) {
                            FileUtils.copyFileToDirectory(file, dest);
                        }
                    } catch (IOException e) {
                        dialogHelper.somethingWentWrongDialog();
                        e.printStackTrace();
                    }
                });
            }
        } catch (UnsupportedFlavorException | IOException e) {
            dialogHelper.somethingWentWrongDialog();
            e.printStackTrace();
        }
        getFiles();
    }

    @Override
    public void copyFilesToClipboard(List<File> files) {
        cuttingFiles = false;
        FileTransferable ft = new FileTransferable(files);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, lostOwnership);
    }

    @Override
    public void moveFiles(List<File> files, String path) {
        File dest = new File(path);

        if (dest.exists()) {
            for (File file : files) {
                if (new File(dest.getPath() + File.separator + file.getName()).exists()) {
                    dialogHelper.fileAlreadyExistsDialog();
                    return;
                }
            }
            files.forEach(file -> {
                try {
                    if (file.isDirectory()) {
                        FileUtils.moveDirectoryToDirectory(file, new File(path), false);
                    } else if (file.isFile()) {
                        FileUtils.moveFileToDirectory(file, new File(path), false);
                    }
                } catch (IOException e) {
                    dialogHelper.somethingWentWrongDialog();
                    e.printStackTrace();
                }
            });
        } else {
            dialogHelper.destinationDirectoryDoesNotExistDialog();
        }
        getFiles();
    }

    @Override
    public void moveFiles(List<File> files, int whichList) {
        File dest = new File(paths[whichList]);
        if (dest.exists() && !paths[0].equals(paths[1])) {
            files.forEach(file -> {
                try {
                    if (file.isDirectory()) {
                        FileUtils.moveDirectoryToDirectory(file, dest, false);
                    } else if (file.isFile()) {
                        FileUtils.moveFileToDirectory(file, dest, false);
                    }
                } catch (IOException e) {
                    dialogHelper.somethingWentWrongDialog();
                    e.printStackTrace();
                }
            });
        }
        getFiles();
    }

    /**
     * Gets all files in the given paths
     * and updates the controller
     */
    @Override
    public void getFiles() {
        for (int whichList = 0; whichList < 2; whichList++) {
            File folder = new File(paths[whichList]);
            File[] fileList = folder.listFiles();
            if (fileList != null) {
                fileList = Arrays.stream(fileList).filter(file -> !file.isHidden()).toArray(File[]::new);
            }
            controller.displayPath(paths[whichList], whichList);
            controller.displayFiles(fileList, whichList);
        }
    }

}
