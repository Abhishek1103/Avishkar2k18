package server;

import constants.Constants;
import data.*;
import encryption.AES;
import encryption.RSA;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static constants.Constants.USER_HOME;

public class TeacherHandler implements Runnable
{

    User user;

    public TeacherHandler(User user)
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
                    String teacherName = user.getDis().readUTF();
                    SQLConnection sqlConnection = new SQLConnection();
                    Statement stm = sqlConnection.connect();
                    String findQuery = "SELECT * FROM teacheripmap WHERE teachername = '"+teacherName+"';";
                    ResultSet rs = stm.executeQuery(findQuery);
                    System.out.println("rs found");
                    boolean res = !(rs.first());
                    System.out.println("calculated returning value "+res);
                    user.getDos().writeBoolean(res);
                    System.out.println("returned "+res);
                    if(res) {
                        File dirForThisTeacher = new File(USER_HOME+"/SquizServer/Teacher/"+teacherName+"/keys");
                        if(!dirForThisTeacher.exists())
                            dirForThisTeacher.mkdirs();
                        String query = "INSERT INTO teacheripmap VALUES ('"+teacherName+"', '"+user.getUserSocket().getInetAddress().getHostAddress()+"', '', 0, 0)";
                        stm.executeUpdate(query);
                        System.out.println("Update database with teaherName and his IP");
                        try {
                            Teacher teacher = new Teacher(teacherName,0);
                            AES aesEncrypter = new AES();
                            File mykey = new File(USER_HOME+"/SquizServer/myKeys/b64encodedAESKey.key");
                            BufferedReader br = new BufferedReader(new FileReader(mykey));
                            String b64encodedMyKey = "";
                            String s;
                            while((s=br.readLine())!=null)
                            {
                                b64encodedMyKey += s;
                            }
                            SecretKey myAESKey = aesEncrypter.decodeKey(b64encodedMyKey);
                            aesEncrypter.setSecretKey(myAESKey);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            aesEncrypter.encryptWithAES(teacher, baos);
                            ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                                    new File(USER_HOME+"/SquizServer/Teacher/"+teacherName+"/dataobject")));
                            writeSerializedObject.writeObject(baos.toByteArray());
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
            case "#NEWTEACHER":
            {
                System.out.println("#NEWTEACHER");
                /*
                 * accept username of teacher
                 * send server public key to user
                 * receive rsa (from your public key) encrypted aes key of the teacher
                 * decrypt this aes teacher key and store it on your system in encoded in base64
                 * */
                try {
                    String teacherName = user.getDis().readUTF();
                    SendFile sendPublicKey = new SendFile();
                    sendPublicKey.sendFile(USER_HOME+"/SquizServer/myKeys/myRSAPublicKey.key", user.getOos());
                    System.out.println("public key file sent");

                    SaveFile saveTeacherRSAPublicKey = new SaveFile();
                    saveTeacherRSAPublicKey.saveFile(USER_HOME+"/SquizServer/Teacher/"+teacherName+
                            "/keys/PublicKey.key", user.getOis());

                    byte RSAEncryptedTeacherKiAESKey[] = (byte []) user.getOis().readObject();
                    RSA rsaObjectForAESDecryption = new RSA();
                    PrivateKey privateKey = rsaObjectForAESDecryption.readPrivateKeyFromFile(USER_HOME+
                            "/SquizServer/myKeys/myRSAPrivateKey.key");
                    SecretKey aesKeyOfTeacher = rsaObjectForAESDecryption.decryptAESKey(RSAEncryptedTeacherKiAESKey, privateKey);
                    AES aesObject = new AES();
                    aesObject.setSecretKey(aesKeyOfTeacher);
                    String b64encodedAESKeyOfTeacher = aesObject.getEncodedKey();
                    SQLConnection sqlConnection = new SQLConnection();
                    Statement stm = sqlConnection.connect();
                    String query = "UPDATE teacheripmap set aesKey = '"+b64encodedAESKeyOfTeacher+"' " +
                            "where teachername = '"+teacherName+"';";
                    stm.executeUpdate(query);
                    System.out.println("Teacher key saved");

                    AES aesb64Decrypter = new AES();
                    SecretKey myAESKey = aesb64Decrypter.decodeKey(Constants.b64encodedMyAESKey);
                    RSA rsaObjectForEncryptionOfMyAESKey = new RSA();
                    PublicKey studentPublicKey = rsaObjectForEncryptionOfMyAESKey.readPublicKeyFromFile(USER_HOME+"/SquizServer/Teacher/"+teacherName+
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
            case "#ADDSUBJECT":
            {
                System.out.println("#ADDSUBJECT");
                try {
                    String teacherName = user.getDis().readUTF();
                    SQLConnection connection = new SQLConnection();
                    Statement stm = connection.connect();
                    String query = "SELECT aesKey FROM teacheripmap where teachername = '"+teacherName+"';";
                    ResultSet rs = stm.executeQuery(query);
                    rs.next();
                    String b64encodedAESKeyOfTeacher = rs.getString(1);
                    System.out.println("SQL query for teacher key done");

                    AES aesDecrypter = new AES();
                    SecretKey AESKey = aesDecrypter.decodeKey(b64encodedAESKeyOfTeacher);
                    byte encryptedSubjectHashMap[] = (byte []) user.getOis().readObject();
                    ByteArrayInputStream bais = new ByteArrayInputStream(encryptedSubjectHashMap);
                    aesDecrypter.setSecretKey(AESKey);
                    HashMap<String, Subject> newSubject = (HashMap<String, Subject>) aesDecrypter.decryptWithAES(bais);
                    System.out.println("Encrypted hashmap decrypted");

                    File file = new File(USER_HOME+ "/SquizServer/Teacher/"+teacherName+"/dataobject");
                    ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));

                    byte objTeacher[] = (byte[]) readSerializedObject.readObject();
                    bais = new ByteArrayInputStream(objTeacher);
                    aesDecrypter = new AES();
                    SecretKey myAESKey = aesDecrypter.decodeKey(Constants.b64encodedMyAESKey);
                    aesDecrypter.setSecretKey(myAESKey);
                    Teacher obj = (Teacher) aesDecrypter.decryptWithAES(bais);
                    System.out.println("obj for " + teacherName);
                    obj.setSubjectHashMap(newSubject);
                    System.out.println("Getting my aes key");

                    AES aesEncrypter = new AES();
                    myAESKey = aesEncrypter.decodeKey(Constants.b64encodedMyAESKey);
                    aesEncrypter.setSecretKey(myAESKey);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    aesEncrypter.encryptWithAES(obj, baos);

                    ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
                            new File(USER_HOME+"/SquizServer/Teacher/"+teacherName+"/dataobject")));
                    writeSerializedObject.writeObject(baos.toByteArray());
                    writeSerializedObject.close();
                    System.out.println("encrypted and stored in file");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }
//            case "#ADDQUIZ":
//            {
//                try {
//                    String teacherName = user.getDis().readUTF();
//                    String subjectName = user.getDis().readUTF();
//                    Quiz newQuiz = (Quiz) user.getOis().readObject();
//                    File file = new File(USER_HOME+ "/SquizServer/Teacher/"+teacherName+"/dataobject");
//                    ObjectInputStream readSerializedObject = new ObjectInputStream(new FileInputStream(file));
//                    Teacher obj = (Teacher) readSerializedObject.readObject();
//                    System.out.println("obj for " + teacherName);
//
//                    obj.getSubjectHashMap().get(newQuiz.getSubjectName()).setQuizHashMap(newQuiz.getQuizName(), newQuiz);
//
//                    ObjectOutputStream writeSerializedObject = new ObjectOutputStream(new FileOutputStream(
//                            new File(USER_HOME+"/Teacher/"+teacherName+"/dataobject")));
//                    writeSerializedObject.writeObject(obj);
//                    writeSerializedObject.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
            case "#ADDSOLUTIONTOQUIZ":
            {
                /*
                * take encrypted hasmap
                * decrypt it with teacher aes key
                * encrypt it with myAESKey and store it in /tacherName/QuizName
                * */
                try {
                    String teacherName = user.getDis().readUTF();
                    String quizName = user.getDis().readUTF();
                    byte encryptedResultHashMap[] = (byte[]) user.getOis().readObject();
                    SQLConnection connection = new SQLConnection();
                    Statement stm = connection.connect();
                    String query = "SELECT aesKey FROM teacheripmap where teachername = '"+teacherName+"';";
                    ResultSet rs = stm.executeQuery(query);
                    rs.next();
                    String b64encodedAESKeyOfTeacher = rs.getString(1);
                    System.out.println("SQL query for teacher key done");

                    AES aesObj = new AES();
                    SecretKey teacherAESKey = aesObj.decodeKey(b64encodedAESKeyOfTeacher);
                    aesObj.setSecretKey(teacherAESKey);
                    ByteArrayInputStream bais = new ByteArrayInputStream(encryptedResultHashMap);
                    HashMap<Question, ArrayList<Integer> > answerMap = (HashMap<Question, ArrayList<Integer> >) aesObj.decryptWithAES(bais);
                    System.out.println("Encrypted hashmap decrypted");


                    aesObj.setSecretKey(aesObj.decodeKey(Constants.b64encodedMyAESKey));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    aesObj.encryptWithAES(answerMap, baos);
                    System.out.println("answerMap encrypted with myKey");

                    File file = new File(USER_HOME+"SquizServer/Teacher/"+teacherName+"/"+quizName+"solution");
                    ObjectOutputStream writeObject = new ObjectOutputStream(new FileOutputStream(file));
                    writeObject.writeObject(baos.toByteArray());
                    writeObject.close();
                    System.out.println("Done writing solution");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "#FETCHRESULTS":
            {
                System.out.println("#FETCHRESULTS");
                // return the whole object /teacherName/QuizName+"results" (if this file exists)
                try {
                    String teacherName = user.getDis().readUTF();
                    String quizName = user.getDis().readUTF();
                    System.out.println("Sending result");
                    synchronized (this) {
                        File resultFile = new File(USER_HOME + "/SquizServer/Teacher/" + teacherName + "/" + quizName + "results");
                        if (resultFile.exists())
                        {
                            user.getDos().writeBoolean(true);
                            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(resultFile));
                            HashMap<String, Integer> resultMap = (HashMap<String, Integer>) ois.readObject();
                            user.getOos().writeObject(resultMap);
                            System.out.println("Result sent");
                        }
                        else {
                            // assumption all responses are stored
                            user.getDos().writeBoolean(false);
                            SQLConnection connection = new SQLConnection();
                            Statement stm = connection.connect();
                            String query = "select studentname from studentipmap;";
                            ResultSet rs = stm.executeQuery(query);
                            ArrayList<String> names = new ArrayList<String>();
                            while(rs.next())
                                names.add(rs.getString(1));

                            for(int i=0;i<names.size();i++)
                            {
                                generateResultsIfAnyPending(names.get(i), teacherName, quizName);
                            }
                            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(resultFile));
                            HashMap<String, Integer> resultMap = (HashMap<String, Integer>) ois.readObject();
                            user.getOos().writeObject(resultMap);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //System.out.println("#GETRATING");
                try {
                    String teacherName = user.getDis().readUTF();
                    String query = "SELECT averagerating FROM teacheripmap WHERE teachername='"+teacherName+"';";
                    SQLConnection connection = new SQLConnection();
                    Statement stm = connection.connect();
                    ResultSet rs = stm.executeQuery(query);
                    rs.next();
                    double rating = rs.getDouble(1);
                    user.getDos().writeDouble(rating);
                    System.out.println("Rating send");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                break;
            }
//            case "#GETRATING":
//            {
//                System.out.println("#GETRATING");
//                try {
//                    String teacherName = user.getDis().readUTF();
//                    String query = "SELECT averagerating FROM teacheripmap WHERE teachername='"+teacherName+"';";
//                    SQLConnection connection = new SQLConnection();
//                    Statement stm = connection.connect();
//                    ResultSet rs = stm.executeQuery(query);
//                    rs.next();

//                    double rating = rs.getDouble(1);
//                    user.getDos().writeDouble(rating);
//                    System.out.println("Rating send");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//
//                break;
//            }
            default:
                System.out.println("Ye kya flag hai "+flag);
        }
    }

    private int generateResultsIfAnyPending(String studentName, String teacherName, String quizName) throws Exception {
        System.out.println("in result generator");
        HashMap<Question, ArrayList<Integer> > solutionMap, answerMap;
        HashMap<String, Integer> resultMap;
        byte answersOfStudentByteArray[] = null;
        File solutoinFile = new File(USER_HOME+"SquizServer/Teacher/"+teacherName+"/"+quizName+"solution");
        if(solutoinFile.exists())
        {
//            System.out.println("Sending true");
//            user.getDos().writeBoolean(true);
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
//            System.out.println("Sending false");
//            user.getDos().writeBoolean(false);
            System.out.println("Out of generator with 0");
            return 0;
        }
    }
}
