package com.EconoMe.obligaciones.controladores;

import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.cuentas.modelos.TipoCuenta;
import com.EconoMe.cuentas.servicios.ServicioCuenta;
import com.EconoMe.obligaciones.dao.DAOObligacionFinanciera;
import com.EconoMe.obligaciones.modelos.Deuda;
import com.EconoMe.obligaciones.modelos.ObligacionFinanciera;
import com.EconoMe.obligaciones.modelos.Prestamo;
import com.EconoMe.obligaciones.servicios.ServicioObligacionFinanciera;

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

class ServletObligacionFinancieraIntegrationTest {

    private ServletObligacionFinanciera servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private ServicioObligacionFinanciera servicioDeudas;

    @Mock
    private DAOObligacionFinanciera daoObligacionFinanciera;

    @Mock
    private ServicioCuenta servicioCuenta;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        servlet = new ServletObligacionFinanciera();

        // Inyectar mocks usando reflection
        java.lang.reflect.Field servicioField = ServletObligacionFinanciera.class.getDeclaredField("servicioDeudas");
        servicioField.setAccessible(true);
        servicioField.set(servlet, servicioDeudas);

        java.lang.reflect.Field daoField = ServletObligacionFinanciera.class.getDeclaredField("daoObligacionFinanciera");
        daoField.setAccessible(true);
        daoField.set(servlet, daoObligacionFinanciera);

        java.lang.reflect.Field servicioCuentaField = ServletObligacionFinanciera.class.getDeclaredField("servicioCuenta");
        servicioCuentaField.setAccessible(true);
        servicioCuentaField.set(servlet, servicioCuenta);

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/EconoMe");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    // ===== PRUEBAS GET - FORMULARIO NUEVO =====

