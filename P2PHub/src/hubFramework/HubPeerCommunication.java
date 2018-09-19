package hubFramework;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class HubPeerCommunication implements Runnable {

    private Peer peer;
    HubPeerCommunication(Peer peer)
    {
        this.peer = peer;
    }

     private Statement connect() {
        Statement stm = null;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/starkhub", "surbhit", "awasthi@7");
            stm = conn.createStatement();

        } catch (SQLException ex) {
            System.out.println("SQL me error!!!");
            ex.printStackTrace();
        }
        return stm;
    }

    @Override
    public void run()
    {
        try {
            String flag = peer.dis.readUTF();
            switch (flag) {
                case "#USERNAME":
                    System.out.println("#USERNAME");
                    authenticate();
                    break;
                case "#NEWUSER":
                    System.out.println("#NEWUSER");
                    newUser();
                    break;
                case "#EXISTINGUSER":
                {
                    System.out.println("#EXISTINGUSER");
                    String username = peer.dis.readUTF();
                    existingUser(username, 2);
                    break;
                }
                case "SEARCH": {
                    System.out.println("#SEARCH");
                    search();
                    break;
                }
                case "#TAGSEARCH": {
                    System.out.println("#TAGSEARCH");
                    tagSearch();
                    break;
                }
                case "#MAKECHANNEL": {
                    System.out.println("#MAKECHANNEL");
                    makeChannel();
                    break;
                }
                case "#ADDVIDEOINCHANNEL":
                {
                    addVideoInChannel();
                    break;
                }
                case "#SETLIKES":
                {
                    System.out.println("#SETLIKES");
                    setLikes();
                    break;
                }
                case "#COMMENTNUMBER":
                {
                    System.out.println("#COMMENTNUMBER");
                    try {
                        String username = peer.dis.readUTF();
                        String channelName = peer.dis.readUTF();
                        File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
                        ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
                        User obj = (User) readSerializedObject.readObject();
                        System.out.println("obj for " + username);
                        int f = 0;
                        for(Channel ch : obj.channels)
                        {
                            if(ch.channelName.equals(channelName)) {
                                ch.totalNoOfComments += 1;
                                break;
                            }
                        }
                        System.out.println("Writting back");
                        ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                                new File(System.getProperty("user.home") + "/Hub/Client/" + username)));
                        writeSerializedObject.writeObject(obj);
                        writeSerializedObject.close();
                        System.out.println("Done Writting back object " + username);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "#GETSTATOFCHANNEL":
                {
                    System.out.println("#GETSTATOFCHANNEL");
                    getStatOfChannel();
                    break;
                }
                case "#ADDSUBSCRIBER":
                {
                    System.out.println("#ADDSUBSCRIBER");
                    String username = peer.dis.readUTF();
                    String subscriberName = peer.dis.readUTF();
                    String channelName = peer.dis.readUTF();
                    File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
                    ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
                    User obj = (User) readSerializedObject.readObject();
                    int f = 0;
                    for (Channel ch : obj.channels) {
                        if(ch.channelName.equals(channelName)){
                            synchronized (this) {
                                ch.channelSubscribers += 1;
                                ch.subscriberName.add(subscriberName);
                            }
                            f = 1;
                        }
                        if (f == 1)
                            break;
                    }
                    System.out.println("Subscriber added: "+subscriberName+" to channel "+channelName);
                    ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                            new File(System.getProperty("user.home") + "/Hub/Client/" + username)));
                    writeSerializedObject.writeObject(obj);
                    writeSerializedObject.close();
                    System.out.println("Done Writting back object " + username);
                    break;
                }
                case "#GETIP":
                {
                    System.out.println("#GETIP");
                    getIP();
                    break;
                }
                case "#PREMIUM":
                {
                    //TODO:according to downtime randomly select people and make store data in them
                    String targetUsername = peer.dis.readUTF();
                    String query = "SELECT ip FROM TABLE useripmap WHERE username='"+targetUsername+"';";
                    Statement stm = connect();
                    System.out.println("Conneted to db");
                    System.out.println(query);
                    ResultSet rs = stm.executeQuery(query);
                    System.out.println("rs found");
                    rs.next();
                    String targetIP = rs.getString(1);
                    try{
                        Socket targetSocket = new Socket(targetIP, 15001);
                        peer.dos.writeBoolean(true);
                        new Thread(new ReceiveAndStoreData(peer, targetUsername, targetIP)).start();
                        //new Thread(new SendData(new Peer()) -> do this at end of
                    } catch (ConnectException e) {
                        peer.dos.writeBoolean(false);
                        System.out.println("Connect exception");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    catch (UnknownHostException e)
                    {
                        peer.dos.writeBoolean(false);
                        System.out.println("UnknownHost exception");
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }
                default:
                    System.out.println("Ye kya flag hai "+peer.peerSocket.getInetAddress().getHostName()+" bhai: "+ flag);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        finally {
            try {
                peer.dis.close();
                peer.dos.close();
                peer.oos.close();
                peer.ois.close();
                peer.peerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String saveFile(String fileName, ObjectInputStream ois) throws Exception
    {
        String path1 = "";

        FileOutputStream fos = null;
        byte[] buffer = new byte[1024];
        // Read file name.
        Object o = null;
//        Object o = ois.readObject();
        path1 = System.getProperty("user.home") + "/Hub/Thumbnails/"+fileName+".png";
        fos = new FileOutputStream(path1);

        // Read file to the end.
        Integer bytesRead = 0;
        do {
            o = ois.readObject();
            if (!(o instanceof Integer)) {
                throwException("Something is wrong");
            }
            bytesRead = (Integer) o;
            o = ois.readObject();
            if (!(o instanceof byte[])) {
                throwException("Something is wrong");
            }
            buffer = (byte[]) o;
            // Write data to output file.
            fos.write(buffer, 0, bytesRead);
        } while (bytesRead == 1024);
        System.out.println("File transfer success");
        fos.close();
        return path1;
    }

    public static void throwException(String message) throws Exception {
        throw new Exception(message);
    }

    private void authenticate()
    {
        //TODO:Authenticate passwords and update IP address of people connecting.
        try {
            String username = peer.dis.readUTF();
            System.out.println("IN USERNAME WITH "+ username);
            Statement stm = connect();
            System.out.println("Conneted to db");
            System.out.println("SELECT * FROM useripmap WHERE username = '"+username+"';");
            ResultSet rs = stm.executeQuery("SELECT * FROM useripmap WHERE username = '"+username+"';");
            System.out.println("rs found");
            Boolean res = !(rs.first());
            System.out.println("calculated returning value "+res);
            peer.dos.writeBoolean(res);
            System.out.println("returned "+res);
            if(res)
                stm.executeUpdate("INSERT INTO useripmap VALUES("+"'"+username+"'"+","+"'"+peer.peerSocket.getInetAddress().getHostAddress()+"',"+"''"+","+"''"+","+"''"+","+"''"+","+"''"+")"+";");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void makeChannel() throws Exception
    {
        String username = peer.dis.readUTF();
        String channelName = peer.dis.readUTF();
        try {
            File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
            ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
            User obj = (User) readSerializedObject.readObject();
            System.out.println("obj for " + username);
            obj.isCreator = true;
            obj.channels.add(new Channel(channelName));
            System.out.println("Writting back");
            ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                    new File(System.getProperty("user.home") + "/Hub/Client/" + username)));
            writeSerializedObject.writeObject(obj);
            writeSerializedObject.close();
            System.out.println("Done Writting back object " + username);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void newUser() throws Exception
    {
        String username = peer.dis.readUTF();
        User user = new User(username);
        try {
            ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                    new File(System.getProperty("user.home") + "/Hub/Client/" + username)));
            writeSerializedObject.writeObject(user);
            writeSerializedObject.close();
        }catch (Exception e){
            System.out.println("Error in writing Serialized object");
            e.printStackTrace();
        }
        existingUser(username, 1);
    }

    private void existingUser(String username, int flag) throws Exception
    {
        //Send Video
        int f = 0;
        //ArrayList<String> paths = new ArrayList<>();
        File dir = new File(System.getProperty("user.home") + "/Hub/Client/");
        for (File usr : dir.listFiles()) {
            try {
                ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(usr));
                User obj = (User) readSerializedObject.readObject();
                if (obj.isCreator) {
                    f = 1;
                    break;
//                    for(Channel ch:obj.channels)
//                    {
//                        if(ch.videos.size() != 0)
//                        {
//                            f = 1;
//                            for(Video vid:ch.videos)
//                            {
//                                paths.add(vid.pathOfVideo);
//                            }
//                        }
//                    }
                }
            } catch (Exception e) {
                System.out.println("Error in reading serialized files");
                e.printStackTrace();
            }
        }
        if (f == 0) {
            peer.dos.writeUTF("#NOFILES");
            System.out.println("#NOFILES send");
        }
        else
        {
            HashMap<String, Video> recommendedData = null;
            String n = "12";
            if(flag == 1) {
                n = "16";
            }
            else
            {
                recommendedData = new HashMap<String, Video>();
                //TODO: make recommendedData
            }
            HashMap<String, Video> data = new HashMap<String, Video>();
            System.out.println("IN USERNAME WITH " + username);
            Statement stm = connect();
            System.out.println("Conneted to db");
            ResultSet rs = stm.executeQuery("SELECT * FROM paththumbnailmap ORDER BY RAND() LIMIT "+n+";");
            for (int i = 1; rs.next() && i <= Integer.parseInt(n); i++) {
                String path = rs.getString(1);
                String ownerName = rs.getString(3);
                String channelName = rs.getString(4);
                System.out.println(path+"\n"+ownerName+"\n"+channelName);
                try {
                    ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(new File(System.getProperty("user.home") + "/Hub/Client/"+

                            ownerName)));
                    User obj = (User) readSerializedObject.readObject();

                    for (Channel ch : obj.channels) {
                        if (ch.channelName.equals(channelName)) {
                            for (Video vid : ch.videos) {
                                if (vid.pathOfVideo.equals(path)) {
                                    System.out.println(vid.videoName);
                                }
                                data.put(vid.videoName, vid);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error in reading serialized files");
                    e.printStackTrace();
                }
            }
            peer.dos.writeUTF("#SENDING");
            //System.out.println(peer.peerSocket.getInetAddress().getHostName()+":11234");
            Thread.sleep(500);
            new Thread(new SendThumbnails(data, peer.peerSocket.getInetAddress().getHostName())).start();
            peer.oos.writeObject(recommendedData);
            peer.oos.writeObject(data);
        }

        //Send  login time notifications if any
        SendLoginTimeNotificationDaemon loginTime = new SendLoginTimeNotificationDaemon(username, peer.peerSocket.getInetAddress().getHostName());
        new Thread(loginTime).start();
    }

    private void addVideoInChannel() throws IOException
    {
        synchronized (this)
        {
            System.out.println("#ADDVIDEOINCHANNEL");
            String username = peer.dis.readUTF();
            String channelName = peer.dis.readUTF();
            int numberOfVideo = peer.dis.readInt();
            try {
                System.out.println("Opened starting connecting to db");
                Statement stm = connect();
                System.out.println("Conneted to db");
                File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
                ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
                User obj = (User) readSerializedObject.readObject();
                System.out.println("obj for " + username);
                ArrayList<Video> allVideos = new ArrayList<Video>();
                while(numberOfVideo-- > 0)
                {
                    String pathOfVideo = peer.dis.readUTF();
                    String videoName = pathOfVideo.substring(pathOfVideo.lastIndexOf("/")+1);
                    ArrayList<String> tagsList = (ArrayList<String>) peer.ois.readObject();
                    System.out.println("give image");
                    String pathOfthumbnail = saveFile(videoName, peer.ois);
                    System.out.println("Path of thumbnail "+pathOfthumbnail);
                    Video vid = new Video(pathOfVideo, username, pathOfthumbnail, tagsList, channelName);
                    allVideos.add(vid);
                    //obj.channels.get(obj.channels.indexOf(channelName)).videos.add(vid);
                    StringBuffer tagBuffer = new StringBuffer("");
                    String finalTag = "";
                    try {
                        if (!(tagsList == null)) {
                            for (String tag : tagsList)
                                tagBuffer.append(tag);
                        }
                        finalTag = tagBuffer.toString();
                    } catch (Exception e){
                        System.out.println("Andar se ayya hoon");
                        e.printStackTrace();
                    }
                    System.out.println("Query:- " + "INSERT INTO paththumbnailmap VALUES(" + "'" + pathOfVideo + "'" + "," + "'" + finalTag + "','" + username + "','" + channelName + "','" + vid.videoName + "')" + ";");
                    stm.executeUpdate("INSERT INTO paththumbnailmap VALUES(" + "'" + pathOfVideo + "'" + "," + "'" + finalTag + "','" + username + "','" + channelName + "','" + vid.videoName + "')" + ";");
                    System.out.println("Updated entry");
                }
                Channel notifierChannel = null;
                for (Channel ch : obj.channels) {
                    if (ch.channelName.equals(channelName))
                    {
                        notifierChannel = ch;
                        for(Video vid : allVideos)
                            ch.videos.add(vid);
                        break;
                    }
                }
                System.out.println("Writting back");
                ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                        new File(System.getProperty("user.home") + "/Hub/Client/" + username)));
                writeSerializedObject.writeObject(obj);
                writeSerializedObject.close();
                System.out.println("Done Writting back object " + username);
                //TODO: notify all subscribers , done but not tested
                System.out.println("allVideos "+allVideos);
                Notification notificationObject = new Notification(notifierChannel, allVideos);
                Thread notificationThread = new Thread(notificationObject);
                notificationThread.start();

            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void search() throws IOException, ClassNotFoundException, SQLException {
        ArrayList<String> searchWaliString = (ArrayList<String>) peer.ois.readObject();
        StringBuffer queryBuffer = new StringBuffer("SELECT * FROM paththumbnailmap WHERE ");
        for (int i = 0; i < searchWaliString.size() - 1; i++) {
            queryBuffer.append("name LIKE '%" + searchWaliString.get(i) + "%' OR ");
        }
        queryBuffer.append("name LIKE '%" + searchWaliString.get(searchWaliString.size() - 1) + "%';");
        String query = queryBuffer.toString();
        Statement stm = connect();
        System.out.println("Conneted to db");
        System.out.println(query);
        ResultSet rs = stm.executeQuery(query);
        System.out.println("rs found");

        ArrayList<Video> result = new ArrayList<Video>();
        while (rs.next()) {
            String pathOfVideo = rs.getString(1);
            String username = rs.getString(3);
            String channelName = rs.getString(4);
            try {
                File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
                ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
                User obj = (User) readSerializedObject.readObject();
                System.out.println("obj for " + username);
                int f = 0;
                for (Channel ch : obj.channels) {
                    if (ch.channelName.equals(channelName)) {
                        for (Video vid : ch.videos) {
                            if (vid.pathOfVideo.equals(pathOfVideo)) {
                                f = 1;
                                result.add(vid);
                                break;
                            }
                        }
                        if (f == 1)
                            break;
                    }
                }
                peer.oos.writeObject(result);
                peer.oos.flush();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void tagSearch() throws IOException, ClassNotFoundException, SQLException {
        ArrayList<String> tagsWaliString = (ArrayList<String>) peer.ois.readObject();
        StringBuffer queryBuffer = new StringBuffer("SELECT * FROM videos WHERE ");
        for (int i = 0; i < tagsWaliString.size() - 1; i++) {
            queryBuffer.append("tags LIKE '%" + tagsWaliString.get(i) + "%' OR ");
        }
        queryBuffer.append("tags LIKE '%" + tagsWaliString.get(tagsWaliString.size() - 1) + "%';");
        String query = queryBuffer.toString();
        Statement stm = connect();
        System.out.println("Conneted to db");
        System.out.println(query);
        ResultSet rs = stm.executeQuery(query);
        System.out.println("rs found");

        ArrayList<Video> result = new ArrayList<Video>();
        while (rs.next()) {
            String pathOfVideo = rs.getString(1);
            String username = rs.getString(3);
            String channelName = rs.getString(4);
            try {
                File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
                ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
                User obj = (User) readSerializedObject.readObject();
                System.out.println("obj for " + username);
                int f = 0;
                for (Channel ch : obj.channels) {
                    if (ch.channelName.equals(channelName)) {
                        for (Video vid : ch.videos) {
                            if (vid.pathOfVideo.equals(pathOfVideo)) {
                                f = 1;
                                result.add(vid);
                                break;
                            }
                        }
                        if (f == 1)
                            break;
                    }
                }
                peer.oos.writeObject(result);
                peer.oos.flush();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void setLikes()
    {
        try {
            String username = peer.dis.readUTF();
            String channelName = peer.dis.readUTF();
            String videoName = peer.dis.readUTF();
            //Video vid = (Video) peer.ois.readObject();
            File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
            ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
            User obj = (User) readSerializedObject.readObject();
            System.out.println("obj for " + username);
            String event = peer.dis.readUTF();
            if(event.equals("#ADD"))
            {
                int f = 0;
                for (Channel ch : obj.channels) {
                    if(ch.channelName.equals(channelName)){
                        for (Video v : ch.videos) {
                            if (v.videoName.equals(videoName)) {
                                synchronized (this) {
                                    v.numberOfLikes += 1;
                                    ch.totalNoOfLikes += 1;
                                }
                                f = 1;
                                break;
                            }
                        }
                    }
                    if (f == 1)
                        break;
                }
            }
            else if(event.equals("#SUB"))
            {
                int f = 0;
                for (Channel ch : obj.channels) {
                    if(ch.channelName.equals(channelName)){
                        for (Video v : ch.videos) {
                            if (v.videoName.equals(videoName)) {
                                synchronized (this) {
                                    v.numberOfLikes -= 1;
                                    ch.totalNoOfLikes -= 1;
                                }
                                f = 1;
                                break;
                            }
                        }
                    }
                    if (f == 1)
                        break;
                }
            }
            System.out.println("Writting back");
            ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                    new File(System.getProperty("user.home") + "/Hub/Client/" + username)));
            writeSerializedObject.writeObject(obj);
            writeSerializedObject.close();
            System.out.println("Done Writting back object " + username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getStatOfChannel()
    {
        try {
            double rating = 0;
            String username = peer.dis.readUTF();
            String channelName = peer.dis.readUTF();
            File file = new File(System.getProperty("user.home") + "/Hub/Client/" + username);
            ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
            User obj = (User) readSerializedObject.readObject();
            System.out.println("obj for " + username);
            int f = 0;
            Channel requestedChannel = null;
            for (Channel ch : obj.channels) {
                if (ch.channelName.equals(channelName)) {
                    requestedChannel = ch;
                    rating = 0.5*ch.channelSubscribers + 0.25*ch.totalNoOfComments + 0.25*ch.totalNoOfLikes;
                    break;
                }
            }
            peer.dos.writeDouble(rating);
            peer.dos.writeInt(requestedChannel.totalNoOfLikes);
            peer.dos.writeInt(requestedChannel.totalNoOfComments);
            peer.dos.writeInt(requestedChannel.channelSubscribers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getIP() throws IOException, SQLException {
        String username = peer.dis.readUTF();
        String query = "SELECT ip FROM useripmap WHERE username = '"+username+"';";
        Statement stm = connect();
        System.out.println(query);
        ResultSet rs = stm.executeQuery(query);
        rs.next();
        String ip = rs.getString(1);
        System.out.println("Sending back ip "+ip);
        peer.dos.writeUTF(ip);
    }
}
