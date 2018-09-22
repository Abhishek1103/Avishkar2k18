package Test;


import java.net.*;
import java.io.*;
import java.util.Arrays;

public class Server
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket connection = new ServerSocket(7000);

        Socket client = connection.accept();
        DataOutputStream dos = new DataOutputStream(client.getOutputStream());
        DataInputStream dis = new DataInputStream(client.getInputStream());

        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());

        /*INSERT CODE HERE*/

        sendFile("file name", oos);
        connection.close();
    }

    private static void sendFile(String path, ObjectOutputStream oos)
    {

        try {
            System.out.println("In send file");

            String file_name = path;
            System.out.println("Input path= " + path);
            File file = new File(file_name);

            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            Integer bytesRead = 0;
            System.out.println("Sending file");
            while ((bytesRead = fis.read(buffer)) > 0) {
                //System.out.println("BytesRead = " + bytesRead);
                oos.writeObject(bytesRead);
                //System.out.println("These many bytes are send");
                oos.writeObject(Arrays.copyOf(buffer, buffer.length));
                //System.out.println("Going for next iteration");
            }

            oos.flush();
            System.out.println("File sent");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
