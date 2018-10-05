package test;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class FinalEncryptionClass extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                return null;
            }
        };
    }
}
