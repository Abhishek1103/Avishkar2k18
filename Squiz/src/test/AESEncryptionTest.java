package test;

import data.Teacher;
import encryption.AES;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AESEncryptionTest {

    public static void main(String[] args) throws Exception{
        AES aes = new AES();
        aes.generateAESKey();
        SecretKey key = aes.getSecretKey();

        Teacher t = new Teacher("MP", 12);
        Teacher tt = new Teacher("NT", 13);
        HashMap<String, byte[]> map = new HashMap<>();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        aes.encryptWithAES(t, baos);
        map.put(t.getTeacherName(), baos.toByteArray());

        baos = new ByteArrayOutputStream();
        aes.encryptWithAES(tt, baos);
        map.put(tt.getTeacherName(), baos.toByteArray());


        System.out.println(map);

        for(Map.Entry<String, byte[]> e: map.entrySet()){
            System.out.println(e.getKey()+"="+e.getValue());
        }

        baos = new ByteArrayOutputStream();
        aes.encryptWithAES(map, baos);

        byte[] b = baos.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        HashMap<String, byte[]> map1 = (HashMap<String, byte[]>) aes.decryptWithAES(bais, key);

        for(Map.Entry<String, byte[]> e: map1.entrySet()){
            System.out.println(e.getKey()+"="+e.getValue());
            ByteArrayInputStream bais1 = new ByteArrayInputStream(e.getValue());
            Teacher te = (Teacher)(aes.decryptWithAES(bais1, key));
            System.out.println(te.getTeacherName()+": "+te);
        }

        System.out.println(map1);
    }

}
