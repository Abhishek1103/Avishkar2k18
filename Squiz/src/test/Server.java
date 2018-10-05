package test;


import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.*;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Arrays;

public class Server
{
    static SecretKey secretKey;
    private static final String transformation = "AES";

    public static void main(String[] args) throws Exception {
        ServerSocket connection = new ServerSocket(7000);

        Socket client = connection.accept();
        DataOutputStream dos = new DataOutputStream(client.getOutputStream());
        DataInputStream dis = new DataInputStream(client.getInputStream());

        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());


        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();


        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec rsaPublicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
        RSAPrivateKeySpec rsaPrivateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);

        saveKeys("/home/aks/Desktop/Public.key", rsaPublicKeySpec.getModulus(),rsaPublicKeySpec.getPublicExponent());
        saveKeys("/home/aks/Desktop/Private.key", rsaPrivateKeySpec.getModulus(), rsaPrivateKeySpec.getPrivateExponent());

        sendFile("/home/aks/Desktop/Public.key", oos);
        String pathOfSurbhitPublicKey = saveFile("/home/aks/Desktop/SurPub.key", ois);

        PublicKey surbhitPublicKey = readPublicKeyFromFile("/home/aks/Desktop/SurPub.key");

        Cipher cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher1.init(Cipher.ENCRYPT_MODE, publicKey);

        String plain = "My name is Abhishek";
        byte[] b = cipher1.doFinal(plain.getBytes());

        Cipher deCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        deCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] p = deCipher.doFinal(b);
        String s = new String(p);
        System.out.println("----"+s+"---------");


        /*INSERT CODE HERE*/



        Cipher cipher = Cipher.getInstance("AES");

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

        SecureRandom secureRandom = new SecureRandom();
        int keyBitSize = 256;
        keyGenerator.init(keyBitSize, secureRandom);

        secretKey = keyGenerator.generateKey();
        //
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        encryptRSA(secretKey, baos, "RSA", surbhitPublicKey);

        int[] arr = {1,2,3,4};

        oos.writeObject(arr);
        oos.flush();

        byte[] bt = encryptSecretKey(secretKey, surbhitPublicKey);

        oos.writeObject(bt);
        oos.flush();
        System.out.println("AES Secret key encrypted and written to Surbhit");

        System.out.println(dis.readBoolean());

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
        encrypt(list, baos, transformation, secretKey);

        oos.writeObject(baos.toByteArray());
        System.out.println("Encrypted Array list sent to surbhit");

        connection.close();
    }

    private static void sendFile(String path, ObjectOutputStream oos) {

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

    public static void encrypt(Serializable object, OutputStream ostream, String algo, SecretKey secretKey) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        try {
            // Length is 16 byte
            //SecretKeySpec sks = new SecretKeySpec(secretKey., transformation);

            // Create cipher
            Cipher cipher = Cipher.getInstance(algo);
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

    public static void encryptRSA(Serializable object, OutputStream ostream, String algo, PublicKey publicKey) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        try {
            // Length is 16 byte
            //SecretKeySpec sks = new SecretKeySpec(secretKey., transformation);

            // Create cipher
            Cipher cipher = Cipher.getInstance(algo);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
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


    public static Object decrypt(InputStream istream, String algo) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        Cipher cipher = Cipher.getInstance(algo);
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

    private static PublicKey readPublicKeyFromFile(String fileName) throws IOException {

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        PublicKey publicKey = null;

        try{

            fis = new FileInputStream(new File(fileName));
            ois = new ObjectInputStream(fis);
            BigInteger modulus = (BigInteger) ois.readObject();
            BigInteger exponent = (BigInteger) ois.readObject();

            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            publicKey = fact.generatePublic(rsaPublicKeySpec);


        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(ois!=null){
                ois.close();
                if(fis != null){
                    fis.close();
                }
            }
            return publicKey;
        }
    }


    private static PrivateKey readPrivateKeyFromFile(String fileName) throws IOException {

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        PrivateKey privateKey = null;

        try{

            fis = new FileInputStream(new File(fileName));
            ois = new ObjectInputStream(fis);
            BigInteger modulus = (BigInteger) ois.readObject();
            BigInteger exponent = (BigInteger) ois.readObject();

            RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            privateKey = fact.generatePrivate(rsaPrivateKeySpec);


        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(ois!=null){
                ois.close();
                if(fis != null){
                    fis.close();
                }
            }
            return privateKey;
        }
    }

    static void saveKeys(String fileName, BigInteger mod, BigInteger exp) throws IOException {
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


    private static String saveFile(String fileName, ObjectInputStream ois) throws Exception
    {
        String path1 = fileName;

        FileOutputStream fos = null;
        byte[] buffer = new byte[1024];
        // Read file name.
        Object o = null;
        path1 =fileName;
        fos = new FileOutputStream(fileName);

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

    private static SecretKey decryptAESKey(byte[] data, PrivateKey privateKey )
    {
        SecretKey key = null;
        PrivateKey privKey = null;
        Cipher cipher = null;

        try
        {
            // this is OUR private key
            privKey = privateKey;

            // initialize the cipher...
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privKey );

            // generate the aes key!
            key = new SecretKeySpec( cipher.doFinal(data), "AES" );
        }
        catch(Exception e)
        {
            System.out.println ( "exception decrypting the aes key: "
                    + e.getMessage() );
            return null;
        }

        return key;
    }
}
