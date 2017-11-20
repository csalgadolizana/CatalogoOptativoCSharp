package com.salgado.cristopher.catalogooptativo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.snowdream.android.widget.SmartImageView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class CatalogoActivity extends AppCompatActivity {

    private ListView listView;

    ProgressDialog progressDialog;

//    ArrayList titulos = new ArrayList();
//    ArrayList descripcion = new ArrayList();
//    ArrayList imagenes = new ArrayList();
//    ArrayList precios = new ArrayList();
//    ArrayList stocks = new ArrayList();

    String username;
    int idPersona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(" ----> Resp -> ", "Llegó al create");
        try {
            Catalogo.init();


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_catalogo);
        setTitle(R.string.label_activity_catalogo);
            idPersona = getIntent().getIntExtra("idPersona", 0);
            username = getIntent().getStringExtra("username");
            Toast.makeText(this, "persona ->> " + username, Toast.LENGTH_LONG).show();
            listView = (ListView) findViewById(R.id.listView);
            progressDialog = new ProgressDialog(CatalogoActivity.this);

            progressDialog.setMessage("Cargando catalogo...");
            progressDialog.show();
            getCatalogo();
        } catch (Exception ex) {
            Log.d(" ----> Resp -> ", ex.getMessage().toString());
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d("----> Resp ->", "click en el elemento " + position + " de mi ListView");
                Log.d("----> Resp ->", "ruta " + Catalogo.imagenes.get(position) + " ");
                Intent intent = new Intent(CatalogoActivity.this, VerOfertaActivity.class);
                intent.putExtra("stock", Catalogo.stocks.get(position).toString());
                intent.putExtra("precio", Catalogo.precios.get(position).toString());
                intent.putExtra("ruta", Catalogo.imagenes.get(position).toString());
                intent.putExtra("nombre", Catalogo.titulos.get(position).toString());
                intent.putExtra("id", position + 1 + "");
                intent.putExtra("idPersona", idPersona + "");
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

//    @Override
//    protected void onResume() {
//        Log.d(" ----> Resp -> "," OnResume()");
////        try {
////
////        }
//        super.onResume();
//        //listView.removeAllViews();
//    }


    @Override
    protected void onRestart() {
        Log.d(" ----> Resp -> ", " onRestart()");
        try {
            listView.setAdapter(new ImagenAdapter(getApplicationContext()));

        } catch (Exception ex) {
            Log.d(" ----> Resp -> ", ex.getMessage().toString());
        }
        super.onRestart();
    }

    private void getCatalogo() {
        Catalogo.titulos.clear();
        Catalogo.imagenes.clear();
        Catalogo.descripcion.clear();
        SegundoPlano tarea = new SegundoPlano();
        tarea.execute();
    }

    private class SegundoPlano extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String result) {
            listView.setAdapter(new ImagenAdapter(getApplicationContext()));
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            convertir();
            return null;
        }

    }

    private void convertir() {
        String NAMESPACE = "http://android.app.services/";
        String METHOD_NAME = "listarCatalogo";
        String URL = "http://169.254.195.150:8080/WebServiceSangucho-op-C-/CatalogoService?WSDL";
        String SOAP_ACTION = NAMESPACE + "" + METHOD_NAME;
        try {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = false;
            soapEnvelope.setOutputSoapObject(request);
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);
            SoapObject res = (SoapObject) soapEnvelope.bodyIn;
            for (int i = 0; i < res.getPropertyCount(); i++) {
                SoapObject so = (SoapObject) res.getProperty(i);
                Catalogo.titulos.add(so.getProperty("nombreproducto"));
                Catalogo.descripcion.add("\nPrecio: $" + so.getProperty("precio") + "\nStock " + so.getProperty("stock"));
//                Catalogo.descripcion.add(" Precio: $" + so.getProperty("precio") + " stock" + so.getProperty("stock"));
                Catalogo.imagenes.add(so.getProperty("rutaImg"));
                Catalogo.stocks.add(so.getProperty("stock"));
                Catalogo.precios.add(so.getProperty("precio"));
            }
        } catch (Exception ex) {
            Log.d(" ----> Resp -> ", ex.getMessage().toString());
        }
    }

    private class ImagenAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater layoutInflater;
        SmartImageView smartImageView;
        TextView tvTitulo, tvDesc;

        public ImagenAdapter(Context context) {
            this.ctx = context;
            layoutInflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return Catalogo.imagenes.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convetView, ViewGroup parent) {
            ViewGroup viewGroup1 = (ViewGroup) layoutInflater.inflate(R.layout.content_catalogo_item, null);
            smartImageView = (SmartImageView) viewGroup1.findViewById(R.id.imagen1);
            tvTitulo = (TextView) viewGroup1.findViewById(R.id.tvTitulo);
            tvDesc = (TextView) viewGroup1.findViewById(R.id.tvDesc);
            String urlFinal = "http://169.254.195.150:8080/WebServiceSangucho-op-C-/" + Catalogo.imagenes.get(position).toString();
            Rect rect = new Rect(smartImageView.getLeft(), smartImageView.getTop(), smartImageView.getRight(), smartImageView.getBottom());
            smartImageView.setImageUrl(urlFinal, rect);
            tvTitulo.setText(Catalogo.titulos.get(position).toString());
            tvDesc.setText(Catalogo.descripcion.get(position).toString());
            return viewGroup1;
        }
    }

}
