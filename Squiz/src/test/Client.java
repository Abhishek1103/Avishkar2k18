package test;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.*;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Arrays;

public class Client
{
    private static final String transformation = "AES";

    public static void main(String[] args) throws Exception
    {
        Socket connection = new Socket("172.31.85.85", 7000);
        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
        DataInputStream dis = new DataInputStream(connection.getInputStream());

        ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());

        String fileName = "/home/surbhit/Desktop/Abhishek_Public.key";
        String pathOfPublicKeyOfAbhishek = saveFile(fileName,ois);

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();


        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec rsaPublicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
        RSAPrivateKeySpec rsaPrivateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);

        saveKeys("/home/surbhit/Desktop/my_Public.key", rsaPublicKeySpec.getModulus(),rsaPublicKeySpec.getPublicExponent());
        saveKeys("/home/surbhit/Desktop/my_Private.key", rsaPrivateKeySpec.getModulus(), rsaPrivateKeySpec.getPrivateExponent());

        Cipher cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher1.init(Cipher.ENCRYPT_MODE, publicKey);

        String plain = "My name is Abhishek";
        byte[] b = cipher1.doFinal(plain.getBytes());

        Cipher deCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        deCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] p = deCipher.doFinal(b);
        String s = new String(p);
        System.out.println("----"+s+"---------");

        sendFile("/home/surbhit/Desktop/my_Public.key", oos);

        int a[] = (int[]) ois.readObject();
        System.out.println(a);

        System.out.println("RSA decrpytion staring");
        byte[] RSAEncryptedAESKey = (byte []) ois.readObject();
        dos.writeBoolean(true);
        //ByteArrayInputStream baisRSA = new ByteArrayInputStream(RSAEncryptedAESKey);
        SecretKey AESKey = (SecretKey) decryptAESKey(RSAEncryptedAESKey, privateKey);
        System.out.println("RSA decrpytion done");

        System.out.println("AES decrpytion staring");
        byte[] objectEncrpytedWithAES = (byte []) ois.readObject();
        System.out.println("objectEncrpytedWithAES received");
        ByteArrayInputStream baisAES = new ByteArrayInputStream(objectEncrpytedWithAES);
        ArrayList<String> decrptedList = (ArrayList<String>) decryptAES(baisAES, AESKey);
        System.out.println("AES decrpytion done");
        System.out.println(decrptedList);

        connection.close();

        /*INSERT CODE HERE*/

    }

    private static  byte[] encryptSecretKey (SecretKey skey, PublicKey publicKey)
    {
        Cipher cipher = null;
        byte[] key = null;

        try
        {
            // initialize the cipher with the user's public key
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey );
            key = cipher.doFinal(skey.getEncoded());
        }
        catch(Exception e )
        {
            System.out.println ( "exception encoding key: " + e.getMessage() );
            e.printStackTrace();
        }
        return key;
    }

    private static SecretKey decryptAESKey(byte[] data, PrivateKey privateKey ) {
        SecretKey key = null;
        PrivateKey privKey = null;
        Cipher cipher = null;

        try {
            // this is OUR private key
            privKey = privateKey;

            // initialize the cipher...
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privKey);

            // generate the aes key!
            key = new SecretKeySpec(cipher.doFinal(data), "AES");
        } catch (Exception e) {
            System.out.println("exception decrypting the aes key: "
                    + e.getMessage());
            return null;
        }

        return key;
    }

    public static Object decryptAES(InputStream istream, SecretKey AESKey) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, AESKey);

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
//
//    public static Object decryptRSA(InputStream istream, PrivateKey my_private_key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
//
//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        cipher.init(Cipher.UNWRAP_MODE, my_private_key);
//
//        CipherInputStream cipherInputStream = new CipherInputStream(istream, cipher);
//        ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream);
//        SealedObject sealedObject;
//        try {
//            sealedObject = (SealedObject) inputStream.readObject();
//            return sealedObject.getObject(cipher);
//        } catch (ClassNotFoundException | IllegalBlockSizeException | BadPaddingException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    private static void saveKeys(String fileName, BigInteger mod, BigInteger exp) throws IOException {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try{
            fos = new FileOutputStream(fileName);
            oos = new ObjectOutputStream(new BufferedOutputStream(fos));

            oos.writeObject(mod);
            oos.writeObject(exp);


        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(oos != null){
                oos.close();
                if(fos != null){
                    fos.close();
                }
            }
        }
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

    private static String saveFile(String fileName, ObjectInputStream ois) throws Exception
    {
        String path1 = "";

        FileOutputStream fos = null;
        byte[] buffer = new byte[1024];
        // Read file name.
        Object o = null;
        path1 =fileName;
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