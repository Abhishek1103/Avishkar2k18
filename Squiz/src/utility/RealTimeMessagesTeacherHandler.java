package utility;

import constants.Constants;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import ui.ChatFrameController;
import ui.MainTeacherPageController;

import java.io.*;
import java.net.Socket;

public class RealTimeMessagesTeacherHandler extends Service {

    private Socket sock;
    private String username;

    public RealTimeMessagesTeacherHandler(Socket socket){
        this.sock = socket;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try{
                    DataInputStream dis = new DataInputStream(sock.getInputStream());
                    DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());

                    String msgLogPath = Constants.USER_DIR+"chats/"+username+".log";
                    PrintWriter pw = new PrintWriter(new FileWriter(msgLogPath));
                    username = dis.readUTF();
                    MainTeacherPageController.queryUsername.put(username, username);
                    String msg = dis.readUTF();
                    while(!msg.equals("#END")){

                        if(MainTeacherPageController.currentChatUser.equals(username)){
                            ChatFrameController.appendToTextArea(msg);
                        }
                        pw.append(msg);

                        msg = dis.readUTF();
                    }
                    pw.close();

                }catch (Exception e){
                    e.printStackTrace();
                }



                return null;
            }
        };
    }
}
