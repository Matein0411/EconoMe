package com.EconoMe.movimientos;

import com.EconoMe.cuentas.dao.DAOCuenta;
import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.cuentas.modelos.TipoCuenta;
import com.EconoMe.cuentas.servicios.ServicioCuenta;
import com.EconoMe.movimientos.dao.DAOMovimiento;
import com.EconoMe.movimientos.modelos.*;
import com.EconoMe.movimientos.servicios.ServicioMovimiento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioMovimientoTest {

    @Mock
    private DAOMovimiento daoMovimiento;

    @Mock
    private DAOCuenta daoCuenta;

    private ServicioCuenta servicioCuenta;
    private ServicioMovimiento servicioMovimiento;

    @BeforeEach
    void setUp() {
        // Given: Un entorno con servicioCuenta real y DAOs mockeados
        servicioCuenta = new ServicioCuenta(daoCuenta);
        servicioMovimiento = new ServicioMovimiento(daoMovimiento, servicioCuenta);
    }

    // =========================================================================
    // BLOQUE 1: REGISTRO DE INGRESOS
    // =========================================================================

    @Nested
    @DisplayName("Pruebas: Registro de Ingresos")
    class RegistroIngresosTest {

        @Test
        @DisplayName("Ingreso válido aumenta saldo")
        void givenValidIncome_whenRegisteringIngreso_thenIngresoCreatedAndBalanceUpdated() {
            // Given
            Long cuentaId = 1L;
            double montoInicial = 100.0;
            double montoIngreso = 50.0;
            Cuenta cuenta = new Cuenta();
            cuenta.setId(cuentaId);
            cuenta.setMonto(montoInicial);

            when(daoCuenta.buscarPorId(cuentaId)).thenReturn(cuenta);

            // When
            servicioMovimiento.registrarIngreso(cuentaId, montoIngreso, "Salario", CategoriaIngreso.SALARIO);

            // Then
            verify(daoMovimiento).crear(any(Ingreso.class));
            verify(daoCuenta).actualizar(argThat(c -> c.getMonto() == 150.0));
        }

        @Test
        @DisplayName("Ingreso con monto negativo lanza excepción")
        void givenNegativeAmount_whenRegisteringIngreso_thenThrowException() {
            // Given
            Long cuentaId = 1L;
            double montoNegativo = -50.0;

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> servicioMovimiento.registrarIngreso(cuentaId, montoNegativo, "Test", CategoriaIngreso.OTROS)
            );
            assertEquals("El monto debe ser mayor a 0", exception.getMessage());
            verify(daoMovimiento, never()).crear(any());
        }

        @Test
        @DisplayName("Ingreso con categoría nula lanza excepción")
        void givenNullCategory_whenRegisteringIngreso_thenThrowException() {
            // Given
            Long cuentaId = 1L;

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> servicioMovimiento.registrarIngreso(cuentaId, 100.0, "Test", null)
            );
            assertTrue(exception.getMessage().contains("categoría de ingreso"));
        }

        @Test
        @DisplayName("Ingreso a cuenta inexistente lanza excepción")
        void givenNonExistentAccount_whenRegisteringIngreso_thenThrowException() {
            // Given
            Long cuentaId = 999L;
            when(daoCuenta.buscarPorId(cuentaId)).thenReturn(null);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> servicioMovimiento.registrarIngreso(cuentaId, 100.0, "Desc", CategoriaIngreso.SALARIO)
            );
            assertEquals("No se encontró la cuenta con ID 999", exception.getMessage());
        }
    }

    // =========================================================================
    // BLOQUE 2: REGISTRO DE GASTOS
    // =========================================================================

    @Nested
    @DisplayName("Pruebas: Registro de Gastos")
    class RegistroGastosTest {

        @Test
        @DisplayName("Gasto válido reduce saldo")
        void givenValidExpense_whenRegisteringGasto_thenGastoCreatedAndBalanceReduced() {
            // Given
            Long cuentaId = 1L;
            double montoInicial = 100.0;
            double montoGasto = 30.0;
            Cuenta cuenta = new Cuenta();
            cuenta.setId(cuentaId);
            cuenta.setMonto(montoInicial);

            when(daoCuenta.obtenerMonto(cuentaId)).thenReturn(montoInicial);
            when(daoCuenta.buscarPorId(cuentaId)).thenReturn(cuenta);

            // When
            servicioMovimiento.registrarGasto(cuentaId, montoGasto, "Super", CategoriaGasto.ALIMENTACION);

            // Then
            verify(daoMovimiento).crear(any(Gasto.class));
            verify(daoCuenta).actualizar(argThat(c -> c.getMonto() == 70.0));
        }

        @Test
        @DisplayName("Gasto con saldo insuficiente lanza excepción")
        void givenInsufficientBalance_whenRegisteringGasto_thenThrowException() {
            // Given
            Long cuentaId = 1L;
            double saldoActual = 50.0;
            double montoGasto = 100.0;
            when(daoCuenta.obtenerMonto(cuentaId)).thenReturn(saldoActual);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> servicioMovimiento.registrarGasto(cuentaId, montoGasto, "Caro", CategoriaGasto.SERVICIOS)
            );
            assertTrue(exception.getMessage().contains("Saldo insuficiente"));
            verify(daoMovimiento, never()).crear(any());
        }

        @Test
        @DisplayName("Gasto con monto cero lanza excepción")
        void givenZeroAmount_whenRegisteringGasto_thenThrowException() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> servicioMovimiento.registrarGasto(1L, 0.0, "Test", CategoriaGasto.TRANSPORTE)
            );
            assertEquals("El monto debe ser mayor a 0", exception.getMessage());
        }

        @Test
        @DisplayName("Gasto con categoría nula lanza excepción")
        void givenNullCategory_whenRegisteringGasto_thenThrowException() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> servicioMovimiento.registrarGasto(1L, 50.0, "Test", null)
            );
            assertTrue(exception.getMessage().contains("categoría de gasto"));
        }
    }

    // =========================================================================
    // BLOQUE 3: CÁLCULOS Y DELEGACIONES BÁSICAS
    // =========================================================================

    @Nested
    @DisplayName("Pruebas: Cálculos y Delegaciones")
    class CalculosDelegacionesTest {

        @Test
        @DisplayName("Calcular balance resta ingresos y gastos")
        void givenAccountWithMovements_whenCalculatingBalance_thenReturnsRoundedDifference() {
            // Given
            Long cuentaId = 1L;
            when(daoMovimiento.sumIngresosPorCuenta(cuentaId)).thenReturn(1000.50);
            when(daoMovimiento.sumGastosPorCuenta(cuentaId)).thenReturn(250.20);

            // When
            double balance = servicioMovimiento.calcularBalancePorCuenta(cuentaId);

            // Then (1000.50 - 250.20 = 750.30)
            assertEquals(750.30, balance, 0.001);
        }

        @Test
        @DisplayName("Contar movimientos delega al DAO")
        void givenAccount_whenCountingMovements_thenDelegatesToDao() {
            // Given
            when(daoMovimiento.contarMovimientos(1L)).thenReturn(10L);

            // When & Then
            assertEquals(10L, servicioMovimiento.contarMovimientos(1L));
            verify(daoMovimiento).contarMovimientos(1L);
        }

        @Test
        @DisplayName("Sumar ingresos delega al DAO")
        void givenAccount_whenSummingIngresos_thenDelegatesToDao() {
            // Given
            when(daoMovimiento.sumIngresosPorCuenta(1L)).thenReturn(500.0);

            // When & Then
            assertEquals(500.0, servicioMovimiento.sumarIngresosPorCuenta(1L));
        }

        @Test
        @DisplayName("Sumar gastos delega al DAO")
        void givenAccount_whenSummingGastos_thenDelegatesToDao() {
            // Given
            when(daoMovimiento.sumGastosPorCuenta(1L)).thenReturn(200.0);

            // When & Then
            assertEquals(200.0, servicioMovimiento.sumarGastosPorCuenta(1L));
        }

        @Test
        @DisplayName("Obtener ingresos delega al DAO")
        void givenAccount_whenGettingIngresos_thenDelegatesToDao() {
            // Given
            List<Ingreso> lista = List.of(new Ingreso());
            when(daoMovimiento.buscarIngresosPorCuenta(1L)).thenReturn(lista);

            // When & Then
            assertEquals(1, servicioMovimiento.obtenerIngresosPorCuenta(1L).size());
        }

        @Test
        @DisplayName("Obtener gastos delega al DAO")
        void givenAccount_whenGettingGastos_thenDelegatesToDao() {
            // Given
            List<Gasto> lista = List.of(new Gasto());
            when(daoMovimiento.buscarGastosPorCuenta(1L)).thenReturn(lista);

            // When & Then
            assertEquals(1, servicioMovimiento.obtenerGastosPorCuenta(1L).size());
        }

        @Test
        @DisplayName("Obtener movimientos delega al DAO")
        void givenAccount_whenGettingMovimientos_thenDelegatesToDao() {
            // Given
            List<Movimiento> lista = List.of(new Gasto());
            when(daoMovimiento.buscarPorCuenta(1L)).thenReturn(lista);

            // When & Then
            assertEquals(1, servicioMovimiento.obtenerMovimientosPorCuenta(1L).size());
        }
    }

    // =========================================================================
    // BLOQUE 4: BÚSQUEDA CON FILTROS (LOGICA DE NEGOCIO)
    // =========================================================================

    @Nested
    @DisplayName("Pruebas: Búsqueda con Filtros")
    class BusquedaFiltrosTest {

        @Test
        @DisplayName("ID de cuenta nulo lanza excepción")
        void givenNullAccountId_whenSearchingWithFilters_thenThrowException() {
            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> servicioMovimiento.buscarConFiltros(null, "tipo", "cat", null, null));
        }

        @Test
        @DisplayName("Fecha inicio posterior a fin lanza excepción")
        void givenStartDateAfterEndDate_whenSearchingWithFilters_thenThrowException() {
            // Given
            Instant inicio = Instant.parse("2024-01-02T00:00:00Z");
            Instant fin = Instant.parse("2024-01-01T00:00:00Z");

            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> servicioMovimiento.buscarConFiltros(1L, "tipo", "cat", inicio, fin));
        }

        @Test
        @DisplayName("Filtros vacíos delegan a búsqueda simple")
        void givenEmptyFilters_whenSearching_thenDelegatesToSimpleSearch() {
            // Given
            Long cuentaId = 1L;
            List<Movimiento> mockList = List.of(new Ingreso());
            when(daoMovimiento.buscarPorCuenta(cuentaId)).thenReturn(mockList);

            // When
            List<Movimiento> result = servicioMovimiento.buscarConFiltros(cuentaId, null, "", null, null);

            // Then
            assertEquals(1, result.size());
            verify(daoMovimiento).buscarPorCuenta(cuentaId);
            verify(daoMovimiento, never()).buscarConFiltros(anyLong(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Filtros válidos delegan a búsqueda filtrada")
        void givenValidFilters_whenSearching_thenDelegatesToFilteredSearch() {
            // Given
            Long cuentaId = 1L;
            Instant now = Instant.now();

            // When
            servicioMovimiento.buscarConFiltros(cuentaId, "Ingreso", "Salario", now, now);

            // Then
            verify(daoMovimiento).buscarConFiltros(cuentaId, "Ingreso", "Salario", now, now);
        }
    }

    // =========================================================================
    // BLOQUE 5: PAGINACIÓN
    // =========================================================================

    @Nested
    @DisplayName("Pruebas: Paginación")
    class PaginacionTest {

        @Test
        @DisplayName("Parámetros de paginación inválidos lanzan excepción")
        void givenInvalidPaginationParams_whenPaginating_thenThrowException() {
            // When & Then - ID Cuenta nulo
            assertThrows(IllegalArgumentException.class,
                    () -> servicioMovimiento.obtenerMovimientosPaginados(null, 1, 10));

            // When & Then - Página cero
            assertThrows(IllegalArgumentException.class,
                    () -> servicioMovimiento.obtenerMovimientosPaginados(1L, 0, 10));

            // When & Then - Tamaño cero
            assertThrows(IllegalArgumentException.class,
                    () -> servicioMovimiento.obtenerMovimientosPaginados(1L, 1, 0));

            // When & Then - Tamaño excesivo
            assertThrows(IllegalArgumentException.class,
                    () -> servicioMovimiento.obtenerMovimientosPaginados(1L, 1, 101));
        }

        @Test
        @DisplayName("Paginación correcta devuelve lista")
        void givenValidParams_whenPaginating_thenReturnsPage() {
            // Given
            Long cuentaId = 1L;
            when(daoMovimiento.contarMovimientosPorCuenta(cuentaId)).thenReturn(20L);

            // When
            servicioMovimiento.obtenerMovimientosPaginados(cuentaId, 1, 10);

            // Then
            verify(daoMovimiento).buscarPorCuentaPaginado(cuentaId, 1, 10);
        }

        @Test
        @DisplayName("Página mayor al total se ajusta a la última")
        void givenPageGreaterThanTotal_whenPaginating_thenReturnsLastPage() {
            // Given
            Long cuentaId = 1L;
            // 25 items, tamaño 10 -> Total páginas = 3
            when(daoMovimiento.contarMovimientosPorCuenta(cuentaId)).thenReturn(25L);

            // When: Pedimos página 10
            servicioMovimiento.obtenerMovimientosPaginados(cuentaId, 10, 10);

            // Then: Verifica que se llamó con la página corregida (3)
            verify(daoMovimiento).buscarPorCuentaPaginado(cuentaId, 3, 10);
        }

        @Test
        @DisplayName("Sin movimientos retorna lista vacía")
        void givenZeroMovements_whenPaginating_thenReturnsEmptyList() {
            // Given
            Long cuentaId = 1L;
            when(daoMovimiento.contarMovimientosPorCuenta(cuentaId)).thenReturn(0L);

            // When
            List<Movimiento> res = servicioMovimiento.obtenerMovimientosPaginados(cuentaId, 1, 10);

            // Then
            assertTrue(res.isEmpty());
            verify(daoMovimiento, never()).buscarPorCuentaPaginado(anyLong(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("Cálculo de páginas total correcto")
        void givenTotalMovements_whenCalculatingPages_thenReturnsCorrectMath() {
            // Given
            Long cuentaId = 1L;
            when(daoMovimiento.contarMovimientosPorCuenta(cuentaId)).thenReturn(21L);

            // When (21 items, grupos de 10 -> 2.1 -> techo 3)
            int paginas = servicioMovimiento.calcularTotalPaginas(cuentaId, 10);

            // Then
            assertEquals(3, paginas);
        }

        @Test
        @DisplayName("Obtener total de movimientos delega al DAO")
        void givenAccount_whenGettingTotalMovements_thenDelegatesToDao() {
            // Given
            when(daoMovimiento.contarMovimientosPorCuenta(1L)).thenReturn(55L);

            // When & Then
            assertEquals(55L, servicioMovimiento.obtenerTotalMovimientos(1L));
        }
    }

    // =========================================================================
    // BLOQUE 6: PARSEO DE FECHAS Y OFFSETS
    // =========================================================================

    @Nested
    @DisplayName("Pruebas: Parseo de Fechas (Strings)")
    class ParseoFechasTest {

        @Test
        @DisplayName("Filtros String parsean fechas y calculan offset correctamente")
        void givenStringDatesAndPagination_whenListingWithFilters_thenParsesDatesAndCalculatesOffset() {
            // Given
            Long cuentaId = 1L;
            String fechaDesde = "2024-01-01";
            String fechaHasta = "2024-01-31";
            int pagina = 2;
            int tamanio = 10;
            // Esperamos offset = (2-1)*10 = 10

            // When
            servicioMovimiento.listarMovimientosConFiltros(cuentaId, "Gasto", "Comida", fechaDesde, fechaHasta, pagina, tamanio);

            // Then
            ArgumentCaptor<Instant> inicioCaptor = ArgumentCaptor.forClass(Instant.class);
            ArgumentCaptor<Instant> finCaptor = ArgumentCaptor.forClass(Instant.class);
            ArgumentCaptor<Integer> offsetCaptor = ArgumentCaptor.forClass(Integer.class);

            verify(daoMovimiento).buscarConFiltros(
                    eq(cuentaId),
                    eq("Gasto"),
                    eq("Comida"),
                    inicioCaptor.capture(),
                    finCaptor.capture(),
                    eq(tamanio),
                    offsetCaptor.capture()
            );

            assertEquals("2024-01-01T00:00:00Z", inicioCaptor.getValue().toString());
            assertEquals("2024-01-31T23:59:59Z", finCaptor.getValue().toString());
            assertEquals(10, offsetCaptor.getValue());
        }

        @Test
        @DisplayName("Filtros String nulos se pasan como null al DAO")
        void givenNullFilters_whenListingWithFilters_thenPassesNullsToDao() {
            // When
            servicioMovimiento.listarMovimientosConFiltros(1L, null, null, null, null, 1, 10);

            // Then
            verify(daoMovimiento).buscarConFiltros(1L, null, null, null, null, 10, 0);
        }

        @Test
        @DisplayName("Total de páginas con filtros calcula ceil correctamente")
        void givenFilteredCount_whenGettingTotalPages_thenReturnsCeiledValue() {
            // Given
            Long cuentaId = 1L;
            when(daoMovimiento.contarConFiltros(anyLong(), any(), any(), any(), any())).thenReturn(25L);

            // When
            int paginas = servicioMovimiento.obtenerTotalPaginasConFiltros(cuentaId, null, null, null, null, 10);

            // Then (25 / 10 = 2.5 -> ceil = 3)
            assertEquals(3, paginas);
        }
    }
}