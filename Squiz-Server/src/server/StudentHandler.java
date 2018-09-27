package server;

import constants.Constants;
import data.Question;
import data.Student;
import data.User;
import encryption.AES;
import encryption.RSA;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static constants.Constants.USER_HOME;

public class StudentHandler implements Runnable
{
    private User user;

    public StudentHandler(User user)
    {
        this.user = user;
    }

    @Override
    public void run()
    {
        String flag="";
        try {
            flag = user.getDis().readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (flag)
        {
            case "#LOGIN":
            {
                new Thread(new SendLoginTimeMessageDaemon(user.getUserSocket().getInetAddress().getHostAddress(), user)).start();
                break;
            }
            case "#AUTH":
            {
                System.out.println("#AUTH");
                try {
                    String studentName = user.getDis().readUTF();
                    SQLConnection sqlConnection = new SQLConnection();
                    Statement stm = sqlConnection.connect();
                    String findQuery = "SELECT * FROM studentipmap WHERE studentname = '"+studentName+"';";
                    ResultSet rs = stm.executeQuery(findQuery);
                    System.out.println("rs found");
                    boolean res = !(rs.first());
                    System.out.println("calculated returning value "+res);
                    user.getDos().writeBoolean(res);
                    System.out.println("returned "+res);
                    if(res) {
                        File dirForThisStudent = new File(USER_HOME+"/SquizServer/Student/"+studentName+"/keys");
                        if(!dirForThisStudent.exists())
                            dirForThisStudent.mkdirs();
                        String query = "INSERT INTO studentipmap VALUES ('"+studentName+"', '"+user.getUserSocket().getInetAddress().getHostAddress()+"', '')";
                        stm.executeUpdate(query);
                        System.out.println("Update database with studentName and his IP");
                        try {
                            Student student = new Student(studentName);
                            ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                                    new File(USER_HOME+"/SquizServer/Student/"+studentName+"/dataobject")));
                            writeSerializedObject.writeObject(student);
                            writeSerializedObject.close();
                        }catch (Exception e){
                            System.out.println("Error in writing Serialized object");
                            e.printStackTrace();
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "#NEWSTUDENT":
            {
                /*
                * save students public key
                * encrypt your aes key with that
                * send it to student
                * */
                System.out.println("#NEWSTUDENT");
                try {
                    String studentName = user.getDis().readUTF();
                    SendFile sendPublicKey = new SendFile();
                    sendPublicKey.sendFile(USER_HOME+"/SquizServer/myKeys/myRSAPublicKey.key", user.getOos());
                    System.out.println("public key file sent");

                    //receiveing student public key
                    SaveFile saveStudentRSAPublicKey = new SaveFile();
                    saveStudentRSAPublicKey.saveFile(USER_HOME+"/SquizServer/Student/"+studentName+
                            "/keys/PublicKey.key", user.getOis());

                    byte RSAEncryptedStudentKiAESKey[] = (byte []) user.getOis().readObject();

                    RSA rsaObjectForAESDecryption = new RSA();
                    PrivateKey privateKey = rsaObjectForAESDecryption.readPrivateKeyFromFile(USER_HOME+
                            "/SquizServer/myKeys/myRSAPrivateKey.key");
                    SecretKey aesKeyOfStudent = rsaObjectForAESDecryption.decryptAESKey(RSAEncryptedStudentKiAESKey, privateKey);
                    AES aesObject = new AES();
                    aesObject.setSecretKey(aesKeyOfStudent);
                    String b64encodedAESKeyOfStudent = aesObject.getEncodedKey();

                    SQLConnection sqlConnection = new SQLConnection();
                    Statement stm = sqlConnection.connect();
                    String query = "UPDATE studentipmap set aesKey = '"+b64encodedAESKeyOfStudent+"' " +
                            "where studentname = '"+studentName+"';";
                    stm.executeUpdate(query);

                    //Ecrypt my aes secret key with student's rsa public key
                    AES aesb64Decrypter = new AES();
                    File myKey = new File(USER_HOME+"/SquizServer/myKeys/b64encodedAESKey.key");
                    BufferedReader br = new BufferedReader(new FileReader(myKey));
                    String b64encodedMyKey = "";
                    String s;
                    while((s=br.readLine())!=null)
                    {
                        b64encodedMyKey += s;
                    }
                    SecretKey myAESKey = aesb64Decrypter.decodeKey(b64encodedMyKey);
                    RSA rsaObjectForEncryptionOfMyAESKey = new RSA();
                    PublicKey studentPublicKey = rsaObjectForEncryptionOfMyAESKey.readPublicKeyFromFile(USER_HOME+"/SquizServer/Student/"+studentName+
                            "/keys/PublicKey.key");
                    byte encryptedmyAESKey[] = rsaObjectForEncryptionOfMyAESKey.encryptSecretKey(myAESKey, studentPublicKey);
                    user.getOos().writeObject(encryptedmyAESKey);
                    System.out.println("Sent my rsa encrypted aes key");

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "#FETCHTEACHERLIST":
            {
                /*
                 * then send aes encrypted student object to him back
                 * */
                System.out.println("#FETCHTEACHERLIST");
                File teacherDir = new File(USER_HOME+"/SquizServer/Teacher/");
                File allFile[] = teacherDir.listFiles();
                if(allFile == null)
                {
                    try {
                        user.getDos().writeBoolean(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("teacher list boolean send false");
                }
                else
                {
                    HashMap<String, byte[]> teacherMap = new HashMap<String, byte[]>();
                    try {
                        user.getDos().writeBoolean(true);
                        System.out.println("teacher list boolean send true");
                        for(File f:allFile)
                        {
                            System.out.println("Path of dataobject = "+f.getAbsolutePath()+"/dataobject");
                            ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(f.getAbsolutePath()+"/dataobject"));
                            byte[] data = (byte[]) readSerializedObject.readObject();
                            teacherMap.put(f.getName(), data);
                            readSerializedObject.close();
                        }
                        AES hashMapEncryptor = new AES();
                        File myKey = new File(USER_HOME+"/SquizServer/myKeys/b64encodedAESKey.key");
                        BufferedReader br = new BufferedReader(new FileReader(myKey));
                        String b64encodedMyKey = "";
                        String s;
                        while((s=br.readLine())!=null)
                        {
                            b64encodedMyKey += s;
                        }
                        SecretKey myAESKey = hashMapEncryptor.decodeKey(b64encodedMyKey);
                        hashMapEncryptor.setSecretKey(myAESKey);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        hashMapEncryptor.encryptWithAES(teacherMap, baos);
                        user.getOos().writeObject(baos.toByteArray());
                        System.out.println("teacher list sent");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }

            //synchronize write to the results file

            case "#SUBMITQUIZ":
            {
                /*
                 * If solution exist ie. /teacherName/QuizName+"solution" exist
                 * read and decryt it with myKey
                 * read and decrypt the answer hashmap coming from student
                 * formulate result in and store in /teacherName/QuizName+"results" serialized hashmap
                 * */
                try {
                    System.out.println("#SUBMITQUIZ");
                    String quizName = user.getDis().readUTF();
                    String teacherName = user.getDis().readUTF();
                    String studentName = user.getDis().readUTF();
                    int rating = user.getDis().readInt();
                    HashMap<Question, ArrayList<Integer> > solutionMap, answerMap;
                    HashMap<String, Integer> resultMap;
                    byte answersOfStudentByteArray[] = null;
                    File solutoinFile = new File(USER_HOME+"SquizServer/Teacher/"+teacherName+"/"+quizName+"solution");
                    if(solutoinFile.exists())
                    {
                        user.getDos().writeBoolean(true);
                        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(solutoinFile));
                        byte solutionByteArray[] = (byte[]) ois.readObject();
                        ByteArrayInputStream bais = new ByteArrayInputStream(solutionByteArray);
                        AES aesObj = new AES();
                        aesObj.setSecretKey(aesObj.decodeKey(Constants.b64encodedMyAESKey));
                        solutionMap = (HashMap<Question, ArrayList<Integer> >) aesObj.decryptWithAES(bais);
                        System.out.println("Read and decrypted solutionMap");

                        SQLConnection connection = new SQLConnection();
                        Statement stm = connection.connect();
                        String query = "SELECT aesKey FROM studentipmap where studentname = '"+studentName+"';";
                        ResultSet rs = stm.executeQuery(query);
                        rs.next();
                        String b64encodedAESKeyOfStudent = rs.getString(1);
                        System.out.println("SQL query for student key done");

                        answersOfStudentByteArray = (byte[]) user.getOis().readObject();
                        bais = new ByteArrayInputStream(answersOfStudentByteArray);
                        aesObj.setSecretKey(aesObj.decodeKey(b64encodedAESKeyOfStudent));
                        answerMap = (HashMap<Question, ArrayList<Integer> >) aesObj.decryptWithAES(bais);
                        System.out.println("Got and decrpted answerMap sent by student");

                        int marksScored = 0;
                        for(Map.Entry quesAns : answerMap.entrySet())
                        {
                            Question q = (Question) quesAns.getKey();
                            ArrayList<Integer> ansMarkedByStudent = (ArrayList<Integer>) quesAns.getValue();
                            if(ansMarkedByStudent == null || ansMarkedByStudent.isEmpty())
                                continue;
                            else
                            {
                                int f = 0;
                                ArrayList<Integer> actualAnswer = solutionMap.get(q);
                                for(Integer i:actualAnswer)
                                {
                                    if(!ansMarkedByStudent.contains(i)) {
                                        f = 1;
                                        break;
                                    }
                                    else
                                    {
                                        ansMarkedByStudent.remove(i);
                                    }
                                }
                                if(f == 1)
                                    continue;
                                else
                                {
                                    if(ansMarkedByStudent.isEmpty())
                                        marksScored += q.getMarks();
                                }
                            }
                        }

                        //check if file already is there is yes append otherwise create and add
                        synchronized (this) {
                            File resultFile = new File(USER_HOME + "/SquizServer/Teacher/" + teacherName + "/" + quizName + "results");
                            if (resultFile.exists())
                            {
                                ObjectInputStream resultMapOIS = new ObjectInputStream(new FileInputStream(resultFile));
                                resultMap = (HashMap<String, Integer>) resultMapOIS.readObject();

                                resultMap.put(studentName, marksScored);

                                System.out.println("Result added in old map");
                                ObjectOutputStream resultWriteBack = new ObjectOutputStream(new FileOutputStream(resultFile));
                                resultWriteBack.writeObject(resultMap);
                                resultWriteBack.close();
                                System.out.println("Written back result File");
                            }
                            else
                            {
                                resultMap = new HashMap<String, Integer>();
                                resultMap.put(studentName, marksScored);
                                ObjectOutputStream resultWriteBack = new ObjectOutputStream(new FileOutputStream(resultFile));
                                resultWriteBack.writeObject(resultMap);
                                resultWriteBack.close();
                                System.out.println("created new map and Written back result File");
                            }
                        }

                    }
                    else
                    {
                        user.getDos().writeBoolean(false);
                        File file = new File(USER_HOME+"/SquizServer/Student/"+studentName+"/"+quizName+"answers");
                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                        answersOfStudentByteArray = (byte[]) user.getOis().readObject();
                        System.out.println("Got and decrpted answerMap sent by student");
                        oos.writeObject(answersOfStudentByteArray);
                    }
                    System.out.println("setting rating");
                    String query = "UPDATE teacheripmap SET averagerating=(averagerating*noofrating+"+rating+")/(noofrating+1), "+
                            "noofrating=noofrating+1 WHERE teachername='"+teacherName+"';";
                    SQLConnection connection = new SQLConnection();
                    Statement stm = connection.connect();
                    stm.executeUpdate(query);
                    System.out.println("Rating updated");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "#GETRESULT"://only if solution is added
            {
                /*
                * return studentName entry after reading the file /teacherName/QuizName+"result" (if this file exists)
                * */
                System.out.println("#GETRESULT");
                try {
                    String studentName = user.getDis().readUTF();
                    String teacherName = user.getDis().readUTF();
                    String quizName = user.getDis().readUTF();
                    if(generateResultsIfAnyPending(studentName, teacherName, quizName) == 1)
                    {
                        System.out.println("Sending result");
                        synchronized (this) {
                            File resultFile = new File(USER_HOME + "/SquizServer/Teacher/" + teacherName + "/" + quizName + "results");
                            if (resultFile.exists()) {
                                user.getDos().writeBoolean(true);
                                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(resultFile));
                                HashMap<String, Integer> resultMap = (HashMap<String, Integer>) ois.readObject();
                                user.getDos().writeInt(resultMap.get(studentName));
                                System.out.println("Result sent");
                            } else
                                user.getDos().writeBoolean(false);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "#SAVESCORE":
            {
                System.out.println("#SAVESCORE");
                try {
                    String studentName = user.getDis().readUTF();
                    String teacherName = user.getDis().readUTF();
                    String quizName = user.getDis().readUTF();
                    int score = user.getDis().readInt();
                    System.out.println("Saving result");
                    synchronized (this) {
                        File resultFile = new File(USER_HOME + "/SquizServer/Teacher/" + teacherName + "/" + quizName + "results");
                        if (resultFile.exists()) {
                            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(resultFile));
                            HashMap<String, Integer> resultMap = (HashMap<String, Integer>) ois.readObject();
                            resultMap.put(studentName, score);
                            System.out.println("Old object modified and saved");
                        } else {
                            HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
                            resultMap.put(studentName, score);
                            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(resultFile));
                            oos.writeObject(resultMap);
                            oos.close();
                            System.out.println("New object created and written");
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "#GETRANK":
            {
                try {
                    String studentName = user.getDis().readUTF();
                    String teacherName = user.getDis().readUTF();
                    String quizName = user.getDis().readUTF();
                    synchronized (this) {
                        File resultFile = new File(USER_HOME + "/SquizServer/Teacher/" + teacherName + "/" + quizName + "results");
                        if (resultFile.exists()) {
                            user.getDos().writeBoolean(true);
                            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(resultFile));
                            HashMap<String, Integer> resultMap = (HashMap<String, Integer>) ois.readObject();
                            System.out.println("sending for sort");
                            HashMap<String, Integer> sortedMap = sortByValue(resultMap);
                            int c = 0;
                            for(Map.Entry a:sortedMap.entrySet())
                            {
                                c++;
                                if(a.getKey().equals(studentName))
                                {
                                    user.getDos().writeInt(c);
                                    break;
                                }
                            }
                        } else {
                            user.getDos().writeBoolean(false);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            default:
                System.out.println("Ye kya flag hai "+flag);
        }
    }

    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private int generateResultsIfAnyPending(String studentName, String teacherName, String quizName) throws Exception {
        System.out.println("in result generator");
        HashMap<Question, ArrayList<Integer> > solutionMap, answerMap;
        HashMap<String, Integer> resultMap;
        byte answersOfStudentByteArray[] = null;
        File solutoinFile = new File(USER_HOME+"SquizServer/Teacher/"+teacherName+"/"+quizName+"solution");
        if(solutoinFile.exists())
        {
            System.out.println("Sending true");
            user.getDos().writeBoolean(true);
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(solutoinFile));
            byte solutionByteArray[] = (byte[]) ois.readObject();
            ByteArrayInputStream bais = new ByteArrayInputStream(solutionByteArray);
            AES aesObj = new AES();
            aesObj.setSecretKey(aesObj.decodeKey(Constants.b64encodedMyAESKey));
            solutionMap = (HashMap<Question, ArrayList<Integer> >) aesObj.decryptWithAES(bais);
            System.out.println("Read and decrypted solutionMap");

            SQLConnection connection = new SQLConnection();
            Statement stm = connection.connect();
            String query = "SELECT aesKey FROM studentipmap where studentname = '"+studentName+"';";
            ResultSet rs = stm.executeQuery(query);
            rs.next();
            String b64encodedAESKeyOfStudent = rs.getString(1);
            System.out.println("SQL query for student key done");

            answersOfStudentByteArray = (byte[]) new ObjectInputStream(new FileInputStream(
                    new File(USER_HOME+"/SquizServer/Student/" +studentName+"/"+quizName+"answers"
                    ))).readObject();
            bais = new ByteArrayInputStream(answersOfStudentByteArray);
            aesObj.setSecretKey(aesObj.decodeKey(b64encodedAESKeyOfStudent));
            answerMap = (HashMap<Question, ArrayList<Integer> >) aesObj.decryptWithAES(bais);
            System.out.println("Got and decrpted answerMap sent by student");

            int marksScored = 0;
            for(Map.Entry quesAns : answerMap.entrySet())
            {
                Question q = (Question) quesAns.getKey();
                ArrayList<Integer> ansMarkedByStudent = (ArrayList<Integer>) quesAns.getValue();
                if(ansMarkedByStudent == null || ansMarkedByStudent.isEmpty())
                    continue;
                else
                {
                    int f = 0;
                    ArrayList<Integer> actualAnswer = solutionMap.get(q);
                    for(Integer i:actualAnswer)
                    {
                        if(!ansMarkedByStudent.contains(i)) {
                            f = 1;
                            break;
                        }
                        else
                        {
                            ansMarkedByStudent.remove(i);
                        }
                    }
                    if(f == 1)
                        continue;
                    else
                    {
                        if(ansMarkedByStudent.isEmpty())
                            marksScored += q.getMarks();
                    }
                }
            }

            //check if file already is there is yes append otherwise create and add
            synchronized (this) {
                File resultFile = new File(USER_HOME + "/SquizServer/Teacher/" + teacherName + "/" + quizName + "results");
                if (resultFile.exists())
                {
                    ObjectInputStream resultMapOIS = new ObjectInputStream(new FileInputStream(resultFile));
                    resultMap = (HashMap<String, Integer>) resultMapOIS.readObject();

                    resultMap.put(studentName, marksScored);

                    System.out.println("Result added in old map");
                    ObjectOutputStream resultWriteBack = new ObjectOutputStream(new FileOutputStream(resultFile));
                    resultWriteBack.writeObject(resultMap);
                    resultWriteBack.close();
                    System.out.println("Written back result File");
                }
                else
                {
                    resultMap = new HashMap<String, Integer>();
                    resultMap.put(studentName, marksScored);
                    ObjectOutputStream resultWriteBack = new ObjectOutputStream(new FileOutputStream(resultFile));
                    resultWriteBack.writeObject(resultMap);
                    resultWriteBack.close();
                    System.out.println("created new map and Written back result File");
                }
            }
            System.out.println("Out of generator with 1");
            return 1;
        }
        else
        {
            System.out.println("Sending false");
            user.getDos().writeBoolean(false);
            System.out.println("Out of generator with 0");
            return 0;
        }
    }
}