    @Test
    @DisplayName("GET /nuevo - Mostrar formulario de nueva obligación")
    void testMostrarFormularioNuevo() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/obligacion_financiera/nuevo");
        when(request.getRequestDispatcher("/obligacion_financiera/VistaObligacionFinancieraFormulario.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }

    // ===== PRUEBAS GET - LISTAR OBLIGACIONES =====

    @Test
    @DisplayName("GET /deudas - Listar obligaciones sin filtros")
    void testListarObligacionesSinFiltros() throws ServletException, IOException {
        List<ObligacionFinanciera> obligaciones = Arrays.asList(
                new Deuda("Juan Pérez", 1000.0, LocalDate.now().plusDays(30)),
                new Prestamo("María García", 2000.0, LocalDate.now().plusDays(60))
        );
        List<Cuenta> cuentas = Arrays.asList(
                new Cuenta("Efectivo", TipoCuenta.EFECTIVO, 5000.0)
        );

        when(request.getServletPath()).thenReturn("/obligacion_financiera/deudas");
        when(request.getParameter("accion")).thenReturn("listar");
        when(daoObligacionFinanciera.buscarConFiltros(null, null, null)).thenReturn(obligaciones);
        when(servicioCuenta.listarTodas()).thenReturn(cuentas);
        when(request.getRequestDispatcher("/obligacion_financiera/VistaObligacionFinanciera.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(daoObligacionFinanciera).buscarConFiltros(null, null, null);
        verify(request).setAttribute(eq("deudas"), eq(obligaciones));
        verify(request).setAttribute(eq("cuentas"), eq(cuentas));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET /deudas - Listar obligaciones con filtro de nombre")
    void testListarObligacionesConFiltroNombre() throws ServletException, IOException {
        String nombreFiltro = "Juan";
        List<ObligacionFinanciera> obligaciones = Arrays.asList(
                new Deuda("Juan Pérez", 1000.0, LocalDate.now().plusDays(30))
        );

        when(request.getServletPath()).thenReturn("/obligacion_financiera/deudas");
        when(request.getParameter("accion")).thenReturn(null);
        when(request.getParameter("nombrePersona")).thenReturn(nombreFiltro);
        when(daoObligacionFinanciera.buscarConFiltros(eq(nombreFiltro), isNull(), isNull()))
                .thenReturn(obligaciones);
        when(servicioCuenta.listarTodas()).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/obligacion_financiera/VistaObligacionFinanciera.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(daoObligacionFinanciera).buscarConFiltros(eq(nombreFiltro), isNull(), isNull());
        verify(request).setAttribute(eq("filtroNombre"), eq(nombreFiltro));
    }

    @Test
    @DisplayName("GET /deudas - Listar obligaciones con filtro de fechas")
    void testListarObligacionesConFiltroFechas() throws ServletException, IOException {
        LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
        LocalDate fechaFin = LocalDate.of(2024, 12, 31);

        when(request.getServletPath()).thenReturn("/obligacion_financiera/deudas");
        when(request.getParameter("accion")).thenReturn("listar");
        when(request.getParameter("fechaInicio")).thenReturn("2024-01-01");
        when(request.getParameter("fechaFin")).thenReturn("2024-12-31");
        when(daoObligacionFinanciera.buscarConFiltros(isNull(), eq(fechaInicio), eq(fechaFin)))
                .thenReturn(new ArrayList<>());
        when(servicioCuenta.listarTodas()).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/obligacion_financiera/VistaObligacionFinanciera.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(daoObligacionFinanciera).buscarConFiltros(isNull(), eq(fechaInicio), eq(fechaFin));
        verify(request).setAttribute(eq("filtroFechaInicio"), eq(fechaInicio));
        verify(request).setAttribute(eq("filtroFechaFin"), eq(fechaFin));
    }

    @Test
    @DisplayName("GET /deudas - Listar obligaciones con fecha inválida")
    void testListarObligacionesConFechaInvalida() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/obligacion_financiera/deudas");
        when(request.getParameter("accion")).thenReturn("listar");
        when(request.getParameter("fechaInicio")).thenReturn("fecha-invalida");
        when(daoObligacionFinanciera.buscarConFiltros(isNull(), isNull(), isNull()))
                .thenReturn(new ArrayList<>());
        when(servicioCuenta.listarTodas()).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/obligacion_financiera/VistaObligacionFinanciera.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(daoObligacionFinanciera).buscarConFiltros(isNull(), isNull(), isNull());
    }

    @Test
    @DisplayName("POST - Registrar con nombre vacío")
    void testRegistrarConNombreVacio() throws IOException {
        when(request.getParameter("accion")).thenReturn("registrar");
        when(request.getParameter("nombrePersona")).thenReturn("");
        when(request.getParameter("montoTotal")).thenReturn("1000");
        when(request.getParameter("fechaPago")).thenReturn("2025-02-15");
        when(request.getParameter("tipo")).thenReturn("DEUDA");

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("nuevo");
        verify(daoObligacionFinanciera, never()).crear(any());
    }

    @Test
    @DisplayName("POST - Registrar con nombre nulo")
    void testRegistrarConNombreNulo() throws IOException {
        when(request.getParameter("accion")).thenReturn("registrar");
        when(request.getParameter("nombrePersona")).thenReturn(null);
        when(request.getParameter("montoTotal")).thenReturn("1000");
        when(request.getParameter("fechaPago")).thenReturn("2025-02-15");
        when(request.getParameter("tipo")).thenReturn("DEUDA");

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("nuevo");
        verify(daoObligacionFinanciera, never()).crear(any());
    }

    @Test
    @DisplayName("POST - Registrar con monto cero")
    void testRegistrarConMontoCero() throws IOException {
        when(request.getParameter("accion")).thenReturn("registrar");
        when(request.getParameter("nombrePersona")).thenReturn("Juan Pérez");
        when(request.getParameter("montoTotal")).thenReturn("0");
        when(request.getParameter("fechaPago")).thenReturn("2025-02-15");
        when(request.getParameter("tipo")).thenReturn("DEUDA");

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("nuevo");
        verify(daoObligacionFinanciera, never()).crear(any());
    }

    @Test
    @DisplayName("POST - Registrar con monto negativo")
    void testRegistrarConMontoNegativo() throws IOException {
        when(request.getParameter("accion")).thenReturn("registrar");
        when(request.getParameter("nombrePersona")).thenReturn("Juan Pérez");
        when(request.getParameter("montoTotal")).thenReturn("-100");
        when(request.getParameter("fechaPago")).thenReturn("2025-02-15");
        when(request.getParameter("tipo")).thenReturn("DEUDA");

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("nuevo");
        verify(daoObligacionFinanciera, never()).crear(any());
    }

    @Test
    @DisplayName("POST - Registrar con monto inválido (no numérico)")
    void testRegistrarConMontoInvalido() throws IOException {
        when(request.getParameter("accion")).thenReturn("registrar");
        when(request.getParameter("nombrePersona")).thenReturn("Juan Pérez");
        when(request.getParameter("montoTotal")).thenReturn("abc");
        when(request.getParameter("fechaPago")).thenReturn("2025-02-15");
        when(request.getParameter("tipo")).thenReturn("DEUDA");

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("nuevo");
        verify(daoObligacionFinanciera, never()).crear(any());
    }

    @Test
    @DisplayName("POST - Registrar con fecha pasada (genera advertencia)")
    void testRegistrarConFechaPasada() throws IOException {
        LocalDate fechaPasada = LocalDate.now().minusDays(10);

        when(request.getParameter("accion")).thenReturn("registrar");
        when(request.getParameter("nombrePersona")).thenReturn("Juan Pérez");
        when(request.getParameter("montoTotal")).thenReturn("1000");
        when(request.getParameter("fechaPago")).thenReturn(fechaPasada.toString());
        when(request.getParameter("tipo")).thenReturn("DEUDA");

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(daoObligacionFinanciera).crear(any(Deuda.class));
        verify(session, atLeast(1)).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("deudas?accion=listar");
    }

    // ===== PRUEBAS POST - ABONAR A OBLIGACIÓN =====

    @Test
    @DisplayName("POST - Abonar exitosamente")
    void testAbonarExitoso() throws IOException {
        Deuda deuda = new Deuda("Juan Pérez", 1000.0, LocalDate.now().plusDays(30));
        deuda.setId(16L);

        when(request.getParameter("accion")).thenReturn("abonar");
        when(request.getParameter("idDeuda")).thenReturn("1");
        when(request.getParameter("monto")).thenReturn("500.00");
        when(request.getParameter("idCartera")).thenReturn("1");
        when(daoObligacionFinanciera.buscarPorId(1L)).thenReturn(deuda);

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(servicioDeudas).abonarADeuda(1L, 1L, 500.0);
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("deudas?accion=listar");
    }

    @Test
    @DisplayName("POST - Abonar monto que completa pago")
    void testAbonarMontoCompletaPago() throws IOException {
        Deuda deuda = new Deuda("Juan Pérez", 1000.0, LocalDate.now().plusDays(30));
        deuda.setId(1L);

        when(request.getParameter("accion")).thenReturn("abonar");
        when(request.getParameter("idDeuda")).thenReturn("1");
        when(request.getParameter("monto")).thenReturn("1000.00");
        when(request.getParameter("idCartera")).thenReturn("1");
        when(daoObligacionFinanciera.buscarPorId(1L)).thenReturn(deuda);

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(servicioDeudas).abonarADeuda(1L, 1L, 1000.0);
        verify(session).setAttribute(eq("mensajes"), any());
    }

    @Test
    @DisplayName("POST - Abonar con monto cero")
    void testAbonarConMontoCero() throws IOException {
        when(request.getParameter("accion")).thenReturn("abonar");
        when(request.getParameter("idDeuda")).thenReturn("1");
        when(request.getParameter("monto")).thenReturn("0");
        when(request.getParameter("idCartera")).thenReturn("1");

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("deudas?accion=listar");
        verify(servicioDeudas, never()).abonarADeuda(anyLong(), anyLong(), anyDouble());
    }

    @Test
    @DisplayName("POST - Abonar con monto negativo")
    void testAbonarConMontoNegativo() throws IOException {
        when(request.getParameter("accion")).thenReturn("abonar");
        when(request.getParameter("idDeuda")).thenReturn("1");
        when(request.getParameter("monto")).thenReturn("-100");
        when(request.getParameter("idCartera")).thenReturn("1");

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("deudas?accion=listar");
        verify(servicioDeudas, never()).abonarADeuda(anyLong(), anyLong(), anyDouble());
    }

    @Test
    @DisplayName("POST - Abonar con obligación no encontrada")
    void testAbonarConObligacionNoEncontrada() throws IOException {
        when(request.getParameter("accion")).thenReturn("abonar");
        when(request.getParameter("idDeuda")).thenReturn("999");
        when(request.getParameter("monto")).thenReturn("500");
        when(request.getParameter("idCartera")).thenReturn("1");
        when(daoObligacionFinanciera.buscarPorId(999L)).thenReturn(null);

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("deudas?accion=listar");
        verify(servicioDeudas, never()).abonarADeuda(anyLong(), anyLong(), anyDouble());
    }

    @Test
    @DisplayName("POST - Abonar monto mayor al saldo pendiente")
    void testAbonarMontoMayorAlSaldo() throws IOException {
        Deuda deuda = new Deuda("Juan Pérez", 1000.0, LocalDate.now().plusDays(30));
        deuda.setId(1L);

        when(request.getParameter("accion")).thenReturn("abonar");
        when(request.getParameter("idDeuda")).thenReturn("1");
        when(request.getParameter("monto")).thenReturn("1500.00");
        when(request.getParameter("idCartera")).thenReturn("1");
        when(daoObligacionFinanciera.buscarPorId(1L)).thenReturn(deuda);

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("deudas?accion=listar");
        verify(servicioDeudas, never()).abonarADeuda(anyLong(), anyLong(), anyDouble());
    }

    @Test
    @DisplayName("POST - Abonar con ID inválido")
    void testAbonarConIdInvalido() throws IOException {
        when(request.getParameter("accion")).thenReturn("abonar");
        when(request.getParameter("idDeuda")).thenReturn("abc");
        when(request.getParameter("monto")).thenReturn("500");
        when(request.getParameter("idCartera")).thenReturn("1");

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("deudas?accion=listar");
        verify(servicioDeudas, never()).abonarADeuda(anyLong(), anyLong(), anyDouble());
    }

    @Test
    @DisplayName("POST - Abonar con monto inválido")
    void testAbonarConMontoInvalido() throws IOException {
        when(request.getParameter("accion")).thenReturn("abonar");
        when(request.getParameter("idDeuda")).thenReturn("1");
        when(request.getParameter("monto")).thenReturn("xyz");
        when(request.getParameter("idCartera")).thenReturn("1");

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("deudas?accion=listar");
        verify(servicioDeudas, never()).abonarADeuda(anyLong(), anyLong(), anyDouble());
    }

    @Test
    @DisplayName("POST - Abonar con error del servicio")
    void testAbonarConErrorServicio() throws IOException {
        Deuda deuda = new Deuda("Juan Pérez", 1000.0, LocalDate.now().plusDays(30));
        deuda.setId(1L);

        when(request.getParameter("accion")).thenReturn("abonar");
        when(request.getParameter("idDeuda")).thenReturn("1");
        when(request.getParameter("monto")).thenReturn("500");
        when(request.getParameter("idCartera")).thenReturn("1");
        when(daoObligacionFinanciera.buscarPorId(1L)).thenReturn(deuda);
        doThrow(new RuntimeException("Error de BD"))
                .when(servicioDeudas).abonarADeuda(anyLong(), anyLong(), anyDouble());

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("deudas?accion=listar");
    }

    // ===== PRUEBAS DE CASOS EDGE =====

    @Test
    @DisplayName("GET /deudas - Sin parámetro acción (default listar)")
    void testListarSinParametroAccion() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/obligacion_financiera/deudas");
        when(request.getParameter("accion")).thenReturn(null);
        when(daoObligacionFinanciera.buscarConFiltros(any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(servicioCuenta.listarTodas()).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/obligacion_financiera/VistaObligacionFinanciera.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET - Lista de personas únicas extraída correctamente")
    void testListaPersonasUnicas() throws ServletException, IOException {
        List<ObligacionFinanciera> obligaciones = Arrays.asList(
                new Deuda("Juan Pérez", 1000.0, LocalDate.now()),
                new Deuda("Juan Pérez", 500.0, LocalDate.now()),
                new Prestamo("María García", 2000.0, LocalDate.now())
        );

        when(request.getServletPath()).thenReturn("/obligacion_financiera/deudas");
        when(request.getParameter("accion")).thenReturn("listar");
        when(daoObligacionFinanciera.buscarConFiltros(any(), any(), any()))
                .thenReturn(obligaciones);
        when(servicioCuenta.listarTodas()).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/obligacion_financiera/VistaObligacionFinanciera.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("personas"), argThat(list ->
                list instanceof List && ((List<?>) list).size() == 2
        ));
    }

    @Test
    @DisplayName("Destroy - No lanza excepciones")
    void testDestroy() {
        assertDoesNotThrow(() -> servlet.destroy());
    }
}