package com.example.gloovitomanager.modelo;

import java.io.Serializable;

public class Linea implements Serializable {
    String  numlinea,
            local,
            localid,
            producto,
            productoid;

    int cantidad;

    Double  precio,
            subtotal;

    public Linea() {
    }

    public Linea(String numlinea, String local, String localid, String producto , String productoid, int cantidad, Double precio, Double subtotal) {
        this.numlinea = numlinea;
        this.local = local;
        this.producto = producto;
        this.localid = localid;
        this.productoid = productoid;
        this.cantidad = cantidad;
        this.precio = precio;
        this.subtotal = subtotal;
    }

    public String getNumlinea() {
        return numlinea;
    }

    public String getLocalid() {
        return localid;
    }

    public void setLocalid(String localid) {
        this.localid = localid;
    }

    public String getProductoid() {
        return productoid;
    }

    public void setProductoid(String productoid) {
        this.productoid = productoid;
    }

    public void setNumlinea(String numlinea) {
        this.numlinea = numlinea;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
