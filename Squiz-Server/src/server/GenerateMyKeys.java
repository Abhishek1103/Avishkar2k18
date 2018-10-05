package server;

import encryption.AES;
import encryption.RSA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import static constants.Constants.USER_HOME;

public class GenerateMyKeys
{
    public static void main(String[] args) {
        AES aesObject = new AES();
        try {
            aesObject.generateAESKey();
            String encodedAESKeyBase64 = aesObject.getEncodedKey();
            File b64encodedAESKey = new File(System.getProperty("user.home")+"/SquizServer/myKeys/b64encodedAESKey.key");
            BufferedWriter bw = new BufferedWriter(new FileWriter(b64encodedAESKey));
            bw.write(encodedAESKeyBase64);
            bw.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RSA rsaObject = new RSA();
        try {
            rsaObject.generateKeyPair();
            PublicKey myRSAPublicKey = rsaObject.getPublicKey();
            PrivateKey myRSAPrivateKey = rsaObject.getPrivateKey();
            rsaObject.setPubliceKey(myRSAPublicKey);
            rsaObject.setPrivateKey(myRSAPrivateKey);
            rsaObject.savePublicKey(USER_HOME+"/SquizServer/myKeys/myRSAPublicKey.key");
            rsaObject.savePrivateKey(USER_HOME+"/SquizServer/myKeys/myRSAPrivateKey.key");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
}
