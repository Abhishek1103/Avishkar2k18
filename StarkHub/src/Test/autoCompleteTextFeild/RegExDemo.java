package Test.autoCompleteTextFeild;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExDemo {

    public static void main(String[] args) {
        Pattern p = Pattern.compile("[A-Za-z0-9]");
        String s = "bA";

        Matcher m = p.matcher(s);
        System.out.println(m.find());
    }
}
