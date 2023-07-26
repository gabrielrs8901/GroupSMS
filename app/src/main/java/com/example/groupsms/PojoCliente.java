package com.example.groupsms;

public class PojoCliente {
    private String nombreCliente;
    private String numeroCliente;

    public PojoCliente(String nombreCliente, String numeroCliente) {
        this.nombreCliente = nombreCliente;
        this.numeroCliente = numeroCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNumeroCliente() {
        return numeroCliente;
    }

    public void setNumeroCliente(String numeroCliente) {
        this.numeroCliente = numeroCliente;
    }
}
