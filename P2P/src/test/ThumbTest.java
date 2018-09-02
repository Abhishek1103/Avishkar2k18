package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.BufferPoolMXBean;

public class ThumbTest {

    public static void main(String[] args) throws Exception {
        String path = "/home/aks/Videos/Berklee.mp4";
        String vidName = path.substring(path.lastIndexOf("/")+1);
        String firstPath = path.substring(0, path.lastIndexOf("/")+1);
        System.out.println(vidName+"\n"+firstPath);
        generateThumbnail("/home/aks/Videos/Berklee.mp4");
        System.exit(0);


    }

    static void generateThumbnail(String path) throws Exception{
        String vidName = path.substring(path.lastIndexOf("/")+1);
        String firstPath = path.substring(0, path.lastIndexOf("/")+1);
        String time = "00:00:20";
        String outPath = System.getProperty("user.home") + "/Desktop/out.png";
        String command = "ffmpeg -ss 00:00:20 -i '/home/surbhit/Videos/KHAABFEATPARMISHVERMA.mp4' -vf scale=-1:120 -vframes 1 -vcodec png /home/surbhit/Desktop/out.png";
        System.out.println(command);
        Runtime run = Runtime.getRuntime();
        Process p = run.exec("ffmpeg");
        p.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String msg;
        while((msg=br.readLine())!=null)
            System.out.println(msg);
        System.out.println("Done");
    }
}