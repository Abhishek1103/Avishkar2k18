/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Serialization;

import java.io.Serializable;

/**
 *
 * @author aks
 */
public class A implements Serializable{
    static int i;  // static member not part of object's state, hence not serialized
    int a;
    String str;
     A(int _a, String _str){
        this.a = _a;
        this.str = _str;
        this.i = _a*10;
    }
}
