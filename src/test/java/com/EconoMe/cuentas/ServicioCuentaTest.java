package com.EconoMe.cuentas;

import com.EconoMe.cuentas.dao.DAOCuenta;
import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.cuentas.servicios.ServicioCuenta;
import com.EconoMe.cuentas.modelos.TipoCuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioCuentaTest {

    @Mock
    private DAOCuenta daoCuenta;

    private ServicioCuenta servicioCuenta;

    @BeforeEach
    void setUp() {
        servicioCuenta = new ServicioCuenta(daoCuenta);
    }

    @Test
    void givenValidAccountData_whenCreatingAccount_thenAccountIsCreated() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Ahorros");
        cuenta.setTipoCuenta(TipoCuenta.AHORROS);
        cuenta.setMonto(100.0);

        when(daoCuenta.existeCuentaPorNombreYTipo("Ahorros", TipoCuenta.AHORROS))
                .thenReturn(false);

        servicioCuenta.crearCuenta(cuenta);

        verify(daoCuenta).crear(cuenta);
    }

    @Test
    void givenNullAccountName_whenCreatingAccount_thenThrowsException() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNombre(null);
        cuenta.setTipoCuenta(TipoCuenta.AHORROS);
        cuenta.setMonto(100.0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.crearCuenta(cuenta)
        );

        assertEquals("El nombre de la cuenta es obligatorio", exception.getMessage());
        verify(daoCuenta, never()).crear(any());
    }

    @Test
    void givenBalanceBelowMinimum_whenCreatingAccount_thenThrowsException() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Efectivo");
        cuenta.setTipoCuenta(TipoCuenta.EFECTIVO);
        cuenta.setMonto(0.0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.crearCuenta(cuenta)
        );

        assertTrue(exception.getMessage().contains("El saldo debe ser mayor a"));
        verify(daoCuenta, never()).crear(any());
    }

    @Test
    void givenDuplicateAccount_whenCreatingAccount_thenThrowsException() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Ahorros");
        cuenta.setTipoCuenta(TipoCuenta.AHORROS);
        cuenta.setMonto(100.0);

        when(daoCuenta.existeCuentaPorNombreYTipo("Ahorros", TipoCuenta.AHORROS))
                .thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> servicioCuenta.crearCuenta(cuenta)
        );

        assertTrue(exception.getMessage().contains("Ya existe una cuenta"));
        verify(daoCuenta, never()).crear(any());
    }

    @Test
    void givenSufficientBalance_whenAdjustingAmount_thenBalanceIsUpdated() {
        Long cuentaId = 1L;
        Cuenta cuenta = new Cuenta();
        cuenta.setId(cuentaId);
        cuenta.setMonto(100.0);
        double cambio = -30.0;

        when(daoCuenta.buscarPorId(cuentaId)).thenReturn(cuenta);

        servicioCuenta.ajustarMonto(cuentaId, cambio);

        assertEquals(70.0, cuenta.getMonto(), 0.01);
        verify(daoCuenta).actualizar(cuenta);
    }

    @Test
    void givenPositiveAmount_whenAdjustingAmount_thenBalanceIncreases() {
        Long cuentaId = 2L;
        Cuenta cuenta = new Cuenta();
        cuenta.setId(cuentaId);
        cuenta.setMonto(50.0);
        double cambio = 25.50;

        when(daoCuenta.buscarPorId(cuentaId)).thenReturn(cuenta);

        servicioCuenta.ajustarMonto(cuentaId, cambio);

        assertEquals(75.50, cuenta.getMonto(), 0.01);
        verify(daoCuenta).actualizar(cuenta);
    }

    @Test
    void givenInsufficientBalance_whenCalculatingNewBalance_thenThrowsException() {
        double saldoActual = 50.0;
        double cambio = -100.0;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCuenta.calcularNuevoSaldo(saldoActual, cambio)
        );

        assertTrue(exception.getMessage().contains("Saldo insuficiente"));
        assertTrue(exception.getMessage().contains("50"));
        assertTrue(exception.getMessage().contains("-100"));
    }

    @Test
    void givenSufficientBalance_whenCheckingBalance_thenReturnsTrue() {
        Long cuentaId = 3L;
        double montoRequerido = 30.0;

        when(daoCuenta.obtenerMonto(cuentaId)).thenReturn(100.0);

        boolean resultado = servicioCuenta.tieneSaldoSuficiente(cuentaId, montoRequerido);

        assertTrue(resultado);
        verify(daoCuenta).obtenerMonto(cuentaId);
    }

    @Test
    void givenInsufficientBalance_whenCheckingBalance_thenReturnsFalse() {
        Long cuentaId = 4L;
        double montoRequerido = 150.0;

        when(daoCuenta.obtenerMonto(cuentaId)).thenReturn(100.0);

        boolean resultado = servicioCuenta.tieneSaldoSuficiente(cuentaId, montoRequerido);

        assertFalse(resultado);
    }

    @Test
    void givenAmountWithExtraDecimals_whenRounding_thenRoundsToTwoDecimals() {
        double montoConDecimales = 123.456789;

        double resultado = servicioCuenta.redondearMonto(montoConDecimales);

        assertEquals(123.46, resultado, 0.001);
    }
}