package utility;


import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import ui.MainQuizFrameController;

import java.util.Timer;
import java.util.TimerTask;

public class TimerClockService extends Service {

    Timer timer;
    int interval;
    Label timerLabel;
    String section;

    public TimerClockService(Label label, int interval, String section){
        this.timerLabel = label;
        this.interval = interval;
        this.section = section;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                int delay = 1000;
                int period = 1000;
                timer = new Timer();
                //System.out.println(secs);
                timer.scheduleAtFixedRate(new TimerTask() {

                    public void run() {
                        System.out.println(updateInterval());
                        // TODO: Update timer label
                        if(interval <= 0){
                            this.cancel();

                        }else {
                            if(section.equals(MainQuizFrameController.currentSection)) {
                                Platform.runLater(() -> {
                                    timerLabel.setText(interval + "");
                                });
                            }
                        }
                    }
                }, delay, period);
                return null;
            }
        };
    }

    private int updateInterval() {
        if (interval == 1) {
            timer.cancel();
            timer.purge();
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
