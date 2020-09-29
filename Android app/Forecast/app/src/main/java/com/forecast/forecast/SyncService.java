package com.forecast.forecast;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;

public class SyncService extends Service  {
    //Servicio que corre en segundo plano para guardar regularmente las alarmas en la bse de datos

    //El intervalo de tiempo (en este aso, 7(días) * 24(horas) * 60(minutos) * 60(segundos) * 1000(milisegundos))
    public static final long NOTIFY_INTERVAL =  7 * 24 * 60 * 60 * 1000; // 7 days

    //Se crea otro thread para que no interrumpa el resto del programa y no crashee
    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }

        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }


    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast
                    //Toast.makeText(getApplicationContext(), "Base de datos actualizada", Toast.LENGTH_SHORT).show();

                    //DataManager dataManager = new DataManager(getApplicationContext());

                    //dataManager.webServiceInsertAlarmsManager("http://192.168.1.15:80/forecast/insert_alarm.php");

                }

            });
        }


    }

}
