package finalnetwork;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class ExecuteNetwork {
    public static void main(String[] args) throws Exception {

        int amountOfInputs = 3;
        Network network = new Network(amountOfInputs,3,2,1);



        String inputLine = null;

        URL webService = new URL("http://192.168.1.13/forecast/get_alarm_data.php");
        URLConnection webServiceConnection = webService.openConnection();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        webServiceConnection.getInputStream()));

        inputLine = reader.readLine();
        reader.close();
        

        double[] evaluationArray = new double[]{Integer.valueOf(args[0]),Integer.valueOf(args[1]),Integer.valueOf(args[2])};

        Network network1 = Network.loadNetwork("res/network.txt");
        double[] output = network1.feedForward(evaluationArray);
        output[0] = output[0] * 20;
        String outputString = String.valueOf(output[0]);
        System.out.println(outputString.substring(0,2) + "%");

    }
}
