package data;

import java.io.*;
import java.net.Socket;

public class User
{
    private Socket userSocket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public User(Socket socket) throws IOException {
        this.userSocket = socket;
        dos = new DataOutputStream(userSocket.getOutputStream());
        dis = new DataInputStream(userSocket.getInputStream());
        oos = new ObjectOutputStream(userSocket.getOutputStream());
        ois = new ObjectInputStream(userSocket.getInputStream());
    }

    public Socket getUserSocket() {
        return userSocket;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public DataInputStream getDis() {
        return dis;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public ObjectInputStream getOis() {
        return ois;
    }
}
