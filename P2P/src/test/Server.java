/*
 *
 * Listen all the time
 * Function of Server:
 * 1) Scan the shared directory
 * 2) Send directory listing to client
 * 3) Send file
 * 4)
 *
 * */

package test;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.net.*;

public class Server
{
    private static String sharedDirectory = System.getProperty("user.home")+"/Desktop/";
    private static TreeItem<String> root;
    static TreeView<String> treeView;

    public static void createTree(File file, TreeItem<String> rootNode) {
        if (file.isDirectory())
        {
            TreeItem<String> treeItem = new TreeItem<>(file.getName());
            rootNode.getChildren().add(treeItem);
            rootNode.setExpanded(true);
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
        root =  new TreeItem<String>();
        createTree(new File(sharedDirectory), root);
        treeView = new TreeView<String>(root);

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

    public static void main(String[] args) throws IOException{
        refreshDirectory();
    }

//    public static void  main(String args[]) throws IOException
//    {
//        ServerSocket serverSocket = new ServerSocket(5001);
//        while(true)
//        {
//            Socket socket = serverSocket.accept();
//            System.out.println(socket.getInetAddress()+ " accepted");
//            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//            System.out.println("ois created");
//            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//            System.out.println("oos created");
//            System.out.println("refreshing");
//            refreshDirectory();
//            System.out.println("refreshed");
//            System.out.println("Sending files");
//            oos.writeObject(new File(System.getProperty("user.home")+"/Desktop/allFile"));
//            System.out.println("sent");
//            oos.close();
//            ois.close();
//            System.out.println("Closed");
//        }
//    }
}
