/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VLCj;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;





/**
 *
 * @author aks
 */
public class VLCPlayer {
    private static EmbeddedMediaPlayerComponent mediaPlayerComponent;
    
    //static String VLCLIBPATH = "/usr/bin/";
    
    public static void main(String[] args) {
        JFrame jf = new JFrame();
        jf.setSize(600,400);
        jf.setLayout(new BorderLayout());
        
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        jf.add(mediaPlayerComponent, BorderLayout.CENTER);
        jf.setVisible(true);
        
        mediaPlayerComponent.getMediaPlayer().playMedia("/home/aks/Videos/Installing Arch Linux (Standard Procedure)-lizdpoZj_vU.mp4","--no-xlib");
        
        mediaPlayerComponent.getMediaPlayer().play();
        
    }
}
