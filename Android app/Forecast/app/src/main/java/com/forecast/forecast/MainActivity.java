//Test usando OpenCV para abrir la cámara.

package com.forecast.forecast;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import java.io.IOException;
import java.util.Calendar;


//CLASE PRINCIPAL DE LA ACTIVIDAD. CONTROLA EL RESTO. CLIENTE
public class MainActivity extends Activity { //Implements es lo que se usa para "heredar" y utilizar una interfaz


    JavaCameraView cameraView;  //Se declara el objeto cameraView el cual se va a bindear con la vista de la UI
    Button calibrate, alterData, confirmData, debug;  //Se declara el objeto calibrate el cual se va a bindear con la vista de la UI homónima
    Camera cameraClass;
    Detector detectorClass;
    boolean flagCalibrated = false;

    long id = -1;
    String firstName = "";
    String lastName = "";
    int gender = 0;
    String dateOfBirth = "";
    int amountOfAlarms1 = 0;
    int amountOfAlarms2 = 0;
    int amountOfAlarms3 = 0;

    DataManager dataManager;


    //Al crear la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {    //onCreate de la actividad en la que se va a trabajar
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main); //Se setea activity_main como la visible

        startService(new Intent(this, SyncService.class)); //Inicia el servicio en segundo plano para sincronizar los datos

        //Toast.makeText(MainActivity.this, "Para comenzar debe ponerse en posición de manejo y tocar el botón 'CALIBRAR'", Toast.LENGTH_LONG).show();

        cameraView = findViewById(R.id.cameraView);  //Se bindea el objeto cameraView con la vista ed la UI
        cameraView.setVisibility(SurfaceView.VISIBLE);  //Se setea cameraView como visible (puede hacerse también desde el X;L y quizás sea mejor idea)

        confirmData = null;

        debug = findViewById(R.id.debug);
        alterData = findViewById(R.id.alterData);
        calibrate = findViewById(R.id.changeSize);





        debug.setOnClickListener(new View.OnClickListener() {        //Se le asigna un listener al botón
            @Override
            public void onClick(View view) {
                debugButton();  //Al hacer click en el botón, se dispara la función calibrateHeight();
            }
        });

        calibrate.setOnClickListener(new View.OnClickListener() {        //Se le asigna un listener al botón
            @Override
            public void onClick(View view) {
                calibrateHeight();  //Al hacer click en el botón, se dispara la función calibrateHeight();
            }
        });

        alterData.setOnClickListener(new View.OnClickListener() {        //Se le asigna un listener al botón
            @Override
            public void onClick(View view) {

                if (lastName.equals("")) {
                    showPopup(true);
                } else {
                    showPopup(false);
                }


            }
        });


        cameraClass = new Camera(this, cameraView);
        dataManager = new DataManager(this);


        //Si no se asignaron los permisos
        //(Debe hacerse en una clase que extienda Activity. Por eso no se pudo poner en la clase camera)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //Pedir permisos
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 200);
        }


        //LOOP PRINCIPAL
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            boolean alarmFlag = false;

            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM,100);


            public void run() {

                if (flagCalibrated) {
                    detectorClass.setIndicators(cameraClass.getFaceHeight(), cameraClass.getEyesDetected()); //Se le asignan al obj de Detector lo que devuelve el de Camera
                    detectorClass.calculateSleepiness();    //Según lo seteado en la linea anterior, detecta si está dormido
                    if (detectorClass.getAlarm()) {  //Si la alarma es true

                        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);  //Suena un sonido

                        if (!alarmFlag){
                            alarmFlag = true;
                            int timeSlot = dataManager.getTimeSlot();
                            switch (timeSlot){
                                case 1:
                                    amountOfAlarms1++;
                                    break;
                                case 2:
                                    amountOfAlarms2++;
                                    break;
                                case 3:
                                    amountOfAlarms3++;
                                    break;
                            }
                        }



                    } else {
                        alarmFlag = false;
                        toneGenerator.stopTone();   //Para el sonido
                    }
                }

                dataManager.setAmountOfAlarms(amountOfAlarms1,amountOfAlarms2,amountOfAlarms3); //Guardar en el dispositivo la cantidad de alarmas
                cameraClass.resetIndicators();
                handler.postDelayed(this, 250);
            }
        };

        handler.postDelayed(runnable, 250);


    }


    //-----MÉTODOS DE LA ACTIVIDAD-----


    //Al cerrar la app
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cameraView != null) {
            cameraView.disableView();
        }
    }

    //Al minimizar la app
    @Override
    protected void onPause() {
        super.onPause();

        if (cameraView != null) {
            cameraView.disableView();
        }

    }

    //Al maximizar la app
    @Override
    protected void onResume() {
        super.onResume();

        //Para debuguear y comprobar si está conectado el manager. Si no se abrió, se intenta volver a abrir
        //Variable que se va a utilizar para depurar. Va a contener mensajes de error o éxito
        if (OpenCVLoader.initDebug()) {
            try {
                cameraClass.baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);  //Conectarse al manager de la cámara del obj de la clase Camera
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, cameraClass.baseLoaderCallback);
        }


        //DataManager dataManager = new DataManager(MainActivity.this);

        id = dataManager.getId();   //Conseguir el id que está guardado en el dispositivo

        if (id != -1) { //Si el id no es -1 (es decir, si existe)

            //Las variables se cargan con los datos del dispositivo
            firstName = dataManager.getFirstName();
            lastName = dataManager.getLastName();
            gender = dataManager.getGender();
            dateOfBirth = dataManager.getDateOfBirth();
            amountOfAlarms1 = dataManager.getAmountOfAlarms1();
            amountOfAlarms2 = dataManager.getAmountOfAlarms2();
            amountOfAlarms3 = dataManager.getAmountOfAlarms3();

            try {
                showPopupWarning();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        id = dataManager.getId();

        if (id == -1) {

            showPopup(true);
        }
    }

    //-----FIN MÉTODOS PROPIOS DE LA ACTIVIDAD-----

    //Al tocar el botón de calibrar
    public void calibrateHeight() {

        //Al clickear en el botón de calibrar altura

        Toast.makeText(MainActivity.this, "Cámara calibrada correctamente. Tenga un buen viaje.", Toast.LENGTH_LONG).show();
        detectorClass = new Detector(cameraClass.getFaceHeight(), cameraClass.getEyesDetected()); //Se crea un objeto nuevo de Detector con parámetros obtenido del objeto de Camera
        flagCalibrated = !(detectorClass.getCalibrationError());  //Flag de que está calibrada la altura si no dio error
    }


    //Mostrar el popup con la carga de información
    public void showPopup(final boolean firstTime) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder.setTitle("  Introduzca sus datos");  //Título del popup
        alertDialogBuilder.setIcon(R.color.transparent);    //Ícono del popup (en este caso, transparente)
        alertDialogBuilder.setCancelable(!firstTime);   //Si es la primera vez, no es cancelable (botón de back del teléfono desactivado)

        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View popupInputDialogView = layoutInflater.inflate(R.layout.popup, null); //Se indica que el cuadro va a usar el layout "popup"
        alertDialogBuilder.setView(popupInputDialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();   //Mostrar el popup

        if (firstTime) {    //Si es la primera vez, no muestra el botón de cancelar
            popupInputDialogView.findViewById(R.id.popupCancel).setVisibility(View.INVISIBLE);
        } else {
            popupInputDialogView.findViewById(R.id.popupCancel).setVisibility(View.VISIBLE);
        }

        //Se instancia un objeto de calendario y se consigue la fecha de hoy
        final Calendar calendar = Calendar.getInstance();

        final TextView popupBirth = popupInputDialogView.findViewById(R.id.popupBirth);
        popupBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                final int month = calendar.get(Calendar.MONTH);
                final int year = calendar.get(Calendar.YEAR);


                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {

                        //Al hacer click en aceptar dentro de la ventana del calendario, se guarda en un string aaaa-mm-dd y se agrega el cero adelante según corresponda
                        String strDay = String.valueOf(mDay);
                        if (strDay.length() == 1) {
                            strDay = "0" + strDay;
                        }

                        String strMonth = String.valueOf(mMonth + 1);
                        if (strMonth.length() == 1) {
                            strMonth = "0" + strMonth;
                        }

                        dateOfBirth = mYear + "-" + (strMonth) + "-" + strDay;
                        popupBirth.setText(dateOfBirth);    //Se pone el string creado en la etiqueta del popup
                    }
                }, year, month, day);
                datePickerDialog.show();
            }


        });


        confirmData = popupInputDialogView.findViewById(R.id.popupConfirm);

        confirmData.setOnClickListener(new View.OnClickListener() {        //Se le asigna un listener al botón
            @Override
            public void onClick(View view) {

                //Al hacer click en acpetar en el popup

                EditText textFirstName = popupInputDialogView.findViewById(R.id.popupFirstName);
                EditText textLastName = popupInputDialogView.findViewById(R.id.popupLastName);
                Spinner spinnerGender = popupInputDialogView.findViewById(R.id.popupGender);

                if (!dateOfBirth.equals("")) {  //Si se introdujo una fecha
                    if (!(Integer.parseInt(dateOfBirth.substring(0, 3)) >= (calendar.get(Calendar.YEAR) - 16))) {   //Se comprueba que la fecha de nacimiento sea de por lo menos 16 años


                        if (!textLastName.getText().toString().equals("") && !textFirstName.getText().toString().equals("") && !spinnerGender.getSelectedItem().toString().equals("")) {    //Si todos los datos están cargados
                            if (DataManager.checkNumbers(firstName) && DataManager.checkNumbers(lastName)) {   //Se comprueba que los nombres no tengan números

                                firstName = textFirstName.getText().toString(); //Se crean variables tomando lo que dicen los cuadros de texto
                                lastName = textLastName.getText().toString();
                                //Esta variable va a ser el número de índice del spinner (similar a combobox)
                                gender = (int) spinnerGender.getSelectedItemId() + 1;


                                dataManager.setUserData(firstName, lastName, gender, dateOfBirth);  //Se guardan en el dispositivo los datos

                                if (firstTime) {
                                    //Si es la primera vez se inserta un nuevo conductor y se devuelve el ID que es le asignó, que es guardado en la memoria del teléfono
                                    dataManager.webServiceInsertDriver("http://192.168.1.2:80/forecast/insert_driver.php");
                                    dataManager.webServiceGetDriverID("http://192.168.1.2/forecast/get_driverID.php?lastName="+dataManager.getLastName()+"&firstName="+dataManager.getFirstName()+"&dateOfBirth="+dataManager.getDateOfBirth()+"&gender="+dataManager.getGender());


                                } else {
                                    //Si ya existía el usuario, se hace un update con los datos nuevos tomando su id
                                    dataManager.webServiceUpdateDriver("http://192.168.1.2:80/forecast/update_driver.php");

                                }
                                dataManager.webServiceGetNeuralNetworkOutput("http://192.168.1.2/forecast/execute_neural_network.php?1=" + dataManager.getGender() + "&2=" + dataManager.getAgeSlot() + "&3=" + dataManager.getTimeSlot());

                                alertDialog.cancel();   //Se cierra el popup
                            } else {
                                Toast.makeText(MainActivity.this, "Su nombre debe contener solo letras.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Debe introducir todos los datos para continuar.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Debe introducir una fecha válida.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Debe introducir una fecha válida.", Toast.LENGTH_LONG).show();

                }
            }

        });


        Button popupCancel = popupInputDialogView.findViewById(R.id.popupCancel);
        popupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

    }

    public void showPopupWarning() throws IOException {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder.setTitle("             ATENCIÓN");  //Título del popup
        alertDialogBuilder.setIcon(R.color.transparent);    //Ícono del popup (en este caso, transparente)
        alertDialogBuilder.setCancelable(true);

        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View popupInputDialogView = layoutInflater.inflate(R.layout.popupwarning, null); //Se indica que el cuadro va a usar el layout "popupwarning"
        alertDialogBuilder.setView(popupInputDialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();   //Mostrar el popup



        TextView warningText = popupInputDialogView.findViewById(R.id.warningText);


        warningText.setText("LAS PROBABILIDADES DE QUEDARSE DORMIDO A ESTA HORA SON DE " + dataManager.getPercentage());







        Button popupwarningCancel = popupInputDialogView.findViewById(R.id.popupwarningCancel);
        popupwarningCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    public void debugButton() {
        dataManager.webServiceInsertAlarmsManager("http://192.168.1.2:80/forecast/insert_alarm.php");
    }



}
