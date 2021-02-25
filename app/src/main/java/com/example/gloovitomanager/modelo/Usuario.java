package com.example.gloovitomanager.modelo;

import java.io.Serializable;

public class Usuario implements Serializable {
    String  id,
            nombre,
            mail;

    Double cartera, reserva;

    public Usuario() {}

    public Usuario(String id, String nombre, Double cartera, Double reserva,String mail) {
        this.id = id;
        this.nombre = nombre;
        this.cartera = cartera;
        this.reserva = reserva;
        this.mail = mail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getCartera() {
        return cartera;
    }

    public void setCartera(Double cartera) {
        this.cartera = cartera;
    }

    public Double getReserva() {
        return reserva;
    }

    public void setReserva(Double reserva) {
        this.reserva = reserva;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}