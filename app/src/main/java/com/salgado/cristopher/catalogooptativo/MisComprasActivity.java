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
        setContentView(R.layout.activity_mis_compras);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog = new ProgressDialog(MisComprasActivity.this);

        progressDialog.setMessage("Cargando catalogo...");
        progressDialog.show();
        SegundoHilo segundoHilo = new SegundoHilo();
        segundoHilo.execute();
    }

    private class SegundoHilo extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            convertir();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            listView.setAdapter(new MisComprasActivity.ImagenAdapter(getApplicationContext()));
            progressDialog.dismiss();
        }

        private void convertir() {
            String NAMESPACE = "http://android.app.services/";
            String METHOD_NAME = "miCatalogo";
            String URL = "http://192.168.1.37:8080/WebServiceSangucho-op-C-/CatalogoService?WSDL";
            String SOAP_ACTION = NAMESPACE + "" + METHOD_NAME;
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("idPersona", Catalogo.idPersona);
                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = false;
                soapEnvelope.setOutputSoapObject(request);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.call(SOAP_ACTION, soapEnvelope);
                SoapObject res = (SoapObject) soapEnvelope.bodyIn;
                for (int i = 0; i < res.getPropertyCount(); i++) {
                    SoapObject so = (SoapObject) res.getProperty(i);
                    int idpro = Integer.parseInt(so.getProperty("idcatalogo").toString()) - 1;
                    titulos.add(Catalogo.titulos.get(idpro).toString());
                    //descripcion.add("\nPrecio: $" + so.getProperty("precio") + "\nStock " + so.getProperty("stock"));
                    descripcion.add("\nPrecio: $" + Catalogo.precios.get(idpro).toString());
//                 descripcion.add(" Precio: $" + so.getProperty("precio") + " stock" + so.getProperty("stock"));
                    imagenes.add(Catalogo.imagenes.get(idpro).toString());
                    stocks.add(Catalogo.stocks.get(idpro).toString());
                    precios.add(so.getProperty("precio"));
                }
            } catch (Exception ex) {
                Log.d(" ----> Resp -> ", ex.getMessage().toString());
            }
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
        public View getView(int position, View convetView, ViewGroup parent) {
            ViewGroup viewGroup1 = (ViewGroup) layoutInflater.inflate(R.layout.content_catalogo_item, null);
            smartImageView = (SmartImageView) viewGroup1.findViewById(R.id.imagen1);
            tvTitulo = (TextView) viewGroup1.findViewById(R.id.tvTitulo);
            tvDesc = (TextView) viewGroup1.findViewById(R.id.tvDesc);
            String urlFinal = "http://192.168.1.37:8080/WebServiceSangucho-op-C-/" + imagenes.get(position).toString();
            Rect rect = new Rect(smartImageView.getLeft(), smartImageView.getTop(), smartImageView.getRight(), smartImageView.getBottom());
            smartImageView.setImageUrl(urlFinal, rect);
            tvTitulo.setText(titulos.get(position).toString());
            tvDesc.setText(descripcion.get(position).toString());
            return viewGroup1;
        }
    }
}
