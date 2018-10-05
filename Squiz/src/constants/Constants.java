package constants;

import data.Subject;
import encryption.AES;
import encryption.RSA;

import java.util.HashMap;

public class Constants {

    public static final String SERVER_IP = "172.31.84.87";
    public static final int SERVER_PORT = 7001;
    public static final int CHAT_PORT = 13001;

    public static String USER_HOME = "";
    public static String USERNAME = "";

    public static String USER_DIR = "";
    public final  static String SQUIZ_DIR = "/squiz/";

    public static HashMap<String, Subject> SUBJECT_MAP;
    public static HashMap<String, byte[]> TEACHER_MAP;

    public static RSA rsa = null;
    public static AES aes = null;

    public static String PUBLIC_KEY_PATH = "";
    public static String PRIVATE_KEY_PATH = "";
    public static String SECRET_KEY_PATH = "";
    public static String SERVER_PUBIC_KEY_PATH = "";
    public static String SERVER_SECRET_KEY_PATH = "";

    public static final String SINGLE = "single";
    public static final String MULTIPLE = "multiple";
    public static final String TF = "tf";
}
