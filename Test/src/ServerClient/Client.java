/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author aks
 */
public class Client {
    
    public static void main(String[] args) throws Exception{
        Socket sock = new Socket("172.31.85.5", 13000);
        System.out.println("Connected");
        DataInputStream dis = new DataInputStream(sock.getInputStream());
        DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
        
        String msg;
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.print("Enter msg: ");
            msg = sc.nextLine();
            dout.writeUTF(msg);
        }
    }
}
