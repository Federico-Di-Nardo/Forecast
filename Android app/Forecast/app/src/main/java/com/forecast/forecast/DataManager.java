package com.forecast.forecast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import static android.content.ContentValues.TAG;
public class DataManager {

    Context context;
    SharedPreferences driverData;
    SharedPreferences.Editor driverDataEditor;
    RequestQueue requestQueue;


    DataManager(Context context) {
        this.context = context; //Se guarda el context de la main activity
        driverData = PreferenceManager.getDefaultSharedPreferences(context);    //Se establecen las sharedPreferences (almacenamiento del teléfono)
        driverDataEditor = driverData.edit();   //Se crea un editor de shared preferences
    }

    static boolean checkNumbers(String string) {

        //Método para verificar que todos los caracteres de un string sean letras
        char[] chars = string.toCharArray();
        for (int i = 0; i <= chars.length - 1; i++) {
            if (Character.isDigit(chars[i])) {
                return false;
            }
        }
        return true;
    }

    private String getDate(){
        //Método que devuelve la fecha del día de hoy
        final Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);

        String strDay = String.valueOf(day);
        if (strDay.length() == 1) {
            strDay = "0" + strDay;
        }

        String strMonth = String.valueOf(month + 1);
        if (strMonth.length() == 1) {
            strMonth = "0" + strMonth;
        }

