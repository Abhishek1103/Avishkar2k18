package Client.Utility;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class CommentPostService extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                try{
                    // TODO: Program Logic

                    
                }catch(Exception e){
                    e.printStackTrace();
                }


                return null;
            }
        };
    }
}
