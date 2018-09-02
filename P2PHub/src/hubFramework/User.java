package hubFramework;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable
{
    protected String username;
    protected Boolean isCreator;
    protected ArrayList<Channel> channels;
    User(String username)
    {
        this.username = username;
        channels = new ArrayList<Channel>();
        isCreator = false;
    }
}
