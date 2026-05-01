package com.EconoMe.comun.mensajes;

public enum TipoMensaje {
    EXITO("success"),
    ERROR("error"),
    ADVERTENCIA("warning"),
    INFO("info");

    private final String tipo;

    TipoMensaje(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}