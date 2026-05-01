package com.EconoMe.recordatorios;

import com.EconoMe.recordatorios.dao.DAORecordatorio;
import com.EconoMe.recordatorios.modelos.Recordatorio;
import com.EconoMe.recordatorios.modelos.Recurrencia;
import com.EconoMe.recordatorios.servicios.ServicioRecordatorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicioRecordatorioTest {

    @Mock
    private DAORecordatorio recordatorioDAO;

    @InjectMocks
    private ServicioRecordatorio servicioRecordatorio;

    private Recordatorio recordatorio;

    @BeforeEach
    void setUp() {
        // Configuración manual del mock en el servicio
        servicioRecordatorio = new ServicioRecordatorio();
        // Usar reflection para inyectar el mock
        try {
            java.lang.reflect.Field field = ServicioRecordatorio.class.getDeclaredField("recordatorioDAO");
            field.setAccessible(true);
            field.set(servicioRecordatorio, recordatorioDAO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        recordatorio = new Recordatorio(
                LocalDate.now().plusDays(5),
                LocalDate.now().plusMonths(1),
                "Pago de renta",
                Recurrencia.MENSUAL,
                500.0,
                5
        );
        recordatorio.setId(1L);
    }

    // ==================== TESTS DE crearRecordatorio ====================

    @Test
    void given_servicioRecordatorio_when_create_then_ok() {
        // Arrange
        doNothing().when(recordatorioDAO).crear(recordatorio);

        // Act
        servicioRecordatorio.crearRecordatorio(recordatorio);

        // Assert
        verify(recordatorioDAO, times(1)).crear(recordatorio);
    }

    @Test
    void given_recordatorioDAO_when_exception_then_fail() {
        // Arrange
        doThrow(new RuntimeException("Error de BD")).when(recordatorioDAO).crear(any());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            servicioRecordatorio.crearRecordatorio(recordatorio);
        });
    }

    // ==================== TESTS DE actualizarRecordatorio ====================

    @Test
    void given_servicioRecordatorio_when_update_then_ok() {
        // Arrange
        recordatorio.setDescripcion("Pago de servicios");
        doNothing().when(recordatorioDAO).actualizar(recordatorio);

        // Act
        servicioRecordatorio.actualizarRecordatorio(recordatorio);

        // Assert
        verify(recordatorioDAO, times(1)).actualizar(recordatorio);
    }

    // ==================== TESTS DE eliminarRecordatorio ====================

    @Test
    void given_servicioRecordatorio_when_delete_then_ok() {
        // Arrange
        Long id = 1L;
        doNothing().when(recordatorioDAO).borrar(id);

        // Act
        servicioRecordatorio.eliminarRecordatorio(id);

        // Assert
        verify(recordatorioDAO, times(1)).borrar(id);
    }

    @Test
    void given_null_id_when_delete_then_fail() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioRecordatorio.eliminarRecordatorio(null)
        );

        assertEquals("El ID del recordatorio es obligatorio", exception.getMessage());
        verify(recordatorioDAO, never()).borrar(any());
    }

    // ==================== TESTS DE listarActivos ====================

    @Test
    void given_recordatorios_when_listar_then_ok() {
        // Arrange
        List<Recordatorio> recordatoriosEsperados = getRecordatorios();
        when(recordatorioDAO.listarActivos()).thenReturn(recordatoriosEsperados);

        // Act
        List<Recordatorio> resultado = servicioRecordatorio.listarActivos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(recordatoriosEsperados, resultado);
        verify(recordatorioDAO, times(1)).listarActivos();
    }

    private static List<Recordatorio> getRecordatorios() {
        Recordatorio rec1 = new Recordatorio(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                "Recordatorio 1",
                Recurrencia.MENSUAL,
                100.0,
                3
        );

        Recordatorio rec2 = new Recordatorio(
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 12, 31),
                "Recordatorio 2",
                Recurrencia.SEMANAL,
                200.0,
                2
        );

        return Arrays.asList(rec1, rec2);
    }

    @Test
    void given_empty_recordatorios_when_listar_then_ok() {
        // Arrange
        when(recordatorioDAO.listarActivos()).thenReturn(List.of());

        // Act
        List<Recordatorio> resultado = servicioRecordatorio.listarActivos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(recordatorioDAO, times(1)).listarActivos();
    }

    // ==================== TESTS DE buscarPorId ====================

    @Test
    void given_recordatorio_when_buscar_then_ok() {
        // Arrange
        Long id = 1L;
        when(recordatorioDAO.buscarPorId(id)).thenReturn(recordatorio);

        // Act
        Recordatorio resultado = servicioRecordatorio.buscarPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(recordatorio.getId(), resultado.getId());
        assertEquals(recordatorio.getDescripcion(), resultado.getDescripcion());
        verify(recordatorioDAO, times(1)).buscarPorId(id);
    }

    @Test
    void not_given_recordatorio_when_getById_then_null() {
        // Arrange
        Long id = 999L;
        when(recordatorioDAO.buscarPorId(id)).thenReturn(null);

        // Act
        Recordatorio resultado = servicioRecordatorio.buscarPorId(id);

        // Assert
        assertNull(resultado);
        verify(recordatorioDAO, times(1)).buscarPorId(id);
    }

    @Test
    void given_null_startDate_when_validarFechas_then_throw_exception() {
        recordatorio.setFechaInicio(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioRecordatorio.validarFechas(recordatorio)
        );

        assertEquals("La fecha de inicio es obligatoria", exception.getMessage());
    }

    @Test
    void given_past_startDate_when_validarFechas_then_throw_exception() {
        recordatorio.setFechaInicio(LocalDate.now().minusDays(1)); // Ayer

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioRecordatorio.validarFechas(recordatorio)
        );

        assertEquals("La fecha de inicio no puede ser anterior a la fecha actual", exception.getMessage());
    }

    @Test
    void given_endDate_before_startDate_when_validarFechas_then_throw_exception() {
        recordatorio.setFechaInicio(LocalDate.now().plusDays(10)); // Empieza en 10 días
        recordatorio.setFechaFin(LocalDate.now().plusDays(5));     // Termina en 5 días (Error)

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioRecordatorio.validarFechas(recordatorio)
        );

        assertEquals("La fecha final no puede ser anterior a la fecha de inicio", exception.getMessage());
    }

    @Test
    void given_valid_dates_when_validarFechas_then_success() {
        recordatorio.setFechaInicio(LocalDate.now().plusDays(1));
        recordatorio.setFechaFin(LocalDate.now().plusDays(5));

        assertDoesNotThrow(() -> servicioRecordatorio.validarFechas(recordatorio));
    }
}