package com.EconoMe.recordatorios.controladores;

import com.EconoMe.recordatorios.modelos.Recordatorio;
import com.EconoMe.recordatorios.modelos.Recurrencia;
import com.EconoMe.recordatorios.servicios.ServicioRecordatorio;

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

class ServletRecordatorioIntegrationTest {

    private ServletRecordatorio servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private ServicioRecordatorio servicioRecordatorio;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        servlet = new ServletRecordatorio();

        // Inyectar mock usando reflection
        java.lang.reflect.Field servicioField = ServletRecordatorio.class.getDeclaredField("servicioRecordatorio");
        servicioField.setAccessible(true);
        servicioField.set(servlet, servicioRecordatorio);

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/EconoMe");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    // ===== PRUEBAS GET - LISTAR RECORDATORIOS =====

    @Test
    @DisplayName("GET / - Listar recordatorios exitosamente")
    void testListarRecordatoriosExitoso() throws ServletException, IOException {
        Recordatorio r1 = new Recordatorio();
        r1.setDescripcion("Pagar luz");
        r1.setFechaInicio(LocalDate.now());
        r1.setRecurrencia(Recurrencia.MENSUAL);
        r1.setDiasDeAnticipacion(3);

        List<Recordatorio> recordatorios = Arrays.asList(r1);

        when(request.getPathInfo()).thenReturn("/");
        when(servicioRecordatorio.listarActivos()).thenReturn(recordatorios);
        when(request.getRequestDispatcher("/recordatorio/VistaRecordatorio.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(servicioRecordatorio).listarActivos();
        verify(request).setAttribute("recordatorios", recordatorios);
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET / - Listar con error del servicio")
    void testListarRecordatoriosConError() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(servicioRecordatorio.listarActivos()).thenThrow(new RuntimeException("Error de BD"));
        when(request.getRequestDispatcher("/recordatorio/VistaRecordatorio.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(request).setAttribute(eq("recordatorios"), eq(List.of()));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET null - Listar recordatorios (pathInfo null)")
    void testListarRecordatoriosPathInfoNull() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(null);
        when(servicioRecordatorio.listarActivos()).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/recordatorio/VistaRecordatorio.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(servicioRecordatorio).listarActivos();
        verify(dispatcher).forward(request, response);
    }

    // ===== PRUEBAS GET - FORMULARIO NUEVO =====

    @Test
    @DisplayName("GET /nuevo - Mostrar formulario de nuevo recordatorio")
    void testMostrarFormularioNuevo() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/nuevo");
        when(request.getRequestDispatcher("/recordatorio/VistaFormularioRecordatorio.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("recordatorio"), any(Recordatorio.class));
        verify(request).setAttribute("recurrencias", Recurrencia.values());
        verify(dispatcher).forward(request, response);
    }

    // ===== PRUEBAS GET - FORMULARIO EDITAR =====

    @Test
    @DisplayName("GET /editar - Mostrar formulario de edición exitosamente")
    void testMostrarFormularioEditarExitoso() throws ServletException, IOException {
        Recordatorio recordatorio = new Recordatorio();
        recordatorio.setId(1L);
        recordatorio.setDescripcion("Pagar agua");
        recordatorio.setFechaInicio(LocalDate.now());
        recordatorio.setRecurrencia(Recurrencia.MENSUAL);
        recordatorio.setDiasDeAnticipacion(5);

        when(request.getPathInfo()).thenReturn("/editar");
        when(request.getParameter("id")).thenReturn("1");
        when(servicioRecordatorio.buscarPorId(1L)).thenReturn(recordatorio);
        when(request.getRequestDispatcher("/recordatorio/VistaFormularioRecordatorio.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(servicioRecordatorio).buscarPorId(1L);
        verify(request).setAttribute("recordatorio", recordatorio);
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET /editar - Recordatorio no encontrado")
    void testMostrarFormularioEditarNoEncontrado() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/editar");
        when(request.getParameter("id")).thenReturn("999");
        when(servicioRecordatorio.buscarPorId(999L)).thenReturn(null);

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/recordatorios");
    }

    @Test
    @DisplayName("GET /editar - ID inválido (no numérico)")
    void testMostrarFormularioEditarIdInvalido() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/editar");
        when(request.getParameter("id")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/EconoMe/recordatorios");
        verify(servicioRecordatorio, never()).buscarPorId(anyLong());
    }

    // ===== PRUEBAS POST - CREAR RECORDATORIO =====

    @Test
    @DisplayName("POST / - Crear recordatorio exitosamente")
    void testCrearRecordatorioExitoso() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("_method")).thenReturn(null);
        when(request.getParameter("descripcion")).thenReturn("Pagar luz");
        when(request.getParameter("fechaInicio")).thenReturn("2025-02-01");
        when(request.getParameter("fechaFin")).thenReturn("2025-12-31");
        when(request.getParameter("recurrencia")).thenReturn("MENSUAL");
        when(request.getParameter("diasDeAnticipacion")).thenReturn("3");
        when(request.getParameter("monto")).thenReturn("100.50");

        servlet.doPost(request, response);

        verify(servicioRecordatorio).crearRecordatorio(any(Recordatorio.class));
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/recordatorios");
    }

    @Test
    @DisplayName("POST / - Crear recordatorio sin fecha fin")
    void testCrearRecordatorioSinFechaFin() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("_method")).thenReturn(null);
        when(request.getParameter("descripcion")).thenReturn("Pagar agua");
        when(request.getParameter("fechaInicio")).thenReturn("2025-02-01");
        when(request.getParameter("fechaFin")).thenReturn("");
        when(request.getParameter("recurrencia")).thenReturn("SEMANAL");
        when(request.getParameter("diasDeAnticipacion")).thenReturn("2");
        when(request.getParameter("monto")).thenReturn("50.00");

        servlet.doPost(request, response);

        verify(servicioRecordatorio).crearRecordatorio(argThat(r -> r.getFechaFin() == null));
        verify(response).sendRedirect("/EconoMe/recordatorios");
    }

    @Test
    @DisplayName("POST / - Crear recordatorio con monto inválido (se asigna 0)")
    void testCrearRecordatorioMontoInvalido() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("_method")).thenReturn(null);
        when(request.getParameter("descripcion")).thenReturn("Pagar internet");
        when(request.getParameter("fechaInicio")).thenReturn("2025-02-01");
        when(request.getParameter("fechaFin")).thenReturn("");
        when(request.getParameter("recurrencia")).thenReturn("MENSUAL");
        when(request.getParameter("diasDeAnticipacion")).thenReturn("5");
        when(request.getParameter("monto")).thenReturn("abc");

        servlet.doPost(request, response);

        verify(servicioRecordatorio).crearRecordatorio(argThat(r -> r.getMonto() == 0.0));
        verify(response).sendRedirect("/EconoMe/recordatorios");
    }

