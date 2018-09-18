package Client.Utility;

import Client.DataClasses.Channel;
import Client.Login.Main;
import Client.UI.MainPageController;
import hubFramework.Video;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class InitializeListsAndMapsService extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try{

                    // TODO: Add functionality
                    //System.out.println("\n\n Add Functionality to me \n\n");

                    File f = new File(System.getProperty("user.home") + "/starkhub/"+ "watchlater/list");
                    if(!f.exists()){
                        MainPageController.watchLaterList = new ArrayList<>();
                    }
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                    MainPageController.watchLaterList = (ArrayList<Video>) ois.readObject();
                    ois.close();
                    System.out.println("Read WatchLater");

                    f = new File(System.getProperty("user.home") + "/starkhub/"+ "history/list");
                    if(!f.exists()){
                        MainPageController.historyList = new ArrayList<>();
                    }
                    ois = new ObjectInputStream(new FileInputStream(f));
                    MainPageController.historyList = (ArrayList<Video>) ois.readObject();
                    ois.close();
                    System.out.println("Read History");

                    f = new File(System.getProperty("user.home") + "/starkhub/"+ "mychannels/list");
                    if(!f.exists()){
                        MainPageController.myChannelMap= new HashMap<>();
                    }
                    ois = new ObjectInputStream(new FileInputStream(f));
                    MainPageController.myChannelMap = (HashMap<String, Channel>) ois.readObject();
                    ois.close();
                    System.out.println("Read Mychannels");

                    f = new File(System.getProperty("user.home") + "/starkhub/"+ "subscriptions/list");
                    if(!f.exists()){
                        MainPageController.subscribedChannelMap= new HashMap<>();
                    }
                    ois = new ObjectInputStream(new FileInputStream(f));
                    MainPageController.subscribedChannelMap = (HashMap<String, String>) ois.readObject();
                    ois.close();
                    System.out.println("Read Subscriptions");

                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
    }


}
