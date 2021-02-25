package com.example.gloovitomanager.modelo;

import java.io.Serializable;

public class Producto implements Serializable {
    String  idproducto,
            descipcion,
            nombre,
            imagenURL;

    Double precio;
    int stock;

    public Producto() { }

    public Producto(String idproducto, String descipcion, String nombre,String imagenURL, Double precio, int stock) {
        this.idproducto = idproducto;
        this.descipcion = descipcion;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.imagenURL = imagenURL;
    }

    public String getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(String idproducto) {
        this.idproducto = idproducto;
    }

    public String getDescipcion() {
        return descipcion;
    }

    public void setDescipcion(String descipcion) {
        this.descipcion = descipcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }
}
