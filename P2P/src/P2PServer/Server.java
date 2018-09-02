package P2PServer;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server
{
    protected static int numberOfConnections = 0;
    private static String ftpUsername;

    public static String getFtpUsername() {
        return ftpUsername;
    }

    public static void setFtpUsername(String ftpUsername) {
        Server.ftpUsername = ftpUsername;
    }

    public static String getFtpPasswd() {
        return ftpPasswd;
    }

    public static void setFtpPasswd(String ftpPasswd) {
        Server.ftpPasswd = ftpPasswd;
    }

    private static String ftpPasswd;

    public static void main(String[] args) throws IOException, InterruptedException {
        Thread people[] = new Thread[5];// For interrupting when there is a sudden closing of application

        //Start the ftp service

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JLabel label1 = new JLabel("Enter your password:");
        JLabel label2 = new JLabel("Enter your username:");
        JTextField user = new JTextField(30);
        JPasswordField pass = new JPasswordField(30);
        panel1.add(label1);
        panel1.add(pass);
        panel2.add(label2);
        panel2.add(user);
        String[] options = new String[]{"OK", "Cancel", "Exit"};
        Runtime runtime = Runtime.getRuntime();
        while(true) {
            try {
                int option = JOptionPane.showOptionDialog(null, panel2, "Username and Password", JOptionPane.NO_OPTION,
                        JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
                if (option == 0) // pressing OK button
                {
                    String username = user.getText();
                    setFtpUsername(username);
                }
                else if(option == 1) {
                    JOptionPane.showMessageDialog(null, "You must specify a username");
                }
                else {
                    System.exit(0);
                }

                option = JOptionPane.showOptionDialog(null, panel1, "Username and Password", JOptionPane.NO_OPTION,
                        JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
                if (option == 0) // pressing OK button
                {
                    String username = user.getText(), passwd = pass.getText();
                    setFtpUsername(username);
                    setFtpPasswd(passwd);
                    String command = "echo " + getFtpPasswd() + " | sudo -S /etc/init.d/vsftpd restart";
                    String[] cmd = {"/bin/bash", "-c", command};
                    Process process = runtime.exec(cmd, null, new java.io.File("/home/" + getFtpUsername()));
                    Scanner z = new Scanner(process.getInputStream());
                    process.waitFor();
                    if (!z.hasNext()) {
                        JOptionPane.showMessageDialog(null, "Sorry you entered wrong username or password.");
                    }
                    else {
                        break;
                    }

                }
                else if(option == 1) {
                    JOptionPane.showMessageDialog(null, "You must specify a password name");
                }
                else {
                    System.exit(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println(getFtpUsername()+"\n"+getFtpPasswd());
        System.exit(0);
        ServerSocket incomingConnection = new ServerSocket(15001);
        while(true) {
            Socket socket = incomingConnection.accept();
            Peer peer = new Peer(socket);
            if (numberOfConnections < 5) {
                peer.dos.writeBoolean(true);
                people[numberOfConnections] = new Thread(new HandlePeer(peer));
                numberOfConnections++;
            } else {
                peer.dos.writeBoolean(false);
                peer.dos.close();
                peer.dis.close();
                peer.oos.close();
                peer.ois.close();
                peer.peerSocket.close();
            }
        }
        //Listen for closing of appliction -> if yes people[index].interrupt();
    }
}
