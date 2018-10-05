package timerthreadtest;


import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

import java.util.Timer;
import java.util.TimerTask;

public class TimerClockService extends Service {

    Timer timer;
    int interval, flag;
    Label timerLabel;

    TimerClockService(Label label, int interval){
        this.timerLabel = label;
        this.interval = interval;
        flag = 1;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                int delay = 0;
                int period = 1000;
                timer = new Timer();
                //System.out.println(secs);
                timer.scheduleAtFixedRate(new TimerTask() {

                    public void run() {
                        System.out.println(updateInterval());
                        // TODO: Update timer label
                        Platform.runLater(() -> {
                            timerLabel.setText(interval+"");
                        });
                    }
                }, delay, period);
                return null;
            }
        };
    }

    public void setFlag(){
        flag = 0;
    }

    private int updateInterval() {
        if(flag == 0)
            timer.cancel();
        if (interval == 0) {
            timer.cancel();
        }
        return --interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getInterval() {
        return this.interval;
    }
}
