package com.example.giovanny.choferburra;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private int TiempoCiclo=2000;
    boolean activado;
    String url ="http://52.39.235.232:8081/burra/";
    //String url ="http://127.0.0.1:8080/burra/";

    ConexionServer cs;
    Localization localization;
    ArrayAdapter<CharSequence> adapter;
    TextView longi,lati,resp;
    double longitud,latitud;
    String bus;

    private final int code_request=1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, code_request);
        }
        activado =false;
        cs= new ConexionServer();
        localization= new Localization(this);
        longitud=0f;
        latitud=0f;
        longi = (TextView)findViewById(R.id.tLongitud);
        lati = (TextView)findViewById(R.id.tLatitud);
        resp = (TextView)findViewById(R.id.tEstado);
        bus="Norte";
        Spinner spinner = (Spinner) findViewById(R.id.spiBurra);
        spinner.setOnItemSelectedListener(this);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.burra_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case code_request:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(MainActivity.this, "COARSE LOCATION permitido", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "COARSE LOCATION no permitido", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(activado);
            HiloLanzador();
    }

    @Override
    protected void onStop() {
        super.onStop();
        activado = false;
    }

    private void HiloLanzador() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (activado) {
                    espTiempo(TiempoCiclo);
                    new RcfServer(url).execute();
                }
            }
        }).start();
    }

    public void espTiempo(int Time){
        try {
            Thread.sleep(Time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(parent.getContext(), "Selected: " + position +"_"+adapter.getItem(position), Toast.LENGTH_LONG).show();
        bus= (String) adapter.getItem(position);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class RcfServer extends AsyncTask<String, Void, String> {
        String url;
        public RcfServer(String url){
            this.url=url;
        }

        @Override
        protected String doInBackground(String... urls) {
            String respues="...";

            try {
                String [] part = localization.posicion.split(":");
                if(!part[0].equals("0")) {
                    String urlfin=url + bus + ":" + localization.posicion;

                    Log.d("localizacion",urlfin);
                    respues = cs.sendToUrl(urlfin);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return respues;
        }

        @Override
        protected void onPostExecute(String result) {
            resp.setText(result);
            String []posi = localization.posicion.split(":");
            double longis=Double.parseDouble(posi[0]);
            double latis=Double.parseDouble(posi[1]);
            if(longitud!=longis || latitud!=latis){
                longitud=longis;
                latitud=latis;
                Toast.makeText(getBaseContext(), "new: "+latitud+","+longitud, Toast.LENGTH_LONG).show();
            }


            longi.setText(posi[0]);
            lati.setText(posi[1]);
        }
    }

    public void mandarSenal(View view) {

        activado = ((CheckBox) view).isChecked();
        if(activado){
            Toast.makeText(this, "ACTIVADO!! ", Toast.LENGTH_LONG).show();
            HiloLanzador();
        }

        else{
            Toast.makeText(this, "desactivado!! ", Toast.LENGTH_LONG).show();
        }


    }

}
