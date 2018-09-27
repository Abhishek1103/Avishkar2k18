package sample;

public class SubstringTest
{
    public static void main(String[] args) {
        String s  = "Surbhit:Awasthi:hulahula";
        System.out.println(s.substring(s.indexOf(":")+1, s.lastIndexOf(":")));
    }
}
