package Test;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        String plain = "My name is Abhishek";
        byte[] b = cipher.doFinal(plain.getBytes());

        Cipher deCipher = Cipher.getInstance("RSA");
        deCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] p = deCipher.doFinal(b);
        String s = new String(p);
        System.out.println("----"+s+"---------");
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
}
