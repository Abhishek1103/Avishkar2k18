package utility;

import constants.Constants;
import constants.Flags;
import data.Subject;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class InitializerService extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try{

                    // TODO: Initialises maps and arrays
                    String type="";
                    if(Flags.isTeacher)
                        type = "teacher";
                    else if(Flags.isStudent)
                        type = "student";

                    // Reading SubjectMap
                    String subjectMapPath = Constants.USER_HOME + Constants.SQUIZ_DIR + type + "/" + Constants.USERNAME + "/"+"subjects/list";
                    if((new File(subjectMapPath)).exists()) {
                        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File(subjectMapPath)));
                        byte[] byteArray = (byte[]) (objectInputStream.readObject());
                        ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
                        Constants.SUBJECT_MAP = (HashMap<String, Subject>) (Constants.aes.decryptWithAES(bais));
                    }else{
                        Constants.SUBJECT_MAP = new HashMap<>();
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }


                return null;
            }
        };
    }
}
