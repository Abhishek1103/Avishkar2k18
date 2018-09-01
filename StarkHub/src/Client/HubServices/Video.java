package Client.HubServices;


import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;

public class Video implements Serializable {

    protected String videoName, ownerName, pathOfVideo;
    protected int numberOfLikes, numberOfDislikes, numberOfComments, numberOfViews;
    protected Image thumbnail;
    protected ArrayList<String> tags;

    Video(String pathOfVideo, String ownerName, Image thumbnail, ArrayList<String> tags) throws FileNotFoundException {
        this.pathOfVideo = pathOfVideo;
        videoName = pathOfVideo.substring(pathOfVideo.lastIndexOf("/"));
        this.ownerName = ownerName;
        numberOfComments = numberOfLikes = numberOfDislikes = numberOfViews = 0;
        this.thumbnail = thumbnail;
        this.tags = tags;
    }

}
