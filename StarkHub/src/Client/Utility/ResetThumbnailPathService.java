package Client.Utility;

import Client.Login.Main;
import Client.UI.ClientPageController;
import Client.UI.VideoPlayerController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;

import static java.lang.Thread.sleep;


public class ResetThumbnailPathService extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                try{
                    // TODO: Program Logic

                    System.out.println("Reset ThumbNail Running");

                    while(!ClientPageController.startResetingThumbNail){
                        System.out.println(ClientPageController.startResetingThumbNail+"");
                        sleep(100);
                    }

                    File[] fileList = new File(System.getProperty("user.home")+"/starkhub/"+Main.USERNAME+"/temp").listFiles();

                    for(File f: fileList){
                        String name = f.getName().substring(0, f.getName().lastIndexOf('.'));
                        if(!ClientPageController.receivedVideos.isEmpty() && ClientPageController.receivedVideos.containsKey(name)){
                            ClientPageController.receivedVideos.get(name).setThumbnailPath(f.getAbsolutePath());
                        }else if(!ClientPageController.receivedVideosRecommended.isEmpty()&&ClientPageController.receivedVideosRecommended.containsKey(name)){
                            ClientPageController.receivedVideosRecommended.get(name).setThumbnailPath(f.getAbsolutePath());
                        }
                    }

                    System.out.println("ThumbNailPath Corrected");


                }catch(Exception e){
                    e.printStackTrace();
                }


                return null;
            }
        };
    }
}
