package com.salgado.cristopher.catalogooptativo;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity {

    int idus = 0;

    private EditText username, password;
    private Button btnIniciar;
    private TextView textView;

    String param1, param2, mensaje;
    SoapPrimitive resulString;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (EditText) findViewById(R.id.edtUsername);
        password = (EditText) findViewById(R.id.edtPassword);
        textView = (TextView) findViewById(R.id.textView);
        btnIniciar = (Button) findViewById(R.id.btnIniciarSesion);
//        Toast.makeText(this, "Holiwiii", Toast.LENGTH_LONG).show();
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                param1 = username.getText() + "";
                param2 = password.getText() + "";
                SegundoPlano tareaSeg = new SegundoPlano();
                tareaSeg.execute();
                //Log.d("----> -> ", 1 + " sgfhmfdgh");
            }
        });

    }


    private class SegundoPlano extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            convertir();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //textView.setText("Response -> " + result.toString() + " , " + mensaje);
//            int respuesta = Integer.parseInt(soapEnvelope.getResponse().toString());
            Log.d("----> -> ", "Respuesta ->> " + result);
            Log.d("----> -> ", "Respuesta ->> " + idus);
            if (idus != 0) {
                intent.putExtra("idPersona", idus);
                Catalogo.idPersona = idus;
                intent.putExtra("username", param1);
                startActivity(intent);
                Log.d(" ----> Resp -> ", "Iniciao el Catalogo de los productos");
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Uppps!");
                alertDialog.setMessage("Su clave o nombre de ususario no estan correctos");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
            }
        }
    }

    private void convertir() {
        String NAMESPACE = "http://services/";
        String METHOD_NAME = "loginAndroid";
        String URL = "http://192.168.1.37:8080/WebServiceSangucho-op-C-/LoginService?WSDL";
        String SOAP_ACTION = NAMESPACE + "" + METHOD_NAME;
        try {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("username", param1);
            request.addProperty("password", param2);
            Log.d("----> -> ", "username " + param1);
            Log.d("----> -> ", "pass " + param2);
            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = false;
            soapEnvelope.setOutputSoapObject(request);
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);
            SoapObject res = (SoapObject) soapEnvelope.bodyIn;

            intent = new Intent(this, CatalogoActivity.class);
//            Log.d("----> -> ", soapEnvelope.getResponse().toString());
            Log.d("----> -> ", soapEnvelope.bodyIn.toString());
            idus = Integer.parseInt(soapEnvelope.getResponse().toString());

        } catch (Exception ex) {
            Log.d(" ----> Resp -> ", ex.getMessage().toString());
            //Toast.makeText(this, "error ->" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
