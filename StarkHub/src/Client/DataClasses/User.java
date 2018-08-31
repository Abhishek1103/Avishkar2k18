package Client.DataClasses;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String userId;
    private transient  String password;
    private boolean isContentCreator = false;
    private int numberOfChannels = 0;
    private boolean isPremium = false;

}
