package test;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class TimerClass implements Runnable{
    int interval;
    Timer timer;

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Input seconds => : ");
        String secs = sc.nextLine();
        int delay = 1000;
        int period = 1000;
        timer = new Timer();
        interval = Integer.parseInt(secs);
        System.out.println(secs);
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                System.out.println(updateInterval());

            }
        }, delay, period);

    }

    private int updateInterval()  {
        if (interval == 1) {
            timer.cancel();
        }

        return --interval;
    }

    public void setInterval(int interval){
        this.interval = interval;
    }

    public int getInterval(){
        return this.interval;
    }
}