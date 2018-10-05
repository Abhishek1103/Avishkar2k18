package server;

import data.User;
import javafx.util.Pair;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static constants.Constants.USER_HOME;

public class ChatHandler implements Runnable
{
    User user;

    public ChatHandler(User user)
    {
        this.user = user;
    }

    @Override
    public void run()
    {
        try {
            String flag = user.getDis().readUTF();
            switch(flag) {
                case "#SENDMESSAGE":
                {
                    System.out.println("#SENDMESSAGE");
                    String from = user.getDis().readUTF();
                    String to = user.getDis().readUTF();
                    String message = user.getDis().readUTF();
                    boolean isMessageForTeacher = user.getDis().readBoolean();

                    SQLConnection connection = new SQLConnection();
                    Statement stm = connection.connect();
                    String query;
                    if (isMessageForTeacher)
                        query = "SELECT ip FROM teacheripmap WHERE teachername='" + to + "'";
                    else
                        query = "SELECT ip FROM studentipmap WHERE studentname='" + to + "'";

                    ResultSet rs = stm.executeQuery(query);
                    rs.next();
                    String toWaleKiIP = rs.getString(1);
                    if (isMessageForTeacher)
                        Chat.userListToNotify.add(new Pair<>(toWaleKiIP, 1+from+":"+to+":"+message));
                    else
                        Chat.userListToNotify.add(new Pair<>(toWaleKiIP, 0+to+":"+from+":"+message));
                    break;
                }
                //Only student wil have log saved with teacher name as file
                //When teacher send request for log format: studentname and then his name and visa versa for student
                case "#GETLOGCOPY":
                {
                    System.out.println("#GETLOGCOPY");
                    String from = user.getDis().readUTF();
                    String to = user.getDis().readUTF();
                    System.out.println("from="+from+"\tto="+to);
                    File file = new File(USER_HOME+"/SquizServer/Student/"+from+"/"+to);
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String t;
                        StringBuffer msg = new StringBuffer();
                        while ((t = br.readLine()) != null) {
                            msg.append(t);
                        }
                        user.getDos().writeUTF(msg.toString());
                    }
                    catch (FileNotFoundException ff){
                        user.getDos().writeUTF("");
                    }
                    break;
                }
                default:
                    System.out.println("Ye kya flag hai "+ flag);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
