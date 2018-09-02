package Client.Utility;



import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;


public class AddToWatchLater implements Runnable {


    private Client.HubServices.Video video;

    public AddToWatchLater(Client.HubServices.Video video){
        this.video = video;
    }

    @Override
    public void run() {
        try {
            System.out.println("Add to watch later thread running");

            File f = new File(System.getProperty("user.home") + "/starkhub/watchlater/" + video.getVideoName());
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(video);
            oos.close();
            System.out.println("Object written");
            System.out.println("Add to watch later thread exiting");
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
