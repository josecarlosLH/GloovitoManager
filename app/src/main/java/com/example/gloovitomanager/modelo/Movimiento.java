package com.example.gloovitomanager.modelo;

public class Movimiento {

    String  clienteId,
            estado,
            movimientoId;

    Double dinero;

    public Movimiento() {}

    public Movimiento(String clienteId, String estado, String movimientoId, Double dinero) {
        this.clienteId = clienteId;
        this.estado = estado;
        this.movimientoId = movimientoId;
        this.dinero = dinero;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMovimientoId() {
        return movimientoId;
    }

    public void setMovimientoId(String movimientoId) {
        this.movimientoId = movimientoId;
    }

    public Double getDinero() {
        return dinero;
    }

    public void setDinero(Double dinero) {
        this.dinero = dinero;
    }
}
