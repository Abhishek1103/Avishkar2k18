package utility;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.net.ServerSocket;
import java.net.Socket;

public class RealTimeMessagesTeacherService extends Service {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {


                try{

                    ServerSocket serverSocket = new ServerSocket(13002);

                    while (true) {
                        try {
                            Socket sock = serverSocket.accept();
                            RealTimeMessagesTeacherHandler handler = new RealTimeMessagesTeacherHandler(sock);
                            handler.start();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }



                return null;
            }
        };
    }
}
