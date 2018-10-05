package encryption;

import constants.Constants;
import constants.Flags;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSA {

    private final String ALGORITHM = "RSA";
    private final String ALGORITHM_PADDING = "RSA/ECB/PKCS1Padding";
    private final int KEY_SIZE = 2048;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        setPrivateKey(privateKey);
        setPubliceKey(publicKey);

        Flags.isKeyPairGenerated = true;

    }



    public PrivateKey getPrivateKey(){
        return this.privateKey;
    }

    public PublicKey getPublicKey(){
        return this.publicKey;
    }

    public void setPrivateKey(PrivateKey privateKey){
        this.privateKey = privateKey;
    }

    public void setPubliceKey(PublicKey publicKey){
        this.publicKey = publicKey;
    }


    public byte[] encryptSecretKey (SecretKey skey, PublicKey publicKey) {
        Cipher cipher = null;
        byte[] key = null;

        try {
            // initialize the cipher with the user's public key
            cipher = Cipher.getInstance(ALGORITHM_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey );
            key = cipher.doFinal(skey.getEncoded());
        }
        catch(Exception e ) {
            System.out.println ( "exception encoding key: " + e.getMessage() );
            e.printStackTrace();
        }
        return key;
    }

    public SecretKey decryptAESKey(byte[] data, PrivateKey privateKey ) {
        SecretKey key = null;
        PrivateKey privKey = null;
        Cipher cipher = null;

        try {
            // this is OUR private key
            privKey = privateKey;

            // initialize the cipher...
            cipher = Cipher.getInstance(ALGORITHM_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, privKey );

            // generate the aes key!
            key = new SecretKeySpec( cipher.doFinal(data), "AES" );
        }
        catch(Exception e) {
            System.out.println ( "exception decrypting the aes key: " + e.getMessage() );
            return null;
        }

        return key;
    }

    public void saveKeys(String fileName, BigInteger mod, BigInteger exp) throws IOException {
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

    public void savePublicKey(String filename) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        RSAPublicKeySpec rsaPublicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);

        try {
            saveKeys(filename, rsaPublicKeySpec.getModulus(), rsaPublicKeySpec.getPublicExponent());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void savePrivateKey(String filename) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        RSAPrivateKeySpec rsaPrivateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);

        try {
            saveKeys(filename, rsaPrivateKeySpec.getModulus(), rsaPrivateKeySpec.getPrivateExponent());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public PublicKey readPublicKeyFromFile(String fileName) throws IOException {

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        PublicKey publicKey = null;

        try{

            fis = new FileInputStream(new File(fileName));
            ois = new ObjectInputStream(fis);
            BigInteger modulus = (BigInteger) ois.readObject();
            BigInteger exponent = (BigInteger) ois.readObject();

            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory fact = KeyFactory.getInstance(ALGORITHM);
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

    public PrivateKey readPrivateKeyFromFile(String fileName) throws IOException {

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        PrivateKey privateKey = null;

        try{

            fis = new FileInputStream(new File(fileName));
            ois = new ObjectInputStream(fis);
            BigInteger modulus = (BigInteger) ois.readObject();
            BigInteger exponent = (BigInteger) ois.readObject();

            RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
            KeyFactory fact = KeyFactory.getInstance(ALGORITHM);
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
