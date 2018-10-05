/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Serialization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author aks
 */
public class B{
    public static void main(String[] args){
        File f = new File(System.getProperty("user.home")+"/Desktop/serial");
    
        A obj = new A(5, "Hello World");
        System.out.println(""+obj.i);
    
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            
            oos.writeObject(obj);
            
            oos.close();
            
        }catch(Exception e){
            System.out.println(""+e);
        }
    }
}
