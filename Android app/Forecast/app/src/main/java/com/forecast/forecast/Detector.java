package com.forecast.forecast;

import android.util.Log;

//CLASE DETECTOR. SE ENCARGA DE CALCULAR SI ESTÁ DORMIDO
class Detector{
    private int faceHeight;
    private boolean eyesDetected;
    private int border;
    private int faceOff;
    private int eyesOff;
    private boolean alarm;
    private boolean calibrationError;

    private static String TAG = "Detector"; //Variable que se va a utilizar para depurar. Va a contener mensajes de error o éxito


    public Detector(int faceHeight, boolean eyesDetected){
        calibrationError = false;
        this.faceHeight = faceHeight;
        this.eyesDetected = eyesDetected;


        if (faceHeight > 1 && eyesDetected){
            //Si los datos son correctos, se calibra calculando que el borde inferior de la cara no baje más de un límite
            border = (int) (faceHeight + (faceHeight * 0.2));

            Log.d(TAG, "CALIBRADO" + border); //Debug. Se calibra

        }else{
            calibrationError = true;
        }

    }

    public boolean getAlarm(){
        return alarm;
    }

    public boolean getCalibrationError(){
        return calibrationError;
    }

    public void setIndicators(int faceHeight, boolean eyesDetected) {
        this.faceHeight = faceHeight;
        this.eyesDetected = eyesDetected;

        Log.d(TAG, "faceHeight" + faceHeight); //Debug. altura cara
        Log.d(TAG, "eyesDetected" + eyesDetected); //Debug. ojos?


    }

    public void calculateSleepiness(){
        if (faceHeight > border || faceHeight == 0 || !eyesDetected){
            if (faceHeight > border || faceHeight == 0) faceOff++;  //Si la cara está muy abajo o no es detectada, error de cara
            if (!eyesDetected) eyesOff++; //Si no se detecetan los ojos, error de ojos

            Log.d(TAG, "OJOS" + eyesOff); // Debug. ojos malos + cant
            Log.d(TAG, "CARA" + faceOff); // Debug. caras malas + cant


            if (faceOff > 5 || eyesOff > 5){
                alarm();
            }

        }else{
            clearIndicators();  //Si todos los indicadores están siendo detectados y están en un lugar correcto, se vuelven a 0
        }
    }

    private void clearIndicators() {
        faceOff = 0;
        eyesOff = 0;
        alarm = false;
    }

    private void alarm(){

        Log.d(TAG, "ALARMA"); // Debug. Alarma

        alarm = true;
    }

}
