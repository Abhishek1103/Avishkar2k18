package utility;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ReadSubjectMapService extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                // TODO: Read subjectMap

                return null;
            }
        };
    }
}
