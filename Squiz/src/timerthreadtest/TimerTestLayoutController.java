package timerthreadtest;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TimerTestLayoutController
{
    @FXML
    Label timerLabel;
    @FXML
    Button startButton, stopButton;

    int f = 0, intervalLeft;


    TimerClockService ser[] = new TimerClockService[3];

    public void startButtonClicked()
    {
        if(f == 0) {
            ser[0] = new TimerClockService(timerLabel, 10);
            f = 1;
            ser[0].start();
        }
        else if(ser[0].getInterval() != -1)
        {
            System.out.println("Again starting where left");
            ser[0] = new TimerClockService(timerLabel, intervalLeft+1);
            ser[0].start();
        }
        else {
            // permanently disable that button
            System.out.println("In else");
        }
    }

    // Now different section buttons can have the same code of start
    // and stop button with making the lastest active button as disabled
    // so that no "hutiyapa" happen as prakhar says...
    // if all buttons are permanently disabled then end the test
    // u can do this by iterating ober the vbox every time a button is
    // disabled.

    public void stopButtonClicked() {
        intervalLeft = ser[0].getInterval();
        ser[0].setFlag();
        System.out.println(intervalLeft + " time left in ser[0]");
    }
}
