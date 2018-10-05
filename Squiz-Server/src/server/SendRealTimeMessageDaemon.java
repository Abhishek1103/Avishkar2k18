package server;

import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import static constants.Constants.USER_HOME;

public class SendRealTimeMessageDaemon implements Runnable
{

    @Override
    public void run()
    {

        while(true)
        {
            Pair<String, String> ipMsgPair = null;
            synchronized (this)
            {
                ipMsgPair = Chat.userListToNotify.poll();
            }

            if(ipMsgPair != null)
            {

                //MAKE a log file for that studnet teacher pair
                //If already exist append to the file

                System.out.println("In real notification thread");
                String ip = ipMsgPair.getKey();
                String message = ipMsgPair.getValue();
                int isMessageForTeacher = message.charAt(0) - '0';
                String nameOfStudent = message.substring(1,message.indexOf(":"));
                String nameOfTeacher = message.substring(message.indexOf(":")+1, message.lastIndexOf(":"));
                String msg  = message.substring(message.lastIndexOf(":")+1);

                try {
                    System.out.println("Making connection");
                    Socket socket = new Socket(ip, 13002);
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());


                    if(isMessageForTeacher == 1)
                        dos.writeUTF(nameOfStudent);
                    else
                        dos.writeUTF(nameOfTeacher);
                    dos.writeUTF(msg);
                    dos.writeUTF("#END");
                    File file = new File(USER_HOME+"/SquizServer/Student/"+nameOfStudent+"/"+nameOfTeacher);
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                    if(!file.exists())
                    {
                        bw.write(msg+"\n");
                        bw.close();
                    }
                    else
                    {
                        bw.append(msg+"\n");
                        bw.close();
                    }
                    System.out.println("Log file updated");
                    dis.close();
                    dos.close();
                    socket.close();
                }
                catch (Exception e)
                {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                    try {
                        File file = new File(USER_HOME + "/SquizServer/Student/" + nameOfStudent + "/" + nameOfTeacher);
                        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                        if (!file.exists()) {
                            bw.write(msg + "\n");
                            bw.close();
                        } else {
                            bw.append(msg + "\n");
                            bw.close();
                        }
                        System.out.println("Log file updated");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    synchronized (this) {
                        System.out.println("Adding to unsent message list");
                        Chat.unSentMessagesUserIpList.add(ip);
                        Queue<String> obj = Chat.ipMsgMap.get(ip);
                        if(obj == null)
                        {
                            Queue<String> msgs = new LinkedList<>();
                            if(isMessageForTeacher == 1)
                                msgs.add(nameOfStudent);
                            else
                                msgs.add(nameOfTeacher);
                            Chat.ipMsgMap.put(ip,msgs);
                        }
                        else
                        {
                            if(isMessageForTeacher == 1)
                                obj.add(nameOfStudent);
                            else
                                obj.add(nameOfTeacher);
                            Chat.ipMsgMap.put(ip, obj);
                        }
                    }
                    System.out.println("Out of catch");
                }
            }
        }
    }
}
