package com.salgado.cristopher.catalogooptativo;

import java.util.ArrayList;

/**
 * Created by PC-Cristopher on 19/11/2017.
 */

public final class Catalogo {
    static int idPersona;
    static public ArrayList titulos;
    static public ArrayList descripcion;
    static public ArrayList imagenes;
    static public ArrayList precios;
    static public ArrayList stocks;

    public static  void init() {
        titulos = new ArrayList();
        descripcion = new ArrayList();
        imagenes = new ArrayList();
        precios = new ArrayList();
        stocks = new ArrayList();
    }
}
