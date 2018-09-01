package Test;

public class ThumbTest {

    public static void main(String[] args) throws Exception {
        generateThumbnail("/home/aks/Videos/Berklee.mp4");
        System.exit(0);


    }

    static void generateThumbnail(String path) throws Exception{
        String time = "00:00:20";
        String outPath = System.getProperty("user.home") + "/Desktop/out.png";
        String command = "ffmpeg "+" -ss "+time+" -i "+path+" -vf scale=-1:120  -vcodec png "+outPath;
        System.out.println(command);
        Runtime run = Runtime.getRuntime();
        Process p = run.exec(command);
        p.waitFor();

    }
}

