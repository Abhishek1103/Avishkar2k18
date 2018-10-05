package test;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSAEncryptionTest {

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, IOException {


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


        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

        SecureRandom secureRandom = new SecureRandom();
        int keyBitSize = 256;
        keyGenerator.init(keyBitSize, secureRandom);

        SecretKey secretKey = keyGenerator.generateKey();


        byte[] encrptedAESKey = encryptSecretKey(secretKey, publicKey);

        SecretKey decryptedSecKey = decryptAESKey(encrptedAESKey, privateKey);

        System.out.println(decryptedSecKey);

//        byte[] encrypted = wrapKey(publicKey, secretKey);



//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        encryptRSA(secretKey, baos, publicKey);
//
//        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
//        SecretKey l = (SecretKey) decryptRSA(bais, privateKey);


//        Cipher cipher = Cipher.getInstance("RSA");
//        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//
//        String plain = "My name is Abhishek";
//        byte[] b = cipher.doFinal(plain.getBytes());
//
//        Cipher deCipher = Cipher.getInstance("RSA");
//        deCipher.init(Cipher.DECRYPT_MODE, privateKey);
//        byte[] p = deCipher.doFinal(b);
//        String s = new String(p);
//        System.out.println("----"+s+"---------");
    }


    public static Object decryptRSA(InputStream istream, PrivateKey privateKey) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

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


    public static void encryptRSA(Serializable object, OutputStream ostream, PublicKey publicKey) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        try {
            // Length is 16 byte
            //SecretKeySpec sks = new SecretKeySpec(secretKey., transformation);

            // Create cipher
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
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


    public static byte[] wrapKey(PublicKey pubKey, SecretKey symKey)
            throws InvalidKeyException, IllegalBlockSizeException {
        try {
            final Cipher cipher = Cipher
                    .getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher.init(Cipher.WRAP_MODE, pubKey);
            final byte[] wrapped = cipher.wrap(symKey);
            return wrapped;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(
                    "Java runtime does not support RSA/ECB/OAEPWithSHA1AndMGF1Padding",
                    e);
        }
    }

//    SecretKey unWrapKey(PrivateKey privateKey, byte[] wrappedKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
//        final Cipher cipher = Cipher
//                .getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
//        cipher.init(Cipher.UNWRAP_MODE, privateKey);
//        //final byte[] wrapped = cipher.wrap(symKey);
//        SecretKey secretKey = (SecretKey)(cipher.unwrap(wrappedKey));
//        return ;
//    }


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
