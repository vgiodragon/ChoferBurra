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


public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {
    private int TiempoCiclo=3000;
    boolean activado;
    String url ="http://52.37.128.123:8081/burra/";

    ConexionServer cs;
    //Localization localization;
    GLocalization glocalization;
    ArrayAdapter<CharSequence> adapter;
    TextView longi,lati,resp;
    double longitud,latitud;
    String bus;

    private final int code_request=1234;
    //GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, code_request);
        }
        activado =false;
        cs= new ConexionServer();
        //localization= new Localization(this);
        glocalization = new GLocalization(this);
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
        //setGoogleApliClient();
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case code_request:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "COARSE LOCATION permitido", Toast.LENGTH_SHORT)
                            .show();
                } else {
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
    public void onBackPressed() {
        super.onBackPressed();
        activado=false;
    }

    @Override
    protected void onStop() {
        glocalization.onStop();
        super.onStop();
    }

    private void HiloLanzador() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (activado) {
                    //glocalization.onStop();
                    espTiempo(TiempoCiclo);
                    //glocalization.onStart();
                    new RcfServer(url).execute();

                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        glocalization.onStart();
        super.onStart();
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
                String [] part = glocalization.getLL().split(":");
                if(!part[0].equals("0.1") &&!part[0].equals("-12.04")) {
                    String urlfin=url + bus + ":" + glocalization.getLL();

                    Log.d("localizacion",urlfin);

                    double longis=Double.parseDouble(part[0]);
                    double latis=Double.parseDouble(part[1]);
                    if(longitud!=longis || latitud!=latis){
                        longitud=longis;
                        latitud=latis;
                        respues = cs.sendToUrl(urlfin);
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            return respues;
        }

        @Override
        protected void onPostExecute(String result) {
            resp.setText(result);
            //String []posi = localization.posicion.split(":");
            String []posi = glocalization.getLL().split(":");
            double longis=Double.parseDouble(posi[0]);
            double latis=Double.parseDouble(posi[1]);
            if(longitud!=longis || latitud!=latis){
                longitud=longis;
                latitud=latis;
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
        }else {
            Toast.makeText(this, "desactivado!! ", Toast.LENGTH_LONG).show();
        }
    }

}
