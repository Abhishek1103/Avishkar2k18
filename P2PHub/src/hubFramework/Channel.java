package hubFramework;

import java.io.Serializable;
import java.util.ArrayList;

public class Channel implements Serializable
{
    protected String channelName;
    //protected int channelPopularityIndex;
    protected int totalNoOfLikes, totalNoOfComments;
    protected int channelSubscribers;
    protected ArrayList<String> subscriberName;
    protected ArrayList<Video> videos;

    Channel(String name)
    {
        channelName = name;
        //channelPopularityIndex = 0;
        channelSubscribers = 0;
        totalNoOfLikes = 0;
        totalNoOfComments = 0;
        subscriberName = new ArrayList<String>();
        videos = new ArrayList<Video>();
    }
}
