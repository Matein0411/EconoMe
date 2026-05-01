package com.EconoMe.obligaciones;


import com.EconoMe.obligaciones.modelos.EstadoObligacionFinanciera;
import com.EconoMe.obligaciones.modelos.ObligacionFinanciera;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de ObligacionFinanciera")
class ObligacionFinancieraTest {

    // Clase concreta para testing (ya que ObligacionFinanciera es abstracta)
    static class ObligacionFinancieraImpl extends ObligacionFinanciera {
        public ObligacionFinancieraImpl(String nombrePersona, double montoTotal, LocalDate fechaPago) {
            super(nombrePersona, montoTotal, fechaPago);
        }
    }

    private ObligacionFinanciera obligacion;

    @BeforeEach
    void setUp() {
        obligacion = new ObligacionFinancieraImpl("Juan Pérez", 1000.0, LocalDate.now().plusDays(10));
    }

    @Nested
    @DisplayName("calcularSaldoPendiente()")
    class CalcularSaldoPendienteTests {

        @Test
        @DisplayName("given_obligacionSinPagos_when_calcularSaldoPendiente_then_retornaMontoTotal")
        void given_obligacionSinPagos_when_calcularSaldoPendiente_then_retornaMontoTotal() {
            double saldo = obligacion.calcularSaldoPendiente();

            assertEquals(1000.0, saldo, 0.01);
        }

        @Test
        @DisplayName("given_obligacionConPagoParcial_when_calcularSaldoPendiente_then_retornaSaldoRestante")
        void given_obligacionConPagoParcial_when_calcularSaldoPendiente_then_retornaSaldoRestante() {
            obligacion.setMontoPagado(400.0);

            double saldo = obligacion.calcularSaldoPendiente();

            assertEquals(600.0, saldo, 0.01);
        }

        @Test
        @DisplayName("given_obligacionTotalmentePagada_when_calcularSaldoPendiente_then_retornaCero")
        void given_obligacionTotalmentePagada_when_calcularSaldoPendiente_then_retornaCero() {
            obligacion.setMontoPagado(1000.0);

            double saldo = obligacion.calcularSaldoPendiente();

            assertEquals(0.0, saldo, 0.01);
        }
    }

    @Nested
    @DisplayName("estaPagadaCompletamente()")
    class EstaPagadaCompletamenteTests {

        @Test
        @DisplayName("given_obligacionSinPagos_when_estaPagadaCompletamente_then_retornaFalse")
        void given_obligacionSinPagos_when_estaPagadaCompletamente_then_retornaFalse() {
            boolean estaPagada = obligacion.estaPagadaCompletamente();

            assertFalse(estaPagada);
        }

        @Test
        @DisplayName("given_obligacionTotalmentePagada_when_estaPagadaCompletamente_then_retornaTrue")
        void given_obligacionTotalmentePagada_when_estaPagadaCompletamente_then_retornaTrue() {
            obligacion.setMontoPagado(1000.0);

            boolean estaPagada = obligacion.estaPagadaCompletamente();

            assertTrue(estaPagada);
        }

        @Test
        @DisplayName("given_obligacionConPagoExcedido_when_estaPagadaCompletamente_then_retornaTrue")
        void given_obligacionConPagoExcedido_when_estaPagadaCompletamente_then_retornaTrue() {
            obligacion.setMontoPagado(1500.0);

            boolean estaPagada = obligacion.estaPagadaCompletamente();

            assertTrue(estaPagada);
        }
    }

    @Nested
    @DisplayName("puedeRecibirPago()")
    class PuedeRecibirPagoTests {

        @Test
        @DisplayName("given_montoValidoYSaldoPendiente_when_puedeRecibirPago_then_retornaTrue")
        void given_montoValidoYSaldoPendiente_when_puedeRecibirPago_then_retornaTrue() {
            double montoAPagar = 100.0;

            boolean puedeRecibir = obligacion.puedeRecibirPago(montoAPagar);

            assertTrue(puedeRecibir);
        }

        @Test
        @DisplayName("given_montoCeroONegativo_when_puedeRecibirPago_then_retornaFalse")
        void given_montoCeroONegativo_when_puedeRecibirPago_then_retornaFalse() {
            assertFalse(obligacion.puedeRecibirPago(0.0));
            assertFalse(obligacion.puedeRecibirPago(-50.0));
        }

        @Test
        @DisplayName("given_obligacionTotalmentePagada_when_puedeRecibirPago_then_retornaFalse")
        void given_obligacionTotalmentePagada_when_puedeRecibirPago_then_retornaFalse() {
            obligacion.setMontoPagado(1000.0);
            double montoAPagar = 100.0;

            boolean puedeRecibir = obligacion.puedeRecibirPago(montoAPagar);

            assertFalse(puedeRecibir);
        }
    }

    @Nested
    @DisplayName("estaVencida()")
    class EstaVencidaTests {

        @Test
        @DisplayName("given_fechaFuturaYEstadoPendiente_when_estaVencida_then_retornaFalse")
        void given_fechaFuturaYEstadoPendiente_when_estaVencida_then_retornaFalse() {
            obligacion.setFechaPago(LocalDate.now().plusDays(5));
            obligacion.setEstado(EstadoObligacionFinanciera.PENDIENTE);

            boolean vencida = obligacion.estaVencida();

            assertFalse(vencida);
        }

        @Test
        @DisplayName("given_fechaPasadaYEstadoPendiente_when_estaVencida_then_retornaTrue")
        void given_fechaPasadaYEstadoPendiente_when_estaVencida_then_retornaTrue() {
            obligacion.setFechaPago(LocalDate.now().minusDays(5));
            obligacion.setEstado(EstadoObligacionFinanciera.PENDIENTE);

            boolean vencida = obligacion.estaVencida();

            assertTrue(vencida);
        }

        @Test
        @DisplayName("given_fechaPasadaYEstadoPagada_when_estaVencida_then_retornaFalse")
        void given_fechaPasadaYEstadoPagada_when_estaVencida_then_retornaFalse() {
            obligacion.setFechaPago(LocalDate.now().minusDays(5));
            obligacion.setEstado(EstadoObligacionFinanciera.PAGADA);

            boolean vencida = obligacion.estaVencida();

            assertFalse(vencida);
        }
    }
}