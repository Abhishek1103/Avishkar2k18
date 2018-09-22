package encryption;

import constants.Constants;
import constants.Flags;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

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

    private void setPrivateKey(PrivateKey privateKey){
        this.privateKey = privateKey;
    }

    private void setPubliceKey(PublicKey publicKey){
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
}
