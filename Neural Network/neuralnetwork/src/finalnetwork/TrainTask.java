package finalnetwork;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TrainTask extends TimerTask {

    public TrainTask(){

        //Constructor

    }

    public void run() {
        try {

            String inputLineCheck = null;

            URL webServiceCheck = new URL("http://192.168.1.13/forecast/get_training_diff.php");
            URLConnection webServiceConnectionCheck = webServiceCheck.openConnection();
            BufferedReader readerCheck = new BufferedReader(
                    new InputStreamReader(
                            webServiceConnectionCheck.getInputStream()));

            inputLineCheck = readerCheck.readLine();
            readerCheck.close();

            System.out.println(Integer.valueOf(inputLineCheck));
            if (Integer.valueOf(inputLineCheck) > 0) {

                int amountOfInputs = 3;
                Network network = new Network(amountOfInputs, 3, 2, 1);


                String inputLine = null;

                URL webService = new URL("http://192.168.1.13/forecast/get_alarm_data.php");
                URLConnection webServiceConnection = webService.openConnection();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                webServiceConnection.getInputStream()));

                inputLine = reader.readLine();
                reader.close();

                int inputNumbers = 0;

                char[] inputLineChars = inputLine.toCharArray();

                for (int i = 0; i < inputLineChars.length; i++) {
                    if (inputLineChars[i] == '+') {
                        inputNumbers++;
                    }
                }
                double[][] inputs = new double[inputNumbers][3];
                double[][] target = new double[inputNumbers][1];
                int row = 0;
                int column = 0;
                String temp = "";

                for (int i = 0; i < inputLineChars.length; i++) {

                    temp += String.valueOf(inputLineChars[i]);
                    if (inputLineChars[i] == '+') {
                        temp = temp.substring(0, temp.length() - 1);
                        target[row][0] = Double.valueOf(temp);
                        row++;
                        column = 0;
                        temp = "";
                    }
                    if (inputLineChars[i] == ',') {
                        temp = temp.substring(0, temp.length() - 1);
                        inputs[row][column] = Double.valueOf(temp);
                        column++;
                        temp = "";
                    }
                    if (inputLineChars[i] == '*') {
                        break;
                    }
                }


                for (int i = 0; i < inputNumbers; i++) {
                    inputs[i] = inputs[i];

                }


                for (int i = 0; i < 1000000; i++) {
                    for (int j = 0; j < inputNumbers; j++) {
                        network.train(inputs[j], target[j], 0.05);
                    }
                }
                network.saveNetwork("res/network.txt");
                System.out.println("Red guardada correctamente");


                String inputLineUpdate = null;

                URL webServiceUpdate = new URL("http://192.168.1.13/forecast/update_last_training.php?newAlarms=" + Integer.valueOf(inputLineCheck));
                URLConnection webServiceConnectionUpdate = webServiceUpdate.openConnection();
                BufferedReader readerUpdate = new BufferedReader(
                        new InputStreamReader(
                                webServiceConnectionUpdate.getInputStream()));

                inputLineUpdate = readerUpdate.readLine();
                readerUpdate.close();









            /*for (int j = 0; j<inputNumbers; j++){
                double[] output = network.feedForward(inputs[j]);
                output[0] = output[0] * 100;
                String outputString = String.valueOf(output[0]);
                System.out.println(outputString.substring(0,2) + "%");
            }*/

            }


        } catch (Exception ex) {
            System.out.println("error running thread " + ex.getMessage());
        }
    }
}
