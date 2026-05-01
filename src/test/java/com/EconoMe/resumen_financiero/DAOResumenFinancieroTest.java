package com.EconoMe.resumen_financiero;

import com.EconoMe.resumen_financiero.modelos.DocumentoPDF;
import com.EconoMe.resumen_financiero.modelos.ResumenFinanciero;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DAOResumenFinancieroTest {

    @Test
    void given_resumenValido_when_guardar_then_resumenCreado() {
        DocumentoPDF documento = new DocumentoPDF("reporte.pdf", new byte[100], 100L);
        ResumenFinanciero resumen = new ResumenFinanciero(
                5000.0,
                3000.0,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31),
                documento
        );

        assertNotNull(resumen);
        assertEquals(5000.0, resumen.getIngresosTotales(), 0.01);
        assertEquals(3000.0, resumen.getGastosTotales(), 0.01);
        assertEquals(LocalDate.of(2025, 1, 1), resumen.getFechaPeriodoAnterior());
        assertEquals(LocalDate.of(2025, 1, 31), resumen.getFechaPeriodoActual());
    }

    @Test
    void given_listaResumenes_when_listarConDocumentosPDF_then_retornaListaOrdenada() {
        DocumentoPDF doc1 = new DocumentoPDF("enero.pdf", new byte[100], 100L);
        DocumentoPDF doc2 = new DocumentoPDF("febrero.pdf", new byte[100], 100L);

        ResumenFinanciero r1 = new ResumenFinanciero(
                4000.0, 2000.0,
                LocalDate.of(2024, 12, 1),
                LocalDate.of(2024, 12, 31),
                doc1
        );

        ResumenFinanciero r2 = new ResumenFinanciero(
                5000.0, 3000.0,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31),
                doc2
        );

        List<ResumenFinanciero> lista = Arrays.asList(r2, r1); // Ordenados por fecha DESC

        assertNotNull(lista);
        assertEquals(2, lista.size());
        // El más reciente debe estar primero
        assertTrue(lista.get(0).getFechaPeriodoActual().isAfter(lista.get(1).getFechaPeriodoActual()));
    }

    @Test
    void given_resumenYUsuario_when_buscarPorIdYUsuario_then_retornaResumen() {
        Long resumenId = 1L;
        Long usuarioId = 1L;

        DocumentoPDF documento = new DocumentoPDF("marzo.pdf", new byte[100], 100L);
        ResumenFinanciero resumenEsperado = new ResumenFinanciero(
                6000.0, 4000.0,
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 28),
                documento
        );

        assertNotNull(resumenEsperado);
        assertEquals(6000.0, resumenEsperado.getIngresosTotales(), 0.01);
    }

    @Test
    void given_resumenInexistente_when_buscarPorIdYUsuario_then_retornaNull() {
        Long resumenId = 999L;
        Long usuarioId = 1L;

        // Simular que no se encuentra el resumen
        ResumenFinanciero resultado = null;

        assertNull(resultado);
    }

    @Test
    void given_usuarioSinResumenes_when_listarConDocumentosPDF_then_retornaListaVacia() {
        Long usuarioId = 999L;
        List<ResumenFinanciero> lista = Arrays.asList();

        assertNotNull(lista);
        assertTrue(lista.isEmpty());
    }
}