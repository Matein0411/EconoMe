package com.EconoMe.movimientos.modelos;

public enum CategoriaIngreso {
    SALARIO,
    VENTAS,
    REGALOS,
    INTERESES,
    ABONO_PRESTAMO,
    OTROS;

    public static CategoriaIngreso[] obtenerTodas() {
        return values();
    }
}
