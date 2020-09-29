package finalnetwork;


import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class TrainNetwork {
    public static void main(String[] args) throws Exception {


        Timer time = new Timer(); // Instantiate Timer Object

        // Start running the task on Monday at 15:40:00, period is set to 8 hours
        // if you want to run the task immediately, set the 2nd parameter to 0
        time.schedule(new TrainTask(), 0, TimeUnit.DAYS.toMillis(7));



    }
}
