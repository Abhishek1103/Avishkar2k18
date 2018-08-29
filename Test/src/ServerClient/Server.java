/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author aks
 */
public class Server {
    
    public static void main(String[] args) throws Exception {
        ServerSocket ssock = new ServerSocket(5001);
        
        Socket sock = ssock.accept();
        
        DataInputStream dis = new DataInputStream(sock.getInputStream());
        DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
        
        while(true){
            String msg = dis.readUTF();
            System.out.println("Client: "+msg);
        }
    }
    
}
