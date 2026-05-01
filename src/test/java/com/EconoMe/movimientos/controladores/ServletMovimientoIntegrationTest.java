package com.EconoMe.movimientos.controladores;

import com.EconoMe.cuentas.dao.DAOCuenta;
import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.cuentas.modelos.TipoCuenta;
import com.EconoMe.movimientos.modelos.CategoriaGasto;
import com.EconoMe.movimientos.modelos.CategoriaIngreso;
import com.EconoMe.movimientos.servicios.ServicioMovimiento;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ServletMovimientoIntegrationTest {

    private ServletMovimiento servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private DAOCuenta daoCuenta;

    @Mock
    private ServicioMovimiento servicioMovimiento;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        servlet = new ServletMovimiento();

        // Inyectar mocks usando reflection
        java.lang.reflect.Field daoCuentaField = ServletMovimiento.class.getDeclaredField("daoCuenta");
        daoCuentaField.setAccessible(true);
        daoCuentaField.set(servlet, daoCuenta);

        java.lang.reflect.Field servicioMovimientoField = ServletMovimiento.class.getDeclaredField("servicioMovimiento");
        servicioMovimientoField.setAccessible(true);
        servicioMovimientoField.set(servlet, servicioMovimiento);

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/EconoMe");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("GET / - Error al cargar formulario")
    void testMostrarFormularioMovimientoConError() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(daoCuenta.listar()).thenThrow(new RuntimeException("Error de base de datos"));

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/cuentas");
    }

    @Test
    @DisplayName("POST / - Registrar ingreso exitosamente")
    void testRegistrarIngresoExitoso() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn("1");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("500.50");
        when(request.getParameter("descripcion")).thenReturn("Salario mensual");
        when(request.getParameter("categoria")).thenReturn("SALARIO");
        when(request.getParameter("idLista")).thenReturn(null);

        servlet.doPost(request, response);

        verify(servicioMovimiento).registrarIngreso(eq(1L), eq(500.50), eq("Salario mensual"), eq(CategoriaIngreso.SALARIO));
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/cuentas/detalle?id=1");
    }

    @Test
    @DisplayName("POST / - Registrar ingreso con idLista (desde lista)")
    void testRegistrarIngresoDesdeListaExitoso() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn("1");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("300.00");
        when(request.getParameter("descripcion")).thenReturn("Freelance");
        when(request.getParameter("categoria")).thenReturn("OTROS");
        when(request.getParameter("idLista")).thenReturn("5");

        servlet.doPost(request, response);

        verify(servicioMovimiento).registrarIngreso(eq(1L), eq(300.0), eq("Freelance"), eq(CategoriaIngreso.OTROS));
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/listas/detalle?id=5");
    }

    // ===== PRUEBAS POST - REGISTRAR GASTO =====

    @Test
    @DisplayName("POST / - Registrar gasto exitosamente")
    void testRegistrarGastoExitoso() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn("1");
        when(request.getParameter("tipo")).thenReturn("GASTO");
        when(request.getParameter("monto")).thenReturn("150.00");
        when(request.getParameter("descripcion")).thenReturn("Compra supermercado");
        when(request.getParameter("categoria")).thenReturn("ALIMENTACION");
        when(request.getParameter("idLista")).thenReturn(null);

        servlet.doPost(request, response);

        verify(servicioMovimiento).registrarGasto(eq(1L), eq(150.0), eq("Compra supermercado"), eq(CategoriaGasto.ALIMENTACION));
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/cuentas/detalle?id=1");
    }

    @Test
    @DisplayName("POST / - Registrar gasto con idLista")
    void testRegistrarGastoDesdeListaExitoso() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn("2");
        when(request.getParameter("tipo")).thenReturn("GASTO");
        when(request.getParameter("monto")).thenReturn("80.00");
        when(request.getParameter("descripcion")).thenReturn("Gasolina");
        when(request.getParameter("categoria")).thenReturn("TRANSPORTE");
        when(request.getParameter("idLista")).thenReturn("3");

        servlet.doPost(request, response);

        verify(servicioMovimiento).registrarGasto(eq(2L), eq(80.0), eq("Gasolina"), eq(CategoriaGasto.TRANSPORTE));
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/listas/detalle?id=3");
    }

    @Test
    @DisplayName("POST / - CuentaId nulo con idLista")
    void testRegistrarMovimientoCuentaIdNuloConLista() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn(null);
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("100");
        when(request.getParameter("descripcion")).thenReturn("Test");
        when(request.getParameter("categoria")).thenReturn("SALARIO");
        when(request.getParameter("idLista")).thenReturn("7");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/listas/detalle?id=7");
    }

    @Test
    @DisplayName("POST / - Tipo vacío con idLista")
    void testRegistrarMovimientoTipoVacioConLista() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn("1");
        when(request.getParameter("tipo")).thenReturn("  ");
        when(request.getParameter("monto")).thenReturn("100");
        when(request.getParameter("descripcion")).thenReturn("Test");
        when(request.getParameter("categoria")).thenReturn("SALARIO");
        when(request.getParameter("idLista")).thenReturn("4");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/listas/detalle?id=4");
    }

    @Test
    @DisplayName("POST / - Monto vacío con idLista")
    void testRegistrarMovimientoMontoVacioConLista() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn("1");
        when(request.getParameter("tipo")).thenReturn("GASTO");
        when(request.getParameter("monto")).thenReturn("");
        when(request.getParameter("descripcion")).thenReturn("Test");
        when(request.getParameter("categoria")).thenReturn("ALIMENTACION");
        when(request.getParameter("idLista")).thenReturn("2");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/listas/detalle?id=2");
    }

    @Test
    @DisplayName("POST / - Descripción vacía con idLista")
    void testRegistrarMovimientoDescripcionVaciaConLista() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn("1");
        when(request.getParameter("tipo")).thenReturn("GASTO");
        when(request.getParameter("monto")).thenReturn("50");
        when(request.getParameter("descripcion")).thenReturn("  ");
        when(request.getParameter("categoria")).thenReturn("TRANSPORTE");
        when(request.getParameter("idLista")).thenReturn("9");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/listas/detalle?id=9");
    }

    @Test
    @DisplayName("POST / - Categoría vacía con idLista")
    void testRegistrarMovimientoCategoriaVaciaConLista() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn("1");
        when(request.getParameter("tipo")).thenReturn("GASTO");
        when(request.getParameter("monto")).thenReturn("75");
        when(request.getParameter("descripcion")).thenReturn("Compra");
        when(request.getParameter("categoria")).thenReturn("");
        when(request.getParameter("idLista")).thenReturn("6");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/listas/detalle?id=6");
    }

    @Test
    @DisplayName("POST / - Monto no numérico con idLista")
    void testRegistrarMovimientoMontoInvalidoConLista() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn("1");
        when(request.getParameter("tipo")).thenReturn("GASTO");
        when(request.getParameter("monto")).thenReturn("abc");
        when(request.getParameter("descripcion")).thenReturn("Test");
        when(request.getParameter("categoria")).thenReturn("ALIMENTACION");
        when(request.getParameter("idLista")).thenReturn("8");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/listas/detalle?id=8");
    }

    @Test
    @DisplayName("POST / - Saldo insuficiente con idLista")
    void testRegistrarGastoSaldoInsuficienteConLista() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn("1");
        when(request.getParameter("tipo")).thenReturn("GASTO");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getParameter("descripcion")).thenReturn("Compra grande");
        when(request.getParameter("categoria")).thenReturn("ALIMENTACION");
        when(request.getParameter("idLista")).thenReturn("3");

        doThrow(new IllegalArgumentException("Saldo insuficiente"))
                .when(servicioMovimiento).registrarGasto(anyLong(), anyDouble(), anyString(), any());

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/listas/detalle?id=3");
    }

    // ===== PRUEBAS POST - ERRORES GENERALES =====

    @Test
    @DisplayName("POST / - Error general sin idLista")
    void testRegistrarMovimientoErrorGeneral() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn("1");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("100");
        when(request.getParameter("descripcion")).thenReturn("Test");
        when(request.getParameter("categoria")).thenReturn("SALARIO");
        when(request.getParameter("idLista")).thenReturn(null);

        doThrow(new RuntimeException("Error de BD"))
                .when(servicioMovimiento).registrarIngreso(anyLong(), anyDouble(), anyString(), any());

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
    }

    @Test
    @DisplayName("POST / - Error general con idLista")
    void testRegistrarMovimientoErrorGeneralConLista() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn("1");
        when(request.getParameter("tipo")).thenReturn("GASTO");
        when(request.getParameter("monto")).thenReturn("50");
        when(request.getParameter("descripcion")).thenReturn("Test");
        when(request.getParameter("categoria")).thenReturn("TRANSPORTE");
        when(request.getParameter("idLista")).thenReturn("10");

        doThrow(new RuntimeException("Error inesperado"))
                .when(servicioMovimiento).registrarGasto(anyLong(), anyDouble(), anyString(), any());

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/listas/detalle?id=10");
    }

    // ===== PRUEBAS POST - DESCRIPCIÓN CON ESPACIOS (TRIM) =====

    @Test
    @DisplayName("POST / - Descripción con espacios se trimea")
    void testRegistrarIngresoDescripcionConEspacios() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn("1");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("200");
        when(request.getParameter("descripcion")).thenReturn("  Descripción con espacios  ");
        when(request.getParameter("categoria")).thenReturn("SALARIO");
        when(request.getParameter("idLista")).thenReturn(null);

        servlet.doPost(request, response);

        verify(servicioMovimiento).registrarIngreso(eq(1L), eq(200.0), eq("Descripción con espacios"), eq(CategoriaIngreso.SALARIO));
    }

    @Test
    @DisplayName("POST / - Gasto con descripción trimeable")
    void testRegistrarGastoDescripcionConEspacios() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn("2");
        when(request.getParameter("tipo")).thenReturn("GASTO");
        when(request.getParameter("monto")).thenReturn("120");
        when(request.getParameter("descripcion")).thenReturn("   Gasto importante   ");
        when(request.getParameter("categoria")).thenReturn("SALUD");
        when(request.getParameter("idLista")).thenReturn(null);

        servlet.doPost(request, response);

        verify(servicioMovimiento).registrarGasto(eq(2L), eq(120.0), eq("Gasto importante"), eq(CategoriaGasto.SALUD));
    }

    // ===== PRUEBAS CASOS EDGE =====

    @Test
    @DisplayName("POST con PathInfo null")
    void testDoPostPathInfoNull() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(null);
        when(request.getParameter("cuentaId")).thenReturn("1");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("100");
        when(request.getParameter("descripcion")).thenReturn("Test");
        when(request.getParameter("categoria")).thenReturn("SALARIO");
        when(request.getParameter("idLista")).thenReturn(null);

        servlet.doPost(request, response);

        verify(servicioMovimiento).registrarIngreso(anyLong(), anyDouble(), anyString(), any());
    }

    @Test
    @DisplayName("POST con PathInfo '/'")
    void testDoPostPathInfoSlash() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("cuentaId")).thenReturn("1");
        when(request.getParameter("tipo")).thenReturn("GASTO");
        when(request.getParameter("monto")).thenReturn("50");
        when(request.getParameter("descripcion")).thenReturn("Test");
        when(request.getParameter("categoria")).thenReturn("OTROS");
        when(request.getParameter("idLista")).thenReturn(null);

        servlet.doPost(request, response);

        verify(servicioMovimiento).registrarGasto(anyLong(), anyDouble(), anyString(), any());
    }

    @Test
    @DisplayName("Destroy - Cierra recursos")
    void testDestroy() {
        assertDoesNotThrow(() -> servlet.destroy());
    }
}