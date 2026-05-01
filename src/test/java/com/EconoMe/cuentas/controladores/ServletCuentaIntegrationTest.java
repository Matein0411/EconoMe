package com.EconoMe.cuentas.controladores;

import com.EconoMe.comun.mensajes.MensajeUtil;
import com.EconoMe.cuentas.dao.DAOCuenta;
import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.cuentas.modelos.TipoCuenta;
import com.EconoMe.cuentas.servicios.ServicioCuenta;
import com.EconoMe.movimientos.modelos.Ingreso;
import com.EconoMe.movimientos.modelos.Movimiento;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ServletCuentaIntegrationTest {

    private ServletCuenta servlet;

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
    private ServicioCuenta servicioCuenta;

    @Mock
    private ServicioMovimiento servicioMovimiento;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        servlet = new ServletCuenta();

        // Inyectar mocks usando reflection
        java.lang.reflect.Field daoCuentaField = ServletCuenta.class.getDeclaredField("daoCuenta");
        daoCuentaField.setAccessible(true);
        daoCuentaField.set(servlet, daoCuenta);

        java.lang.reflect.Field servicioCuentaField = ServletCuenta.class.getDeclaredField("servicioCuenta");
        servicioCuentaField.setAccessible(true);
        servicioCuentaField.set(servlet, servicioCuenta);

        java.lang.reflect.Field servicioMovimientoField = ServletCuenta.class.getDeclaredField("servicioMovimiento");
        servicioMovimientoField.setAccessible(true);
        servicioMovimientoField.set(servlet, servicioMovimiento);

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/EconoMe");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    // ===== PRUEBAS GET - LISTAR CUENTAS =====

    @Test
    @DisplayName("GET / - Listar cuentas exitosamente")
    void testListarCuentasExitoso() throws ServletException, IOException {
        List<Cuenta> cuentas = Arrays.asList(
                new Cuenta("Efectivo", TipoCuenta.EFECTIVO, 1000.0),
                new Cuenta("Banco", TipoCuenta.AHORROS, 5000.0)
        );

        when(request.getPathInfo()).thenReturn("/");
        when(daoCuenta.listar()).thenReturn(cuentas);
        when(request.getRequestDispatcher("/cuenta/VistaCuentas.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(daoCuenta).listar();
        verify(request).setAttribute("cuentas", cuentas);
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET / - Listar cuentas con error en DAO")
    void testListarCuentasConError() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(daoCuenta.listar()).thenThrow(new RuntimeException("Error de base de datos"));
        when(request.getRequestDispatcher("/cuenta/VistaCuentas.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET / - Listar cuentas vacías")
    void testListarCuentasVacia() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(daoCuenta.listar()).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/cuenta/VistaCuentas.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("cuentas", new ArrayList<>());
        verify(dispatcher).forward(request, response);
    }

    // ===== PRUEBAS GET - FORMULARIO NUEVO =====

    @Test
    @DisplayName("GET /nuevo - Mostrar formulario de nueva cuenta")
    void testMostrarFormularioNuevo() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/nuevo");
        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("tipos", TipoCuenta.values());
        verify(dispatcher).forward(request, response);
    }

    // ===== PRUEBAS GET - DETALLE CUENTA =====


    @Test
    @DisplayName("GET /detalle - ID de cuenta inválido (no numérico)")
    void testMostrarDetalleCuentaIdInvalido() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/detalle");
        when(request.getParameter("id")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/cuentas");
    }

    @Test
    @DisplayName("GET /detalle - Cuenta no existe")
    void testMostrarDetalleCuentaNoExiste() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/detalle");
        when(request.getParameter("id")).thenReturn("999");
        when(servicioCuenta.buscarCuenta(999L)).thenReturn(null);

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/cuentas");
    }

    @Test
    @DisplayName("GET /detalle - Página por defecto (sin parámetro)")
    void testMostrarDetalleCuentaPaginaPorDefecto() throws ServletException, IOException {
        Long cuentaId = 1L;
        Cuenta cuenta = new Cuenta("Efectivo", TipoCuenta.EFECTIVO, 1000.0);

        when(request.getPathInfo()).thenReturn("/detalle");
        when(request.getParameter("id")).thenReturn(cuentaId.toString());
        when(request.getParameter("pagina")).thenReturn(null);
        when(servicioCuenta.buscarCuenta(cuentaId)).thenReturn(cuenta);
        when(servicioMovimiento.obtenerTotalPaginasConFiltros(anyLong(), any(), any(), any(), any(), anyInt())).thenReturn(1);
        when(servicioMovimiento.listarMovimientosConFiltros(anyLong(), any(), any(), any(), any(), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/cuenta/VistaDetalleCuenta.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("paginaActual", 1);
    }

    @Test
    @DisplayName("GET /detalle - Página negativa (lanza error)")
    void testMostrarDetalleCuentaPaginaNegativa() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/detalle");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("pagina")).thenReturn("-1");

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/cuentas/detalle?id=1");
    }

    @Test
    @DisplayName("GET /detalle - Página no numérica")
    void testMostrarDetalleCuentaPaginaInvalida() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/detalle");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("pagina")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/cuentas/detalle?id=1");
    }

    @Test
    @DisplayName("GET /detalle - Página mayor al total (se ajusta)")
    void testMostrarDetalleCuentaPaginaMayorAlTotal() throws ServletException, IOException {
        Long cuentaId = 1L;
        Cuenta cuenta = new Cuenta("Efectivo", TipoCuenta.EFECTIVO, 1000.0);

        when(request.getPathInfo()).thenReturn("/detalle");
        when(request.getParameter("id")).thenReturn(cuentaId.toString());
        when(request.getParameter("pagina")).thenReturn("10");
        when(servicioCuenta.buscarCuenta(cuentaId)).thenReturn(cuenta);
        when(servicioMovimiento.obtenerTotalPaginasConFiltros(anyLong(), any(), any(), any(), any(), anyInt())).thenReturn(3);
        when(servicioMovimiento.listarMovimientosConFiltros(anyLong(), any(), any(), any(), any(), eq(3), anyInt())).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/cuenta/VistaDetalleCuenta.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("paginaActual", 3);
        verify(servicioMovimiento).listarMovimientosConFiltros(anyLong(), any(), any(), any(), any(), eq(3), anyInt());
    }

    @Test
    @DisplayName("GET /detalle - Con filtros aplicados")
    void testMostrarDetalleCuentaConFiltros() throws ServletException, IOException {
        Long cuentaId = 1L;
        Cuenta cuenta = new Cuenta("Efectivo", TipoCuenta.EFECTIVO, 1000.0);

        when(request.getPathInfo()).thenReturn("/detalle");
        when(request.getParameter("id")).thenReturn(cuentaId.toString());
        when(request.getParameter("pagina")).thenReturn("1");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("categoria")).thenReturn("Salario");
        when(request.getParameter("fechaDesde")).thenReturn("2024-01-01");
        when(request.getParameter("fechaHasta")).thenReturn("2024-12-31");
        when(servicioCuenta.buscarCuenta(cuentaId)).thenReturn(cuenta);
        when(servicioMovimiento.obtenerTotalPaginasConFiltros(eq(cuentaId), eq("INGRESO"), eq("Salario"), eq("2024-01-01"), eq("2024-12-31"), anyInt())).thenReturn(1);
        when(servicioMovimiento.listarMovimientosConFiltros(eq(cuentaId), eq("INGRESO"), eq("Salario"), eq("2024-01-01"), eq("2024-12-31"), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/cuenta/VistaDetalleCuenta.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(servicioMovimiento).listarMovimientosConFiltros(eq(cuentaId), eq("INGRESO"), eq("Salario"), eq("2024-01-01"), eq("2024-12-31"), anyInt(), anyInt());
    }

    @Test
    @DisplayName("GET /detalle - Sin movimientos (cantidad total = 0)")
    void testMostrarDetalleCuentaSinMovimientos() throws ServletException, IOException {
        Long cuentaId = 1L;
        Cuenta cuenta = new Cuenta("Efectivo", TipoCuenta.EFECTIVO, 1000.0);

        when(request.getPathInfo()).thenReturn("/detalle");
        when(request.getParameter("id")).thenReturn(cuentaId.toString());
        when(servicioCuenta.buscarCuenta(cuentaId)).thenReturn(cuenta);
        when(servicioMovimiento.obtenerTotalPaginasConFiltros(anyLong(), any(), any(), any(), any(), anyInt())).thenReturn(0);
        when(servicioMovimiento.listarMovimientosConFiltros(anyLong(), any(), any(), any(), any(), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/cuenta/VistaDetalleCuenta.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("mostrandoDesde", 0);
        verify(request).setAttribute("esUltimaPagina", true);
    }

    @Test
    @DisplayName("GET /detalle - Error general en el procesamiento")
    void testMostrarDetalleCuentaErrorGeneral() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/detalle");
        when(request.getParameter("id")).thenReturn("1");
        when(servicioCuenta.buscarCuenta(1L)).thenThrow(new RuntimeException("Error de BD"));

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/cuentas");
    }

    // ===== PRUEBAS POST - CREAR CUENTA =====

    @Test
    @DisplayName("POST / - Crear cuenta exitosamente")
    void testCrearCuentaExitoso() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("nombre")).thenReturn("Mi Cuenta");
        when(request.getParameter("tipo")).thenReturn("EFECTIVO");
        when(request.getParameter("monto")).thenReturn("1000.50");

        servlet.doPost(request, response);

        verify(servicioCuenta).crearCuenta(any(Cuenta.class));
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/cuentas");
    }

    @Test
    @DisplayName("POST / - Nombre vacío")
    void testCrearCuentaNombreVacio() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("nombre")).thenReturn("");
        when(request.getParameter("tipo")).thenReturn("EFECTIVO");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(dispatcher).forward(request, response);
        verify(servicioCuenta, never()).crearCuenta(any());
    }

    @Test
    @DisplayName("POST / - Nombre nulo")
    void testCrearCuentaNombreNulo() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("nombre")).thenReturn(null);
        when(request.getParameter("tipo")).thenReturn("EFECTIVO");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("POST / - Tipo vacío")
    void testCrearCuentaTipoVacio() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("nombre")).thenReturn("Mi Cuenta");
        when(request.getParameter("tipo")).thenReturn("");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("POST / - Tipo nulo")
    void testCrearCuentaTipoNulo() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("nombre")).thenReturn("Mi Cuenta");
        when(request.getParameter("tipo")).thenReturn(null);
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("POST / - Monto vacío")
    void testCrearCuentaMontoVacio() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("nombre")).thenReturn("Mi Cuenta");
        when(request.getParameter("tipo")).thenReturn("EFECTIVO");
        when(request.getParameter("monto")).thenReturn("");
        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("POST / - Monto nulo")
    void testCrearCuentaMontoNulo() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("nombre")).thenReturn("Mi Cuenta");
        when(request.getParameter("tipo")).thenReturn("EFECTIVO");
        when(request.getParameter("monto")).thenReturn(null);
        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("POST / - Monto no numérico")
    void testCrearCuentaMontoInvalido() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("nombre")).thenReturn("Mi Cuenta");
        when(request.getParameter("tipo")).thenReturn("EFECTIVO");
        when(request.getParameter("monto")).thenReturn("abc");
        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("POST / - Error de validación del servicio (monto negativo)")
    void testCrearCuentaValidacionServicio() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("nombre")).thenReturn("Mi Cuenta");
        when(request.getParameter("tipo")).thenReturn("EFECTIVO");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);

        doThrow(new IllegalArgumentException("El monto debe ser mayor a cero"))
                .when(servicioCuenta).crearCuenta(any(Cuenta.class));

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("POST / - Cuenta duplicada")
    void testCrearCuentaDuplicada() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("nombre")).thenReturn("Mi Cuenta");
        when(request.getParameter("tipo")).thenReturn("EFECTIVO");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);

        doThrow(new IllegalStateException("Cuenta duplicada"))
                .when(servicioCuenta).crearCuenta(any(Cuenta.class));

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("POST / - Error general inesperado")
    void testCrearCuentaErrorGeneral() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("nombre")).thenReturn("Mi Cuenta");
        when(request.getParameter("tipo")).thenReturn("EFECTIVO");
        when(request.getParameter("monto")).thenReturn("1000");

        doThrow(new RuntimeException("Error de BD"))
                .when(servicioCuenta).crearCuenta(any(Cuenta.class));

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/cuentas/nuevo");
    }

    @Test
    @DisplayName("POST / - Nombre con espacios (se trimea)")
    void testCrearCuentaNombreConEspacios() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("nombre")).thenReturn("  Mi Cuenta  ");
        when(request.getParameter("tipo")).thenReturn("EFECTIVO");
        when(request.getParameter("monto")).thenReturn("1000");

        servlet.doPost(request, response);

        verify(servicioCuenta).crearCuenta(argThat(cuenta ->
                cuenta.getNombre().equals("Mi Cuenta")
        ));
    }

    // ===== PRUEBAS DE CASOS EDGE =====

    @Test
    @DisplayName("GET con PathInfo null (default case)")
    void testDoGetPathInfoNull() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(null);
        when(daoCuenta.listar()).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/cuenta/VistaCuentas.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(daoCuenta).listar();
    }

    @Test
    @DisplayName("POST con PathInfo null")
    void testDoPostPathInfoNull() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(null);
        when(request.getParameter("nombre")).thenReturn("Cuenta");
        when(request.getParameter("tipo")).thenReturn("EFECTIVO");
        when(request.getParameter("monto")).thenReturn("1000");

        servlet.doPost(request, response);

        verify(servicioCuenta).crearCuenta(any(Cuenta.class));
    }

    @Test
    @DisplayName("Destroy - Cierra recursos")
    void testDestroy() {
        assertDoesNotThrow(() -> servlet.destroy());
    }
}