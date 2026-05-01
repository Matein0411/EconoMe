package com.EconoMe.resumen_financiero;

import com.EconoMe.resumen_financiero.modelos.DocumentoPDF;
import com.EconoMe.resumen_financiero.modelos.ResumenFinanciero;
import com.EconoMe.resumen_financiero.servicios.ServicioResumenFinanciero;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ServicioResumenFinancieroTest {

    @Test
    void given_patronYTextoConMonto_when_extraerMonto_then_retornaMonto() {
        String patron = "DEPÓSITO / CRÉDITOS\\s*\\(\\d+\\)\\s+(\\d+\\.\\d+)";
        String textoPDF = "DEPÓSITO / CRÉDITOS (5) 12345.67";

        Double monto = ServicioResumenFinanciero.extraerMonto(patron, textoPDF);

        assertNotNull(monto);
        assertEquals(12345.67, monto, 0.01);
    }

    @Test
    void given_patronYTextoSinMonto_when_extraerMonto_then_retornaNull() {
        String patron = "DEPÓSITO / CRÉDITOS\\s*\\(\\d+\\)\\s+(\\d+\\.\\d+)";
        String textoPDF = "Texto sin el patrón esperado";

        Double monto = ServicioResumenFinanciero.extraerMonto(patron, textoPDF);

        assertNull(monto);
    }

    @Test
    void given_montoInvalido_when_extraerMonto_then_retornaNull() {
        String patron = "DEPÓSITO / CRÉDITOS\\s*\\(\\d+\\)\\s+(.+)";
        String textoPDF = "DEPÓSITO / CRÉDITOS (5) INVALIDO";

        Double monto = ServicioResumenFinanciero.extraerMonto(patron, textoPDF);

        assertNull(monto);
    }

    @Test
    void given_patronYTextoConFecha_when_extraerFecha_then_retornaFecha() {
        String patron = "FECHA ÚLTIMO CORTE\\s*\\(FACTURA\\)\\s*(\\d{2}-\\d{2}-\\d{4})";
        String textoPDF = "FECHA ÚLTIMO CORTE (FACTURA) 15-01-2025";

        LocalDate fecha = ServicioResumenFinanciero.extraerFecha(patron, textoPDF);

        assertNotNull(fecha);
        assertEquals(LocalDate.of(2025, 1, 15), fecha);
    }

    @Test
    void given_patronYTextoSinFecha_when_extraerFecha_then_retornaNull() {
        String patron = "FECHA ÚLTIMO CORTE\\s*\\(FACTURA\\)\\s*(\\d{2}-\\d{2}-\\d{4})";
        String textoPDF = "Texto sin fecha";

        LocalDate fecha = ServicioResumenFinanciero.extraerFecha(patron, textoPDF);

        assertNull(fecha);
    }

    @Test
    void given_fechaInvalida_when_extraerFecha_then_retornaNull() {
        String patron = "FECHA\\s*(.+)";
        String textoPDF = "FECHA 99-99-9999";

        LocalDate fecha = ServicioResumenFinanciero.extraerFecha(patron, textoPDF);

        assertNull(fecha);
    }

    @Test
    void given_textoPDFCompleto_when_procesarInformacion_then_creaResumenCorrectamente() {
        // Este test valida la lógica sin ejecutar la extracción real de PDF
        String textoPDF = """
            DEPÓSITO / CRÉDITOS (10) 5000.50
            CHEQUES / DÉBITOS (8) 3000.25
            FECHA ÚLTIMO CORTE (FACTURA) 01-01-2025
            FECHA ESTE CORTE (FACTURA) 31-01-2025
            """;

        // Validar que los patrones funcionan
        String patronIngresos = "DEPÓSITO / CRÉDITOS\\s*\\(\\d+\\)\\s+(\\d+\\.\\d+)";
        String patronGastos = "CHEQUES / DÉBITOS\\s*\\(\\d+\\)\\s+(\\d+\\.\\d+)";

        Double ingresos = ServicioResumenFinanciero.extraerMonto(patronIngresos, textoPDF);
        Double gastos = ServicioResumenFinanciero.extraerMonto(patronGastos, textoPDF);

        assertNotNull(ingresos);
        assertNotNull(gastos);
        assertEquals(5000.50, ingresos, 0.01);
        assertEquals(3000.25, gastos, 0.01);
    }

    @Test
    void given_textoPDFIncompleto_when_procesarInformacion_then_retornaNull() {
        String textoPDFIncompleto = "DEPÓSITO / CRÉDITOS (10) 5000.50";
        // Falta información de gastos y fechas

        String patronGastos = "CHEQUES / DÉBITOS\\s*\\(\\d+\\)\\s+(\\d+\\.\\d+)";
        Double gastos = ServicioResumenFinanciero.extraerMonto(patronGastos, textoPDFIncompleto);

        assertNull(gastos);
    }

    @Test
    void given_montosYFechas_when_crearResumen_then_resumenEsValido() {
        DocumentoPDF documento = new DocumentoPDF("test.pdf", new byte[100], 100L);

        ResumenFinanciero resumen = new ResumenFinanciero(
                5000.50,
                3000.25,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31),
                documento
        );

        assertNotNull(resumen);
        assertEquals(5000.50, resumen.getIngresosTotales(), 0.01);
        assertEquals(3000.25, resumen.getGastosTotales(), 0.01);
        assertEquals(LocalDate.of(2025, 1, 1), resumen.getFechaPeriodoAnterior());
        assertEquals(LocalDate.of(2025, 1, 31), resumen.getFechaPeriodoActual());
        assertNotNull(resumen.getDocumentoPDF());
    }
}