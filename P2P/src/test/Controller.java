package test;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class Controller implements Initializable
{
    @FXML TreeView<String> treeView;

    private static String sharedDirectory = System.getProperty("user.home")+"/Desktop/";
    private static TreeItem<String> root;

    public static void createTree(File file, TreeItem<String> rootNode) {
        if (file.isDirectory())
        {
            TreeItem<String> treeItem = new TreeItem<>(file.getName());
            rootNode.getChildren().add(treeItem);
            //rootNode.setExpanded(true);
            for (File f : file.listFiles()) {
                createTree(f, treeItem);
            }
        }
        else
        {
            rootNode.getChildren().add(new TreeItem<>(file.getName()));
        }
    }

    protected static void refreshDirectory() throws IOException
    {
        root =  new TreeItem<String>("User Name will come here");
        createTree(new File(sharedDirectory), root);

//        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(System.getProperty("user.home")+"/Desktop/allFile")));
//        oos.writeObject(allFile);
//        oos.close();
//        for(File file : allFile)
//        {
//            if(file.isFile())
//                System.out.println("File: " + file.getName());
//            else if(file.isDirectory())
//                System.out.println("Dir: "+ file.getName());
//            else
//                System.out.println("Ye kya hai be...");
//        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try {
            refreshDirectory();

        } catch (Exception e) {
            e.printStackTrace();
        }
        treeView.setRoot(root);
        //treeView = Server.treeView;
    }
}
