package com.EconoMe.movimientos;

import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.cuentas.modelos.TipoCuenta;
import com.EconoMe.movimientos.modelos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class MovimientoTest {

    private Ingreso ingreso;
    private Gasto gasto;

    @BeforeEach
    void setUp(){
        ingreso = null;
        gasto = null;
    }

    @Test
    void given_valid_data_when_create_ingreso_then_ok(){
        Double monto = 100.0;
        String descripcion = "Salario";
        Cuenta cuenta = new Cuenta("ahorros test", TipoCuenta.AHORROS, 200.0);
        CategoriaIngreso categoriaIngreso = CategoriaIngreso.SALARIO;
        ingreso = new Ingreso(monto, descripcion, cuenta, categoriaIngreso);

        assertNotNull(ingreso);
        assertEquals(monto, ingreso.getMonto());
        assertEquals(descripcion, ingreso.getDescripcion());
        assertEquals(cuenta, ingreso.getCuenta());
        assertEquals(categoriaIngreso, ingreso.getCategoriaIngreso());
    }

    @Test
    void given_valid_data_when_create_gasto_then_ok(){
        Double monto = 25.0;
        String descripcion = "Alimentos";
        Cuenta cuenta = new Cuenta("ahorros test", TipoCuenta.AHORROS, 200.0);
        CategoriaGasto categoriaGasto = CategoriaGasto.ALIMENTACION;
        gasto = new Gasto(monto, descripcion, cuenta, categoriaGasto);

        assertNotNull(gasto);
        assertEquals(monto, gasto.getMonto());
        assertEquals(descripcion, gasto.getDescripcion());
        assertEquals(cuenta, gasto.getCuenta());
        assertEquals(categoriaGasto,  gasto.getCategoriaGasto());
    }

    @Test()
    void given_null_fields_when_create_movimiento_then_exception(){
        Double monto = null;
        String descripcion = null;
        Cuenta cuenta = null;
        CategoriaIngreso categoriaIngreso = null;
        assertThrows(
                IllegalArgumentException.class,
                () -> new Ingreso(monto,descripcion,cuenta, categoriaIngreso)
        );
    }

    @Test()
    void given_negative_amount_when_create_movimiento_then_exception(){
        Double monto = -100.0;
        String descripcion = "Salario";
        Cuenta cuenta = new Cuenta("ahorros test", TipoCuenta.AHORROS, 200.0);
        CategoriaIngreso categoriaIngreso = CategoriaIngreso.SALARIO;
        assertThrows(
                IllegalArgumentException.class,
                () -> new Ingreso(monto,descripcion,cuenta, categoriaIngreso)
        );
    }
}