package Client.Utility;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class InitializeListsAndMapsService extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                try{

                    // TODO: Add functionality
                    System.out.println("\n\n Add Functionality to me \n\n");

                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
