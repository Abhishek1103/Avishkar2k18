package Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Client
{
    public static void main(String[] args) throws Exception
    {
        Socket connection = new Socket("127.0.0.1", 7000);
        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
        DataInputStream dis = new DataInputStream(connection.getInputStream());

        ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());

        /*INSERT CODE HERE*/

        String fileName = "Public.key";
        saveFile(fileName,ois);
        connection.close();

    }

    private static String saveFile(String fileName, ObjectInputStream ois) throws Exception
    {
        String path1 = "";

        FileOutputStream fos = null;
        byte[] buffer = new byte[1024];
        // Read file name.
        Object o = null;
        path1 = System.getProperty("user.home") + "/Hub/Thumbnails/"+fileName;
        fos = new FileOutputStream(path1);

        // Read file to the end.
        Integer bytesRead = 0;
        do {
            o = ois.readObject();
            if (!(o instanceof Integer)) {
                throwException("Something is wrong");
            }
            bytesRead = (Integer) o;
            o = ois.readObject();
            if (!(o instanceof byte[])) {
                throwException("Something is wrong");
            }
            buffer = (byte[]) o;
            // Write data to output file.
            fos.write(buffer, 0, bytesRead);
        } while (bytesRead == 1024);
        System.out.println("File transfer success");
        fos.close();
        return path1;
    }

    public static void throwException(String message) throws Exception {
        throw new Exception(message);
    }
}