    @Test
    @DisplayName("POST / - Crear con fecha inicio inválida")
    void testCrearRecordatorioFechaInicioInvalida() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("_method")).thenReturn(null);
        when(request.getParameter("descripcion")).thenReturn("Pagar luz");
        when(request.getParameter("fechaInicio")).thenReturn("fecha-invalida");
        when(request.getParameter("fechaFin")).thenReturn("");
        when(request.getParameter("recurrencia")).thenReturn("MENSUAL");
        when(request.getParameter("diasDeAnticipacion")).thenReturn("3");
        when(request.getParameter("monto")).thenReturn("100");
        when(request.getRequestDispatcher("/recordatorio/VistaFormularioRecordatorio.jsp"))
                .thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(dispatcher).forward(request, response);
        verify(servicioRecordatorio, never()).crearRecordatorio(any());
    }

    @Test
    @DisplayName("POST / - Crear con validación del servicio falla")
    void testCrearRecordatorioValidacionServicioFalla() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("_method")).thenReturn(null);
        when(request.getParameter("descripcion")).thenReturn("Pagar luz");
        when(request.getParameter("fechaInicio")).thenReturn("2025-02-01");
        when(request.getParameter("fechaFin")).thenReturn("");
        when(request.getParameter("recurrencia")).thenReturn("MENSUAL");
        when(request.getParameter("diasDeAnticipacion")).thenReturn("3");
        when(request.getParameter("monto")).thenReturn("100");
        when(request.getRequestDispatcher("/recordatorio/VistaFormularioRecordatorio.jsp"))
                .thenReturn(dispatcher);

        doThrow(new IllegalArgumentException("Descripción requerida"))
                .when(servicioRecordatorio).crearRecordatorio(any());

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("POST / - Crear con error general")
    void testCrearRecordatorioErrorGeneral() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("_method")).thenReturn(null);
        when(request.getParameter("descripcion")).thenReturn("Pagar luz");
        when(request.getParameter("fechaInicio")).thenReturn("2025-02-01");
        when(request.getParameter("fechaFin")).thenReturn("");
        when(request.getParameter("recurrencia")).thenReturn("MENSUAL");
        when(request.getParameter("diasDeAnticipacion")).thenReturn("3");
        when(request.getParameter("monto")).thenReturn("100");
        when(request.getRequestDispatcher("/recordatorio/VistaFormularioRecordatorio.jsp"))
                .thenReturn(dispatcher);

        doThrow(new RuntimeException("Error de BD"))
                .when(servicioRecordatorio).crearRecordatorio(any());

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(dispatcher).forward(request, response);
    }

    // ===== PRUEBAS POST - EDITAR RECORDATORIO =====

    @Test
    @DisplayName("POST / - Editar recordatorio exitosamente")
    void testEditarRecordatorioExitoso() throws ServletException, IOException {
        Recordatorio existente = new Recordatorio();
        existente.setId(1L);
        existente.setDescripcion("Pagar agua");

        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("_method")).thenReturn("PUT");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("descripcion")).thenReturn("Pagar agua editada");
        when(request.getParameter("fechaInicio")).thenReturn("2025-02-01");
        when(request.getParameter("fechaFin")).thenReturn("");
        when(request.getParameter("recurrencia")).thenReturn("MENSUAL");
        when(request.getParameter("diasDeAnticipacion")).thenReturn("5");
        when(request.getParameter("monto")).thenReturn("200");
        when(servicioRecordatorio.buscarPorId(1L)).thenReturn(existente);

        servlet.doPost(request, response);

        verify(servicioRecordatorio).actualizarRecordatorio(any(Recordatorio.class));
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/recordatorios");
    }

    @Test
    @DisplayName("POST / - Editar recordatorio no existente")
    void testEditarRecordatorioNoExistente() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("_method")).thenReturn("PUT");
        when(request.getParameter("id")).thenReturn("999");
        when(request.getParameter("descripcion")).thenReturn("Test");
        when(request.getParameter("fechaInicio")).thenReturn("2025-02-01");
        when(request.getParameter("fechaFin")).thenReturn("");
        when(request.getParameter("recurrencia")).thenReturn("MENSUAL");
        when(request.getParameter("diasDeAnticipacion")).thenReturn("3");
        when(request.getParameter("monto")).thenReturn("100");
        when(servicioRecordatorio.buscarPorId(999L)).thenReturn(null);
        when(request.getRequestDispatcher("/recordatorio/VistaFormularioRecordatorio.jsp"))
                .thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(dispatcher).forward(request, response);
        verify(servicioRecordatorio, never()).actualizarRecordatorio(any());
    }

    @Test
    @DisplayName("POST / - Editar con fecha inválida")
    void testEditarRecordatorioFechaInvalida() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("_method")).thenReturn("PUT");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("descripcion")).thenReturn("Test");
        when(request.getParameter("fechaInicio")).thenReturn("fecha-mala");
        when(request.getParameter("fechaFin")).thenReturn("");
        when(request.getParameter("recurrencia")).thenReturn("MENSUAL");
        when(request.getParameter("diasDeAnticipacion")).thenReturn("3");
        when(request.getParameter("monto")).thenReturn("100");
        when(request.getRequestDispatcher("/recordatorio/VistaFormularioRecordatorio.jsp"))
                .thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(dispatcher).forward(request, response);
        verify(servicioRecordatorio, never()).actualizarRecordatorio(any());
    }

    // ===== PRUEBAS POST - BORRAR RECORDATORIO =====

    @Test
    @DisplayName("POST /borrar - Eliminar recordatorio exitosamente")
    void testBorrarRecordatorioExitoso() throws IOException {
        when(request.getPathInfo()).thenReturn("/borrar");
        when(request.getParameter("id")).thenReturn("1");

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(servicioRecordatorio).eliminarRecordatorio(1L);
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/recordatorios");
    }

    @Test
    @DisplayName("POST /borrar - ID nulo")
    void testBorrarRecordatorioIdNulo() throws IOException {
        when(request.getPathInfo()).thenReturn("/borrar");
        when(request.getParameter("id")).thenReturn(null);

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/recordatorios");
        verify(servicioRecordatorio, never()).eliminarRecordatorio(anyLong());
    }

    @Test
    @DisplayName("POST /borrar - ID vacío")
    void testBorrarRecordatorioIdVacio() throws IOException {
        when(request.getPathInfo()).thenReturn("/borrar");
        when(request.getParameter("id")).thenReturn("  ");

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/recordatorios");
        verify(servicioRecordatorio, never()).eliminarRecordatorio(anyLong());
    }

    @Test
    @DisplayName("POST /borrar - ID inválido (no numérico)")
    void testBorrarRecordatorioIdInvalido() throws IOException {
        when(request.getPathInfo()).thenReturn("/borrar");
        when(request.getParameter("id")).thenReturn("abc");

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/recordatorios");
        verify(servicioRecordatorio, never()).eliminarRecordatorio(anyLong());
    }

    @Test
    @DisplayName("POST /borrar - Error de validación del servicio")
    void testBorrarRecordatorioErrorValidacion() throws IOException {
        when(request.getPathInfo()).thenReturn("/borrar");
        when(request.getParameter("id")).thenReturn("1");
        doThrow(new IllegalArgumentException("Recordatorio no encontrado"))
                .when(servicioRecordatorio).eliminarRecordatorio(1L);

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/recordatorios");
    }

    @Test
    @DisplayName("POST /borrar - Error general")
    void testBorrarRecordatorioErrorGeneral() throws IOException {
        when(request.getPathInfo()).thenReturn("/borrar");
        when(request.getParameter("id")).thenReturn("1");
        doThrow(new RuntimeException("Error de BD"))
                .when(servicioRecordatorio).eliminarRecordatorio(1L);

        try {
            servlet.doPost(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/recordatorios");
    }

    // ===== PRUEBAS DE CASOS EDGE =====

    @Test
    @DisplayName("POST / - Crear con fechaFin null en request")
    void testCrearConFechaFinNull() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("_method")).thenReturn(null);
        when(request.getParameter("descripcion")).thenReturn("Test");
        when(request.getParameter("fechaInicio")).thenReturn("2025-02-01");
        when(request.getParameter("fechaFin")).thenReturn(null);
        when(request.getParameter("recurrencia")).thenReturn("MENSUAL");
        when(request.getParameter("diasDeAnticipacion")).thenReturn("3");
        when(request.getParameter("monto")).thenReturn("100");

        servlet.doPost(request, response);

        verify(servicioRecordatorio).crearRecordatorio(argThat(r -> r.getFechaFin() == null));
    }

    @Test
    @DisplayName("GET / - Listar recordatorios vacíos")
    void testListarRecordatoriosVacios() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");
        when(servicioRecordatorio.listarActivos()).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/recordatorio/VistaRecordatorio.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("recordatorios", new ArrayList<>());
    }

    @Test
    @DisplayName("Destroy - No lanza excepciones")
    void testDestroy() {
        assertDoesNotThrow(() -> servlet.destroy());
    }
}