package com.salgado.cristopher.catalogooptativo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.snowdream.android.widget.SmartImageView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class VerOfertaActivity extends AppCompatActivity {

    //    SmartImageView imageView;
    TextView tvTit, tvStock, tvPrecio, tvDescr;
    ImageView imageView;

    int stock, precio, idproducto, idPersona;
    String ruta, nombre, username;
    Bitmap obtener_imagen;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_oferta);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        idproducto = Integer.parseInt(getIntent().getStringExtra("id"));
//        ruta = getIntent().getStringExtra("ruta");
        precio = Integer.parseInt(Catalogo.precios.get(idproducto - 1).toString());
        nombre = Catalogo.titulos.get(idproducto - 1).toString();
        stock = Integer.parseInt(Catalogo.stocks.get(idproducto - 1).toString());
        ruta = Catalogo.imagenes.get(idproducto - 1).toString();

        idPersona = Integer.parseInt(getIntent().getStringExtra("idPersona"));
        Toast.makeText(VerOfertaActivity.this, "id persona : " + idPersona, Toast.LENGTH_LONG).show();
        username = getIntent().getStringExtra("username");

        tvTit = (TextView) findViewById(R.id.titulo);
        tvDescr = (TextView) findViewById(R.id.desc);
        tvTit.setText(nombre);
        tvDescr.setText("\n\nPrecio : " + precio + "\n" + "Cantidad disponible : " + stock);
        imageView = (ImageView) findViewById(R.id.image_view);
        ruta = "http://192.168.1.37:8080/WebServiceSangucho-op-C-/" + ruta;
        Log.d(" ----> Resp -> ", ruta);
        SegundoPlano segundoPlano = new SegundoPlano();
        segundoPlano.execute();
    }

    public void btnVolverClick(View view) {
        this.finish();
    }

    public void btnConfirmarCompraClick(View view) {
        final AlertDialog alertDialog = new AlertDialog.Builder(VerOfertaActivity.this).create();
        alertDialog.setTitle("Confirmacion");
        alertDialog.setMessage("¿Realmente desea comprar el producto \n por $" + precio + "?");
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Comprar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                progressDialog = new ProgressDialog(VerOfertaActivity.this);
                progressDialog.setMessage("Realizando la compra...");
                progressDialog.show();
                compraProductoSegundoPlano compraProductoSegundoPlano = new compraProductoSegundoPlano();
                compraProductoSegundoPlano.execute();
                Catalogo.stocks.set(idproducto - 1, stock - 1);
                stock = Integer.parseInt(Catalogo.stocks.get(idproducto - 1).toString());
                Catalogo.descripcion.set(idproducto - 1, "\nPrecio: $" + precio + "\nStock " + stock);
                Toast.makeText(VerOfertaActivity.this, "Quedan " + Catalogo.stocks.get(idproducto - 1).toString() + " productos", Toast.LENGTH_LONG);
                tvDescr.setText("\n\nPrecio : " + precio + "\n" + "Cantidad disponible : " + stock);
                AlertDialog avisioCompra = new AlertDialog.Builder(VerOfertaActivity.this).create();
                avisioCompra.setTitle("Aviso");
                avisioCompra.setMessage("La compra se realizo con exito \n¿Desea seguir comprando?");
                avisioCompra.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(VerOfertaActivity.this,CatalogoActivity.class);
                        startActivity(intent);
                    }
                });
                avisioCompra.setButton(DialogInterface.BUTTON_POSITIVE, "Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        VerOfertaActivity.this.finish();
                    }
                });
            }
        });
        alertDialog.show();
    }

    private class compraProductoSegundoPlano extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            comprar();
            progressDialog.dismiss();
            return null;
        }

        private void comprar() {
            String NAMESPACE = "http://android.app.services/";
            String METHOD_NAME = "crearCompras";
            String URL = "http://192.168.1.37:8080/WebServiceSangucho-op-C-/CompraService?WSDL";
            String SOAP_ACTION = NAMESPACE + "" + METHOD_NAME;
            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("idusuario", idPersona);
                request.addProperty("cantidad", 1);
                request.addProperty("precio", precio);
                request.addProperty("idcatalogo", idproducto);
                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = false;
                soapEnvelope.setOutputSoapObject(request);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.call(SOAP_ACTION, soapEnvelope);
                SoapObject res = (SoapObject) soapEnvelope.bodyIn;
                Toast.makeText(VerOfertaActivity.this, "Respuesta " + res.toString(), Toast.LENGTH_LONG).show();
                Log.d(" ----> Resp -> ", "Respuesta " + res.toString());
//                for (int i = 0; i < res.getPropertyCount(); i++) {
//                    SoapObject so = (SoapObject) res.getProperty(i);
//                    titulos.add(so.getProperty("nombreproducto"));
//                    descripcion.add("\nPrecio: $" + so.getProperty("precio") + "\nStock " + so.getProperty("stock"));
////                descripcion.add(" Precio: $" + so.getProperty("precio") + " stock" + so.getProperty("stock"));
//                    imagenes.add(so.getProperty("rutaImg"));
//                    stocks.add(so.getProperty("stock"));
//                    precios.add(so.getProperty("precio"));
//                }
            } catch (Exception ex) {
                Log.d(" ----> Resp -> ", ex.getMessage().toString());
            }
        }
    }


    private class SegundoPlano extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String result) {
            imageView.setImageBitmap(obtener_imagen);
        }

        @Override
        protected String doInBackground(String... params) {
            get_imagen();
            return null;
        }

        private Bitmap get_imagen() {
            Bitmap bm = null;
            try {
                URL _url = new URL(ruta);
                URLConnection con = _url.openConnection();
                con.connect();
                InputStream is = con.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.d(" ----> Resp -> ", e.getMessage());
            }
            obtener_imagen = bm;
            return bm;
        }
    }

}
