package com.salgado.cristopher.catalogooptativo;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.snowdream.android.widget.SmartImageView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class MisComprasActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    private ListView listView;

    ArrayList titulos = new ArrayList();
    ArrayList descripcion = new ArrayList();
    ArrayList imagenes = new ArrayList();
    ArrayList precios = new ArrayList();
    ArrayList stocks = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = (ListView) findViewById(R.id.listViewMisComprasss);
        setContentView(R.layout.activity_mis_compras);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listView = (ListView) findViewById(R.id.listViewMisComprasss);
        progressDialog = new ProgressDialog(MisComprasActivity.this);
        Log.d(" ----> Resp -> ", "Catalogo.idPersona -> " + Catalogo.idPersona + "1");
        progressDialog.setMessage("Cargando catalogo...");
        progressDialog.show();
        SegundoHilo segundoHilo = new SegundoHilo();
        segundoHilo.execute();
    }

    private class SegundoHilo extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String result) {
            Log.d(" ----> Resp -> ", "Entro al onPostExecute(String result)");
            progressDialog.dismiss();
            Log.d(" ----> Resp -> ", "hizo el progressDialog.dismiss()");
//            listView.setAdapter(new );
            listView.setAdapter(new ImagenAdapter2(getApplicationContext()));
        }


        @Override
        protected String doInBackground(String... strings) {
            convertir();
            return null;
        }

    }

    private void convertir() {
        String NAMESPACE = "http://android.app.services/";
        String METHOD_NAME = "miCatalogo";
        String URL = "http://192.168.1.38:8080/WebServiceSangucho-op-C-/CatalogoService?WSDL";
        String SOAP_ACTION = NAMESPACE + "" + METHOD_NAME;
        try {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            Log.d(" ----> Resp -> ", "Catalogo.idPersona -> " + Catalogo.idPersona + "");
            request.addProperty("idPersona", Catalogo.idPersona);
            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = false;
            soapEnvelope.setOutputSoapObject(request);
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);
            SoapObject res = (SoapObject) soapEnvelope.bodyIn;
            for (int i = 0; i < res.getPropertyCount(); i++) {
                SoapObject so = (SoapObject) res.getProperty(i);
                Log.d(" ----> Resp -> ", so.toString());
                int idpro = Integer.parseInt(so.getProperty("idcatalogo").toString()) - 1;
                titulos.add(Catalogo.titulos.get(idpro).toString());
                //descripcion.add("\nPrecio: $" + so.getProperty("precio") + "\nStock " + so.getProperty("stock"));
                descripcion.add("\nPrecio: $" + Catalogo.precios.get(idpro).toString());
//                 descripcion.add(" Precio: $" + so.getProperty("precio") + " stock" + so.getProperty("stock"));
                imagenes.add(Catalogo.imagenes.get(idpro).toString());
                stocks.add(Catalogo.stocks.get(idpro).toString());
                precios.add(so.getProperty("precio"));
            }
            Log.d(" ----> Resp -> ", "Fin del convertir()");
        } catch (Exception ex) {
            Log.d(" ----> Resp -> ", ex.getMessage().toString());
        }
    }


    private class ImagenAdapter2 extends BaseAdapter {
        Context ctx;
        LayoutInflater layoutInflater;
        SmartImageView smartImageView;
        TextView tvTitulo, tvDesc;

        public ImagenAdapter2(Context context) {
            Log.d(" ----> Resp -> ", "Entro al ImagenAdapter2(Context context)");
            this.ctx = context;
            layoutInflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return imagenes.size();
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
        public View getView(int position, View view, ViewGroup viewGroup) {
            Log.d(" ----> Resp -> ", "getView() 1");
            ViewGroup viewGroup1 = (ViewGroup) layoutInflater.inflate(R.layout.content_catalogo_mis_compras_item, null);
            Log.d(" ----> Resp -> ", "getView() 2");
            smartImageView = (SmartImageView) viewGroup1.findViewById(R.id.imagen2);
            Log.d(" ----> Resp -> ", "getView() 3");
            tvTitulo = (TextView) viewGroup1.findViewById(R.id.tvTitulo2);
            Log.d(" ----> Resp -> ", "getView() 4");
            tvDesc = (TextView) viewGroup1.findViewById(R.id.tvDesc2);
            Log.d(" ----> Resp -> ", "getView() 5");
            String urlFinal = "http://192.168.1.38:8080/WebServiceSangucho-op-C-/" + imagenes.get(position).toString();
            Log.d(" ----> Resp -> ", "getView() 6");
            Rect rect = new Rect(smartImageView.getLeft(), smartImageView.getTop(), smartImageView.getRight(), smartImageView.getBottom());
            Log.d(" ----> Resp -> ", "getView() 7");
            smartImageView.setImageUrl(urlFinal, rect);
            Log.d(" ----> Resp -> ", "getView() 8");
            tvTitulo.setText(titulos.get(position).toString());
            Log.d(" ----> Resp -> ", "getView() 9");
            tvDesc.setText(descripcion.get(position).toString());
            Log.d(" ----> Resp -> ", "getView() 10");
            return viewGroup1;

        }


    }
}