        return year + "-" + (strMonth) + "-" + strDay;


    }

    public int getTimeSlot(){
        Calendar currentTime = Calendar.getInstance();
        if (currentTime.get(Calendar.HOUR_OF_DAY) < 14 && currentTime.get(Calendar.HOUR_OF_DAY) > 6) {
            //Franja de tiempo de la mañana
            return 1;

        } else {
            if (currentTime.get(Calendar.HOUR_OF_DAY) < 22 && currentTime.get(Calendar.HOUR_OF_DAY) > 14) {
                //Franja de tiempo de la tarde
                return 2;
            } else {
                //Franja de tiempo de la noche.
                return 3;
            }
        }
    }

    public int getAge(){
        String year = getDate().substring(0,4);
        String dob = getDateOfBirth().substring(0,4);
        return Integer.parseInt(year) - Integer.parseInt(dob);
    }

    public int getAgeSlot() {
        if (getAge() < 31){
            return 1;
        }else if (getAge() < 61){
            return 2;
        }else{
            return 3;
        }
    }

        //----MÉTODOS DE DEVOLUCIÓN DE PARÁMETROS DE LAS SHAREDPREFERENCES----

    public long getId() {
        return driverData.getLong("ID", -1);
    }

    public String getPercentage() {
        return driverData.getString("Percentage", "");
    }

    public String getFirstName() {
        return driverData.getString("FirstName", "");
    }

    public String getLastName() {
        return driverData.getString("LastName", "");
    }

    public String getDateOfBirth() {
        return driverData.getString("DateOfBirth", "");
    }

    public int getGender() {
        return driverData.getInt("Gender", 0);
    }

    public int getAmountOfAlarms1() {
        return driverData.getInt("AmountOfAlarms1", -1);
    }

    public int getAmountOfAlarms2() {
        return driverData.getInt("AmountOfAlarms2", -1);
    }

    public int getAmountOfAlarms3() {
        return driverData.getInt("AmountOfAlarms3", -1);
    }

    //----FIN MÉTODOS DE DEVOLUCIÓN DE PARÁMETROS DE LAS SHAREDPREFERENCES----

    public void setUserData(String firstName, String lastName, int gender, String dateOfBirth) {
        //Método para setear en las SharedPreferences todos los datos del usuario
        driverDataEditor.putInt("Gender", gender);
        driverDataEditor.putString("FirstName", firstName);
        driverDataEditor.putString("LastName", lastName);
        driverDataEditor.putString("DateOfBirth", dateOfBirth);
        driverDataEditor.apply();
    }

    public void setUserID(long id) {
        //Método para setear en las SharedPreferences el ID del usuario
        driverDataEditor.putLong("ID", id);
        driverDataEditor.apply();
    }

    public void setPercentage(String percentage) {
        //Método para setear en las SharedPreferences el ID del usuario
        driverDataEditor.putString("Percentage", percentage);
        driverDataEditor.apply();
    }


    public void setAmountOfAlarms(int amountOfAlarms1, int amountOfAlarms2, int amountOfAlarms3) {
        //Método para setear en las SharedPreferences las alarmas
        driverDataEditor.putInt("AmountOfAlarms1", amountOfAlarms1);
        driverDataEditor.putInt("AmountOfAlarms2", amountOfAlarms2);
        driverDataEditor.putInt("AmountOfAlarms3", amountOfAlarms3);
        driverDataEditor.apply();
    }





    //-------MÉTODOS QUE UTILIZAN LOS WEB SERVICES-------

    @SuppressLint("StaticFieldLeak")
    public void webServiceInsertDriver(String url) {
        //método para llamar la webservice que llama al SP que inserta los datos del conductor  (recibe la URL del webservice)

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() { //Crea un request usando los parámetros que están en un HashMap
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Operación exitosa", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<String, String>();
                //Carga el HashMap con los parámteros que necesita el webservice
                parameters.put("lastName", getLastName());
                parameters.put("firstName", getFirstName());
                parameters.put("dateOfBirth", getDateOfBirth());
                parameters.put("gender", String.valueOf(getGender()));
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
        SystemClock.sleep(500); //Sleep para que espere a terminar de insertar los datos antes de continuar con la ejecución del programa

    }

    public void webServiceUpdateDriver(String url) {
        //Método muy similar al de insertDriver pero tiene que usar también el driverID
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Operación exitosa", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("driverID", String.valueOf(getId()));
                parameters.put("lastName", getLastName());
                parameters.put("firstName", getFirstName());
                parameters.put("dateOfBirth", getDateOfBirth());
                parameters.put("gender", String.valueOf(getGender()));
                return parameters;
            }
        };
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    public void webServiceInsertAlarmsManager(String url){
        //Método que maneja las tres inserciones de alarmas (una por cada franja horaria) y reiniciar la cuenta
        webServiceInsertAlarms(url,1);
        webServiceInsertAlarms(url,2);
        webServiceInsertAlarms(url,3);
        setAmountOfAlarms(0,0,0);
    }

    @SuppressLint("StaticFieldLeak")
    private void webServiceInsertAlarms(String url, final int timeSlot) {
        //Método para hacer el insert de alarmas. Recibe por parámetro la url del webservice y qué franja horaria tiene que manejar
        int amountOfAlarms = 0;

        //Según la franja horaria, se le asigna a una variable la cantidad de alarmas que están almacenadas en el teléfono
        switch (timeSlot){
            case 1:
                amountOfAlarms = getAmountOfAlarms1();
                break;

            case 2:
                amountOfAlarms = getAmountOfAlarms2();
                break;

            case 3:
                amountOfAlarms = getAmountOfAlarms3();
                break;
        }

        final int finalAmountOfAlarms = amountOfAlarms;

        //El resto del método es similar a los dos anteriores

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Operación exitosa", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("driverID", String.valueOf(getId()));
                parameters.put("timeSlot", String.valueOf(timeSlot));
                parameters.put("date", getDate());
                parameters.put("amountOfAlarms", String.valueOf(finalAmountOfAlarms));
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }


    public void webServiceGetDriverID(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String id = (response.getString("DriverID"));

                            setUserID(Long.parseLong(id));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);

    }

    public void webServiceGetNeuralNetworkOutput(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String percentage = (response.getString("Percentage"));

                            setPercentage(percentage);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
        SystemClock.sleep(500); //Sleep para que espere a terminar de insertar los datos antes de continuar con la ejecución del programa

    }

    //-------FIN MÉTODOS QUE UTILIZAN LOS WEB SERVICES-------

}