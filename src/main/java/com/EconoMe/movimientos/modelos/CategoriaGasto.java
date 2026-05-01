package com.EconoMe.movimientos.modelos;

public enum CategoriaGasto {
    ALIMENTACION,
    TRANSPORTE,
    VIVIENDA,
    SALUD,
    EDUCACION,
    ENTRETENIMIENTO,
    ROPA,
    SERVICIOS,
    IMPUESTOS,
    ABONO_DEUDA,
    OTROS;

    public static CategoriaGasto[] obtenerTodas() {
        return values();
    }
}
