package encryption;

import constants.Constants;
import constants.Flags;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AES {

    private final String ALGORITHM = "AES";
    private final String ALGORITHM_PADDING = "AES/ECB/PKCS5Padding";
    private final int KEY_SIZE = 256;

    private SecretKey secretKey;

    public void generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom();
        keyGenerator.init(KEY_SIZE, secureRandom);

        SecretKey secretKey = keyGenerator.generateKey();
        setSecretKey(secretKey);

        Flags.isSecretKeyGenerated = true;
    }

    public void setSecretKey(SecretKey secretKey){
        this.secretKey = secretKey;
    }

    public SecretKey getSecretKey(){
        return this.secretKey;
    }

    // Encrypt with AES
    public void encryptWithAES(Serializable object, OutputStream _outputStream) throws InvalidKeyException, IOException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {

        Cipher cipher = Cipher.getInstance(ALGORITHM_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        SealedObject sealedObject = new SealedObject(object, cipher);

        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(_outputStream, cipher);
        ObjectOutputStream outputStream = new ObjectOutputStream(cos);
        outputStream.writeObject(sealedObject);
        outputStream.close();
    }

    // Decrypt with AES
    public Object decryptWithAES(InputStream _inputStream, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        CipherInputStream cipherInputStream = new CipherInputStream(_inputStream, cipher);
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

    // Encode Secret key to Base64 string
    public String getEncodedKey(SecretKey key) throws Exception{
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // Decode base64 String to Secret Key
    public SecretKey decodeKey(String encodedKey) throws Exception{
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
        return originalKey;
    }
}
