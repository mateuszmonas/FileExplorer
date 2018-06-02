package FileExplorer.test;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import FileExplorer.main.*;

public class ModelTest {
    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    private ViewContract.Model model;

    public ModelTest() {
        model = new Model();
        model.start(new ViewContract.Controller() {
            @Override
            public void displayPath(String path, int whichList) {

            }

            @Override
            public void displayFiles(File[] files, int whichList) {

            }
        }, new DialogHelper() {
            @Override
            public void fileAlreadyExistsDialog() {

            }

            @Override
            public void fileWasNotCreatedDialog() {

            }

            @Override
            public void fineCouldNotBeMovedToTrashDialog() {

            }

            @Override
            public void fileCouldNotBeDeletedDialog() {

            }

            @Override
            public void destinationDirectoryDoesNotExistDialog() {

            }

            @Override
            public void somethingWentWrongDialog() {

            }
        });
    }

    @Test
    public void filesShouldNotBeMoved() throws IOException{
        File source = folder.newFolder("source");
        File dest = folder.newFolder("dest");
        File subFolderSource = new File(source, "subFolder");
        File fileOneSource = new File(source, "test.txt");
        File subFolderDest = new File(dest, "subFolder");
        Assert.assertTrue(subFolderSource.mkdir());
        Assert.assertTrue(fileOneSource.mkdir());
        Assert.assertTrue(subFolderDest.mkdir());
        model.moveFiles(Arrays.asList(subFolderSource, fileOneSource), dest.getPath());
        Assert.assertTrue(subFolderSource.exists());
        Assert.assertTrue(fileOneSource.exists());
        Assert.assertTrue(subFolderDest.exists());
    }

    @Test
    public void filesShouldBeMoved() throws IOException {
        File source = folder.newFolder("source");
        File dest = folder.newFolder("dest");
        File subFolder = new File(source, "subFolder");
        File fileOne = new File(source, "test.txt");
        File fileTwo = new File(subFolder, "test2.txt");
        Assert.assertTrue(subFolder.mkdir());
        Assert.assertTrue(fileOne.mkdir());
        Assert.assertTrue(fileTwo.mkdir());
        model.moveFiles(Collections.singletonList(source), dest.getPath());
        Assert.assertTrue(new File(dest.getPath() + File.separator + source.getName()).exists());
        Assert.assertTrue(new File(dest.getPath() + File.separator + source.getName() + File.separator + subFolder.getName()).exists());
        Assert.assertTrue(new File(dest.getPath() + File.separator + source.getName() + File.separator + subFolder.getName()).exists());
        Assert.assertTrue(new File(dest.getPath() + File.separator + source.getName() + File.separator + fileOne.getName()).exists());
        Assert.assertTrue(new File(dest.getPath() + File.separator + source.getName() + File.separator + subFolder.getName() + File.separator + fileTwo.getName()).exists());
    }

    @Test
    public void fileShouldBeCreated(){
        String fileName = "Created Test File";
        String path = folder.getRoot().getPath();
        model.enterDirectory(path, 0);
        model.createFile(fileName, 0);
        Assert.assertTrue(new File(path + File.separator + fileName).exists());
    }

    @Test
    public void fileShouldBeDeleted() throws IOException{
        File fileToDelete = folder.newFolder("fileToDelete");
        File fileToDelete2 = folder.newFolder("fileToDelete2");
        Assert.assertTrue(fileToDelete.exists());
        Assert.assertTrue(fileToDelete2.exists());
        model.deleteFiles(Arrays.asList(fileToDelete, fileToDelete2));
        Assert.assertFalse(fileToDelete.exists());
        Assert.assertFalse(fileToDelete2.exists());
    }

    @Test
    public void filesShouldBeCopiedToClipboard() throws IOException, UnsupportedFlavorException {
        File source = folder.newFolder("source");
        File subFolder = new File(source, "subFolder");
        File fileOne = new File(source, "test.txt");
        Assert.assertTrue(subFolder.mkdir());
        Assert.assertTrue(fileOne.mkdir());
        List<File> files = Arrays.asList(subFolder, fileOne);
        model.copyFilesToClipboard(files);
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = c.getContents(this);
        Assert.assertTrue(Arrays.stream(t.getTransferDataFlavors()).anyMatch(dataFlavor -> dataFlavor.equals(DataFlavor.javaFileListFlavor)));
        Assert.assertEquals(files, t.getTransferData(DataFlavor.javaFileListFlavor));
    }

    @Test
    public void fileShouldNotBeRenamed() throws IOException{
        File fileA = folder.newFile("fileA");
        File fileB = folder.newFile("fileB");
        String newName = "fileB";
        Assert.assertTrue(fileA.exists());
        model.renameFile(fileA, newName);
        Assert.assertTrue(fileA.exists());
        Assert.assertTrue(new File(fileA.getParent()+File.separator+newName).exists());
    }

    @Test
    public void fileShouldBeRenamed() throws IOException{
        File fileA = folder.newFile("fileA");
        String newName = "fileB";
        Assert.assertTrue(fileA.exists());
        model.renameFile(fileA, newName);
        Assert.assertFalse(fileA.exists());
        Assert.assertTrue(new File(fileA.getParent()+File.separator+newName).exists());
    }

}
