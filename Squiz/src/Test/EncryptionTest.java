package Test;

import javafx.scene.shape.Circle;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

public class EncryptionTest {

    static SecretKey secretKey;
    private static final String transformation = "AES/ECB/PKCS5Padding";


    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException, ClassNotFoundException {

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

        SecureRandom secureRandom = new SecureRandom();
        int keyBitSize = 256;
        keyGenerator.init(keyBitSize, secureRandom);

        secretKey = keyGenerator.generateKey();

        System.out.println(""+secretKey.toString());

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);



        byte[] plainText = "My name is Abhishek Kumar Singh".getBytes("UTF-8");
        byte[] cipherText = cipher.doFinal(plainText);

        System.out.println(""+cipherText.toString());

        Cipher decCipher = Cipher.getInstance("AES");
        decCipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] pltT = decCipher.doFinal(cipherText);
        String res = new String(pltT);
        System.out.println("---"+res+"---");


        ArrayList<String> list = new ArrayList<>();
        list.add("Anthony");
        list.add("Rick");
        list.add("Morty");
        list.add("Tom");
        list.add("Jerry");
        System.out.println(list);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encrypt(list, baos);

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("/home/surbhit/Desktop/enc"));
        oos.writeObject(baos.toByteArray());

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("/home/surbhit/Desktop/enc"));
        byte[] b = (byte[])ois.readObject();

        //ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ArrayList<String> l = (ArrayList<String>) decrypt(bais);

        System.out.println(l);



    }

    public static void encrypt(Serializable object, OutputStream ostream) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        try {
            // Length is 16 byte
            //SecretKeySpec sks = new SecretKeySpec(secretKey., transformation);

            // Create cipher
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            SealedObject sealedObject = new SealedObject(object, cipher);

            // Wrap the output stream
            CipherOutputStream cos = new CipherOutputStream(ostream, cipher);
            ObjectOutputStream outputStream = new ObjectOutputStream(cos);
            outputStream.writeObject(sealedObject);
            outputStream.close();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }


    public static Object decrypt(InputStream istream) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        CipherInputStream cipherInputStream = new CipherInputStream(istream, cipher);
        ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream);
        SealedObject sealedObject;
        try {
            sealedObject = (SealedObject) inputStream.readObject();
            return sealedObject.getObject(cipher);
        } catch (ClassNotFoundException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }


}
