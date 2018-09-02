package hubFramework;

import java.io.Serializable;
import java.util.ArrayList;

public class Channel implements Serializable
{
    protected String channelName;
    protected int channelPopularityIndex;
    protected int channelSubscribers;
    protected ArrayList<Video> videos;

    Channel(String name)
    {
        channelName = name;
        channelPopularityIndex = 0;
        channelSubscribers = 0;
        videos = new ArrayList<Video>();
    }
}
