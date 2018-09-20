package Client.DataClasses;

import java.io.Serializable;
import java.util.ArrayList;

public class Channel implements Serializable {

    protected String channelName;
    protected int numberOfVideos;
    protected String channelOwner;

    ArrayList<Video> videoList;


    public Channel(String channelName, String channelOwner){
        this.channelName = channelName;
        this.channelOwner = channelOwner;
        videoList = new ArrayList<>();
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getNumberOfVideos() {
        return numberOfVideos;
    }

    public void setNumberOfVideos(int numberOfVideos) {
        this.numberOfVideos = numberOfVideos;
    }

    public String getChannelOwner() {
        return channelOwner;
    }

    public void setChannelOwner(String channelOwner) {
        this.channelOwner = channelOwner;
    }

    public ArrayList<Video> getVideoList() {
        return videoList;
    }

    public void setVideoList(ArrayList<Video> videoList) {
        this.videoList = videoList;
    }
}
