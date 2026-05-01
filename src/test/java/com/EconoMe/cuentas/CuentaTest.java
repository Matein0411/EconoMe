package com.EconoMe.cuentas;

import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.cuentas.modelos.TipoCuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    private Cuenta cuenta;

    @BeforeEach
    void setUp(){
        cuenta = null;
    }

    @Test
    void given_valid_data_when_create_cuenta_then_ok(){

        String nombre = "cuenta corriente";
        TipoCuenta tipo = TipoCuenta.CORRIENTE;
        double monto = 10.0;

        cuenta = new Cuenta(nombre, tipo, monto);
        assertNotNull(cuenta);
        assertEquals("cuenta corriente", cuenta.getNombre());
        assertEquals(TipoCuenta.CORRIENTE, cuenta.getTipoCuenta());
        assertEquals(10.0, cuenta.getMonto());
    }

    @Test
    void given_cuenta_efectivo_when_create_then_tipo_is_efectivo(){
        cuenta = new Cuenta("Billetera", TipoCuenta.EFECTIVO, 50.0);

        assertEquals(TipoCuenta.EFECTIVO, cuenta.getTipoCuenta());
    }

    @Test
    void given_cuenta_ahorros_when_create_then_tipo_is_ahorros(){
        cuenta = new Cuenta("Mi ahorro", TipoCuenta.AHORROS, 100.0);

        assertEquals(TipoCuenta.AHORROS, cuenta.getTipoCuenta());
    }

    @Test
    void given_cuenta_corriente_when_create_then_tipo_is_corriente(){
        cuenta = new Cuenta("Corriente", TipoCuenta.CORRIENTE, 150.0);

        assertEquals(TipoCuenta.CORRIENTE, cuenta.getTipoCuenta());
    }


}