/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 *
 * @author aks
 */
public class C {
    public static void main(String[] args){
        File f = new File(System.getProperty("user.home")+"/Desktop/serial");
        
        try{
            
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            
            A obj = (A)ois.readObject();
            
            System.out.println(""+obj.str+" "+obj.a+" "+obj.i);
            
            ois.close();
            
        }catch(Exception e){
            System.out.println(""+e);
        }
        
    }
}
