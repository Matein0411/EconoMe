package com.EconoMe.recordatorios;

import com.EconoMe.recordatorios.modelos.Recordatorio;
import com.EconoMe.recordatorios.modelos.Recurrencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RecordatorioTest {

    private Recordatorio recordatorio;

    @BeforeEach
    void setUp() {
        recordatorio = new Recordatorio();
    }

    // ==================== TESTS DE calcularProximaFechaVencimiento ====================

    @Test
    void given_fechaInicio_fechaFin_and_recurrencia_diaria_when_calcularProximaFechaVencimiento_then_ok() {
        LocalDate fechaInicio = LocalDate.of(2026, 1, 1);
        LocalDate fechaFin = LocalDate.of(2026, 1, 10);

        LocalDate resultado = recordatorio.calcularProximaFechaVencimiento(
                Recurrencia.DIARIA, fechaInicio, fechaFin
        );

        assertEquals(fechaFin, resultado);
    }

    @Test
    void given_fechaInicio_fechaFin_and_recurrencia_semanal_when_calcularProximaFechaVencimiento_then_ok() {
        LocalDate fechaInicio = LocalDate.of(2025, 1, 1); // Miércoles
        LocalDate fechaFin = LocalDate.of(2025, 1, 20); // Lunes (19 días después)

        LocalDate resultado = recordatorio.calcularProximaFechaVencimiento(
                Recurrencia.SEMANAL, fechaInicio, fechaFin
        );

        // Debería ser el próximo miércoles después del 20
        assertEquals(LocalDate.of(2025, 1, 22), resultado);
    }

    @Test
    void given_fechaInicio_fechaFin_and_recurrencia_mensual_when_calcularProximaFechaVencimiento_then_ok() {
        LocalDate fechaInicio = LocalDate.of(2025, 1, 15);
        LocalDate fechaFin = LocalDate.of(2025, 3, 10);

        LocalDate resultado = recordatorio.calcularProximaFechaVencimiento(
                Recurrencia.MENSUAL, fechaInicio, fechaFin
        );

        assertEquals(LocalDate.of(2025, 3, 15), resultado);
    }

    @Test
    void given_fechaInicio_fechaFin_and_recurrencia_anual_when_calcularProximaFechaVencimiento_then_ok() {
        LocalDate fechaInicio = LocalDate.of(2025, 1, 15);
        LocalDate fechaFin = LocalDate.of(2027, 2, 1);

        LocalDate resultado = recordatorio.calcularProximaFechaVencimiento(
                Recurrencia.ANUAL, fechaInicio, fechaFin
        );

        assertEquals(LocalDate.of(2028, 1, 15), resultado);
    }

    @Test
    void given_fechaInicio_fechaFin_and_recurrencia_ninguna_when_calcularProximaFechaVencimiento_then_ok() {
        LocalDate fechaInicio = LocalDate.of(2025, 1, 15);
        LocalDate fechaFin = LocalDate.of(2025, 3, 1);

        // Recurrencia NINGUNA debe retornar la fecha de inicio
        LocalDate resultado = recordatorio.calcularProximaFechaVencimiento(
                Recurrencia.NINGUNA, fechaInicio, fechaFin
        );

        assertEquals(fechaInicio, resultado);
    }

    // ==================== TESTS DE obtenerFechaNotificable ====================

    @Test
    // Debe retornar fecha cuando está en el rango de notificación
    void given_fechaInicio_recurrencia_ninguna_and_dias_anticipacion_when_en_rango_de_avisos_then_notificar() {
        recordatorio.setFechaInicio(LocalDate.of(2025, 1, 15));
        recordatorio.setRecurrencia(Recurrencia.NINGUNA);
        recordatorio.setDiasDeAnticipacion(5);

        LocalDate hoy = LocalDate.of(2025, 1, 12); // 3 días antes del vencimiento

        Optional<LocalDate> resultado = recordatorio.obtenerFechaNotificable(hoy);

        assertTrue(resultado.isPresent());
        assertEquals(LocalDate.of(2025, 1, 15), resultado.get());
    }

    @Test
    void given_fechaInicio_recurrencia_ninguna_and_dias_anticipacion_when_no_en_rango_de_avisos_then_notificar() {
        recordatorio.setFechaInicio(LocalDate.of(2025, 1, 15));
        recordatorio.setRecurrencia(Recurrencia.NINGUNA);
        recordatorio.setDiasDeAnticipacion(5);

        LocalDate hoy = LocalDate.of(2025, 1, 8); // 7 días antes, fuera del rango

        Optional<LocalDate> resultado = recordatorio.obtenerFechaNotificable(hoy);

        assertFalse(resultado.isPresent());
    }

    @Test
    //Debe retornar vacío cuando ya pasó el vencimiento
    void given_fechaInicio_recurrencia_ninguna_and_dias_anticipacion_when_despues_de_rango_de_avisos_then_false() {
        recordatorio.setFechaInicio(LocalDate.of(2025, 1, 15));
        recordatorio.setRecurrencia(Recurrencia.NINGUNA);
        recordatorio.setDiasDeAnticipacion(5);

        LocalDate hoy = LocalDate.of(2025, 1, 17); // 2 días después del vencimiento

        Optional<LocalDate> resultado = recordatorio.obtenerFechaNotificable(hoy);

        assertFalse(resultado.isPresent());
    }

    @Test
    // Recurrencia DIARIA debe notificar todos los días desde inicio con anticipación 0
    void given_recurrencia_diaria_when_notificar_then_ok() {
        recordatorio.setFechaInicio(LocalDate.of(2025, 1, 1));
        recordatorio.setRecurrencia(Recurrencia.DIARIA);
        recordatorio.setDiasDeAnticipacion(3); // Se ignora para DIARIA después del inicio

        LocalDate hoy = LocalDate.of(2025, 1, 10);

        Optional<LocalDate> resultado = recordatorio.obtenerFechaNotificable(hoy);

        assertTrue(resultado.isPresent());
        assertEquals(hoy, resultado.get());
    }

    @Test
    // Recurrencia MENSUAL debe notificar correctamente con anticipación
    void given_recurrencia_mensual_when_notificar_then_ok() {
        recordatorio.setFechaInicio(LocalDate.of(2025, 1, 15));
        recordatorio.setRecurrencia(Recurrencia.MENSUAL);
        recordatorio.setDiasDeAnticipacion(7);

        LocalDate hoy = LocalDate.of(2025, 2, 10); // 5 días antes del 15

        Optional<LocalDate> resultado = recordatorio.obtenerFechaNotificable(hoy);

        assertTrue(resultado.isPresent());
        assertEquals(LocalDate.of(2025, 2, 15), resultado.get());
    }

    // ==================== TESTS DE sumarPeriodoDeTiempo ====================

    @Test
    // Debe sumar correctamente períodos de semanas
    void given_periodos_de_tiempo_when_sumarPeriodoDeTiempo_then_ok() {
        LocalDate fechaInicio = LocalDate.of(2025, 1, 1);
        LocalDate fechaFin = LocalDate.of(2025, 1, 20);

        LocalDate resultado = recordatorio.sumarPeriodoDeTiempo(
                fechaInicio, fechaFin, java.time.temporal.ChronoUnit.WEEKS
        );

        assertTrue(!resultado.isBefore(fechaFin));
    }

    @Test
    // Debe avanzar un período cuando la fecha calculada es anterior a fechaFin
    void given_periodos_de_tiempo_when_fechaCalculada_es_anterior_a_fechaFin_then_avanzar_un_periodo() {
        LocalDate fechaInicio = LocalDate.of(2025, 1, 1);
        LocalDate fechaFin = LocalDate.of(2025, 1, 3);

        LocalDate resultado = recordatorio.sumarPeriodoDeTiempo(
                fechaInicio, fechaFin, java.time.temporal.ChronoUnit.WEEKS
        );

        assertEquals(LocalDate.of(2025, 1, 8), resultado);
    }
}
