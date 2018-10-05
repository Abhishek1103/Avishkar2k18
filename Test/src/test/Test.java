/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;

/**
 *
 * @author aks
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        listFiles("");
    }
    
    static void listFiles(String dirName) throws Exception{
        File f = new File("/home/aks/Documents");
        
        File[] fList = f.listFiles();
        
        for(File file : fList){
            if(file.isFile())
                System.out.println(""+file.getName());
            else System.err.println(""+file.getName());
        }
    }
}
