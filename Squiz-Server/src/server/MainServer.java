package server;


import data.User;
import encryption.AES;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class MainServer
{
    public static void main(String[] args) throws IOException {

        ServerSocket connection = new ServerSocket(7001);

        new Thread(new Chat()).start();


        while(true)
        {
            System.out.println("Waiting for connection");
            Socket newConnection = connection.accept();
            User user = new User(newConnection);

            boolean isTeacher = user.getDis().readBoolean();
            System.out.println(isTeacher+"---");
            if (isTeacher)
            {
                System.out.println("Is teacher");
                new Thread(new TeacherHandler(user)).start();
            }
            else
            {
                System.out.println("Is student");
                new Thread(new StudentHandler(user)).start();
            }
        }
    }
}
