package com.EconoMe.plantillas.controladores;

import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.cuentas.modelos.TipoCuenta;
import com.EconoMe.cuentas.servicios.ServicioCuenta;
import com.EconoMe.movimientos.modelos.CategoriaGasto;
import com.EconoMe.movimientos.modelos.CategoriaIngreso;
import com.EconoMe.movimientos.modelos.Ingreso;
import com.EconoMe.movimientos.modelos.Movimiento;
import com.EconoMe.plantillas.modelos.Plantilla;
import com.EconoMe.plantillas.servicios.ServicioPlantilla;

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

class ServletPlantillaIntegrationTest {

    private ServletPlantilla servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private ServicioPlantilla servicioPlantilla;

    @Mock
    private ServicioCuenta servicioCuenta;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        servlet = new ServletPlantilla();

        // Inyectar mocks usando reflection
        java.lang.reflect.Field servicioPlantillaField = ServletPlantilla.class.getDeclaredField("servicioPlantilla");
        servicioPlantillaField.setAccessible(true);
        servicioPlantillaField.set(servlet, servicioPlantilla);

        java.lang.reflect.Field servicioCuentaField = ServletPlantilla.class.getDeclaredField("servicioCuenta");
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
    @DisplayName("GET /plantillas/nuevo - Mostrar formulario sin plantilla duplicada")
    void testMostrarFormularioNuevo() throws ServletException, IOException {
        List<Cuenta> cuentas = Arrays.asList(
                new Cuenta("Efectivo", TipoCuenta.EFECTIVO, 1000.0)
        );

        when(request.getServletPath()).thenReturn("/plantillas/nuevo");
        when(servicioCuenta.listarTodas()).thenReturn(cuentas);
        when(session.getAttribute("plantillaDuplicada")).thenReturn(null);
        when(request.getRequestDispatcher("/plantillas/VistaFormPlantilla.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(servicioCuenta).listarTodas();
        verify(request).setAttribute(eq("cuentas"), eq(cuentas));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET /plantillas/nuevo - Mostrar formulario con plantilla duplicada en sesión")
    void testMostrarFormularioConPlantillaDuplicada() throws ServletException, IOException {
        List<Cuenta> cuentas = Arrays.asList(
                new Cuenta("Efectivo", TipoCuenta.EFECTIVO, 1000.0)
        );
        Plantilla plantillaDuplicada = new Plantilla();
        plantillaDuplicada.setNombre("Plantilla Duplicada");

        when(request.getServletPath()).thenReturn("/plantillas/nuevo");
        when(servicioCuenta.listarTodas()).thenReturn(cuentas);
        when(session.getAttribute("plantillaDuplicada")).thenReturn(plantillaDuplicada);
        when(request.getRequestDispatcher("/plantillas/VistaFormPlantilla.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(session).removeAttribute("plantillaDuplicada");
        verify(request).setAttribute(eq("plantilla"), eq(plantillaDuplicada));
        verify(dispatcher).forward(request, response);
    }

    // ===== PRUEBAS GET - FORMULARIO EDITAR =====

    @Test
    @DisplayName("GET /plantillas/editar - Mostrar formulario de edición exitosamente")
    void testMostrarFormularioEdicionExitoso() throws ServletException, IOException {
        Plantilla plantilla = new Plantilla();
        plantilla.setId(1L);
        plantilla.setNombre("Plantilla Test");
        List<Cuenta> cuentas = Arrays.asList(
                new Cuenta("Efectivo", TipoCuenta.EFECTIVO, 1000.0)
        );

        when(request.getServletPath()).thenReturn("/plantillas/editar");
        when(request.getParameter("id")).thenReturn("1");
        when(servicioPlantilla.buscarPorId(1L)).thenReturn(plantilla);
        when(servicioCuenta.listarTodas()).thenReturn(cuentas);
        when(request.getRequestDispatcher("/plantillas/VistaFormPlantilla.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(servicioPlantilla).buscarPorId(1L);
        verify(request).setAttribute(eq("plantilla"), eq(plantilla));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET /plantillas/editar - ID no proporcionado")
    void testMostrarFormularioEdicionSinId() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/editar");
        when(request.getParameter("id")).thenReturn(null);

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
        verify(servicioPlantilla, never()).buscarPorId(anyLong());
    }

    @Test
    @DisplayName("GET /plantillas/editar - ID inválido")
    void testMostrarFormularioEdicionIdInvalido() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/editar");
        when(request.getParameter("id")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
        verify(servicioPlantilla, never()).buscarPorId(anyLong());
    }

    @Test
    @DisplayName("GET /plantillas/editar - Plantilla no encontrada")
    void testMostrarFormularioEdicionPlantillaNoEncontrada() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/editar");
        when(request.getParameter("id")).thenReturn("999");
        when(servicioPlantilla.buscarPorId(999L)).thenReturn(null);

        servlet.doGet(request, response);

        verify(servicioPlantilla).buscarPorId(999L);
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
    }

    // ===== PRUEBAS GET - BUSCAR PLANTILLAS =====

    @Test
    @DisplayName("GET /plantillas/buscar - Buscar sin filtros")
    void testBuscarPlantillasSinFiltros() throws ServletException, IOException {
        List<Plantilla> plantillas = Arrays.asList(
                new Plantilla(),
                new Plantilla()
        );
        List<Cuenta> cuentas = Arrays.asList(
                new Cuenta("Efectivo", TipoCuenta.EFECTIVO, 1000.0)
        );

        when(request.getServletPath()).thenReturn("/plantillas/buscar");
        when(request.getParameter("nombre")).thenReturn(null);
        when(request.getParameter("tipo")).thenReturn(null);
        when(request.getParameter("categoria")).thenReturn(null);
        when(servicioCuenta.listarTodas()).thenReturn(cuentas);
        when(servicioPlantilla.buscarPlantillasConFiltros(null, null, null)).thenReturn(plantillas);
        when(request.getRequestDispatcher("/movimiento/VistaMovimientos.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(servicioPlantilla).buscarPlantillasConFiltros(null, null, null);
        verify(request).setAttribute(eq("plantillas"), eq(plantillas));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET /plantillas/buscar - Buscar con filtros completos")
    void testBuscarPlantillasConFiltros() throws ServletException, IOException {
        String nombre = "Salario";
        String tipo = "INGRESO";
        String categoria = "SALARIO";
        List<Plantilla> plantillas = Arrays.asList(new Plantilla());
        List<Cuenta> cuentas = Arrays.asList(
                new Cuenta("Efectivo", TipoCuenta.EFECTIVO, 1000.0)
        );

        when(request.getServletPath()).thenReturn("/plantillas/buscar");
        when(request.getParameter("nombre")).thenReturn(nombre);
        when(request.getParameter("tipo")).thenReturn(tipo);
        when(request.getParameter("categoria")).thenReturn(categoria);
        when(servicioCuenta.listarTodas()).thenReturn(cuentas);
        when(servicioPlantilla.buscarPlantillasConFiltros(nombre, tipo, categoria)).thenReturn(plantillas);
        when(request.getRequestDispatcher("/movimiento/VistaMovimientos.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(servicioPlantilla).buscarPlantillasConFiltros(nombre, tipo, categoria);
        verify(request).setAttribute(eq("filtroNombre"), eq(nombre));
        verify(request).setAttribute(eq("filtroTipo"), eq(tipo));
        verify(request).setAttribute(eq("filtroCategoria"), eq(categoria));
    }

    @Test
    @DisplayName("GET /plantillas/buscar - Sin cuentas disponibles")
    void testBuscarPlantillasSinCuentas() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/buscar");
        when(servicioCuenta.listarTodas()).thenReturn(new ArrayList<>());
        when(servicioPlantilla.buscarPlantillasConFiltros(any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/movimiento/VistaMovimientos.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
    }

    // ===== PRUEBAS GET - DUPLICAR PLANTILLA =====

    @Test
    @DisplayName("GET /plantillas/duplicar - Duplicar exitosamente")
    void testDuplicarPlantillaExitoso() throws ServletException, IOException {
        Plantilla original = new Plantilla();
        original.setId(1L);
        original.setNombre("Plantilla Original");

        Plantilla copia = new Plantilla();
        copia.setNombre("Copia de Plantilla Original");

        when(request.getServletPath()).thenReturn("/plantillas/duplicar");
        when(request.getParameter("id")).thenReturn("1");
        when(servicioPlantilla.buscarPorId(1L)).thenReturn(original);
        when(servicioPlantilla.duplicarPlantilla(original)).thenReturn(copia);

        servlet.doGet(request, response);

        verify(servicioPlantilla).duplicarPlantilla(original);
        verify(session).setAttribute(eq("plantillaDuplicada"), eq(copia));
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/plantillas/nuevo");
    }

    @Test
    @DisplayName("GET /plantillas/duplicar - ID no proporcionado")
    void testDuplicarPlantillaSinId() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/duplicar");
        when(request.getParameter("id")).thenReturn(null);

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
        verify(servicioPlantilla, never()).duplicarPlantilla(any());
    }

    @Test
    @DisplayName("GET /plantillas/duplicar - Plantilla no encontrada")
    void testDuplicarPlantillaNoEncontrada() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/duplicar");
        when(request.getParameter("id")).thenReturn("999");
        when(servicioPlantilla.buscarPorId(999L)).thenReturn(null);

        servlet.doGet(request, response);

        verify(servicioPlantilla).buscarPorId(999L);
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
        verify(servicioPlantilla, never()).duplicarPlantilla(any());
    }

    // ===== PRUEBAS GET - APLICAR PLANTILLA =====

    @Test
    @DisplayName("GET /plantillas/aplicar - Aplicar exitosamente")
    void testAplicarPlantillaExitoso() throws ServletException, IOException {
        Plantilla plantilla = new Plantilla();
        plantilla.setId(1L);
        plantilla.setNombre("Plantilla Test");

        Movimiento movimiento = new Ingreso();

        when(request.getServletPath()).thenReturn("/plantillas/aplicar");
        when(request.getParameter("id")).thenReturn("1");
        when(servicioPlantilla.buscarPorId(1L)).thenReturn(plantilla);
        when(servicioPlantilla.aplicarPlantilla(plantilla)).thenReturn(movimiento);

        servlet.doGet(request, response);

        verify(servicioPlantilla).aplicarPlantilla(plantilla);
        verify(session).setAttribute(eq("movimientoDesdePlantilla"), eq(movimiento));
        verify(session).setAttribute(eq("plantillaAplicada"), eq("Plantilla Test"));
        verify(response).sendRedirect("/EconoMe/movimientos");
    }

    @Test
    @DisplayName("GET /plantillas/aplicar - ID no proporcionado")
    void testAplicarPlantillaSinId() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/aplicar");
        when(request.getParameter("id")).thenReturn(null);

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
        verify(servicioPlantilla, never()).aplicarPlantilla(any());
    }

    @Test
    @DisplayName("GET /plantillas/aplicar - Plantilla no encontrada")
    void testAplicarPlantillaNoEncontrada() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/aplicar");
        when(request.getParameter("id")).thenReturn("999");
        when(servicioPlantilla.buscarPorId(999L)).thenReturn(null);

        servlet.doGet(request, response);

        verify(servicioPlantilla).buscarPorId(999L);
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
        verify(servicioPlantilla, never()).aplicarPlantilla(any());
    }

    @Test
    @DisplayName("GET /plantillas/aplicar - Error al aplicar plantilla")
    void testAplicarPlantillaConError() throws ServletException, IOException {
        Plantilla plantilla = new Plantilla();
        plantilla.setId(1L);
        plantilla.setNombre("Plantilla Test");

        when(request.getServletPath()).thenReturn("/plantillas/aplicar");
        when(request.getParameter("id")).thenReturn("1");
        when(servicioPlantilla.buscarPorId(1L)).thenReturn(plantilla);
        when(servicioPlantilla.aplicarPlantilla(plantilla))
                .thenThrow(new IllegalStateException("Error al aplicar"));

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
    }

    // ===== PRUEBAS POST - CREAR PLANTILLA =====

    @Test
    @DisplayName("POST /plantillas/nuevo - Crear plantilla de ingreso exitosamente")
    void testCrearPlantillaIngresoExitoso() throws ServletException, IOException {
        Cuenta cuenta = new Cuenta("Efectivo", TipoCuenta.EFECTIVO, 1000.0);
        cuenta.setId(1L);

        when(request.getServletPath()).thenReturn("/plantillas/nuevo");
        when(request.getParameter("nombre")).thenReturn("Salario Mensual");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("3000.50");
        when(request.getParameter("categoria")).thenReturn("SALARIO");
        when(request.getParameter("cuentaId")).thenReturn("1");
        when(servicioCuenta.buscarCuenta(1L)).thenReturn(cuenta);

        servlet.doPost(request, response);

        verify(servicioPlantilla).crearPlantilla(argThat(plantilla ->
                plantilla.getNombre().equals("Salario Mensual") &&
                        plantilla.getTipo().equals("INGRESO") &&
                        plantilla.getMonto() == 3000.50 &&
                        plantilla.getCategoria().equals("SALARIO") &&
                        plantilla.getCuenta().equals(cuenta)
        ));
        verify(session).setAttribute(eq("mensajes"), any());
        verify(session).removeAttribute("plantillaDuplicada");
        verify(response).sendRedirect("/EconoMe/movimientos");
    }

    @Test
    @DisplayName("POST /plantillas/nuevo - Crear plantilla de gasto exitosamente")
    void testCrearPlantillaGastoExitoso() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/nuevo");
        when(request.getParameter("nombre")).thenReturn("Renta Mensual");
        when(request.getParameter("tipo")).thenReturn("GASTO");
        when(request.getParameter("monto")).thenReturn("800.00");
        when(request.getParameter("categoria")).thenReturn("VIVIENDA");
        when(request.getParameter("cuentaId")).thenReturn("");

        servlet.doPost(request, response);

        verify(servicioPlantilla).crearPlantilla(argThat(plantilla ->
                plantilla.getNombre().equals("Renta Mensual") &&
                        plantilla.getTipo().equals("GASTO") &&
                        plantilla.getMonto() == 800.00 &&
                        plantilla.getCategoria().equals("VIVIENDA") &&
                        plantilla.getCuenta() == null
        ));
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
    }

    @Test
    @DisplayName("POST /plantillas/nuevo - Nombre vacío")
    void testCrearPlantillaConNombreVacio() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/nuevo");
        when(request.getParameter("nombre")).thenReturn("");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getParameter("categoria")).thenReturn("SALARIO");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/plantillas/nuevo");
        verify(servicioPlantilla, never()).crearPlantilla(any());
    }

    @Test
    @DisplayName("POST /plantillas/nuevo - Nombre nulo")
    void testCrearPlantillaConNombreNulo() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/nuevo");
        when(request.getParameter("nombre")).thenReturn(null);
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getParameter("categoria")).thenReturn("SALARIO");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/plantillas/nuevo");
        verify(servicioPlantilla, never()).crearPlantilla(any());
    }

    @Test
    @DisplayName("POST /plantillas/nuevo - Tipo vacío")
    void testCrearPlantillaConTipoVacio() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/nuevo");
        when(request.getParameter("nombre")).thenReturn("Test");
        when(request.getParameter("tipo")).thenReturn("");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getParameter("categoria")).thenReturn("SALARIO");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/plantillas/nuevo");
        verify(servicioPlantilla, never()).crearPlantilla(any());
    }

    @Test
    @DisplayName("POST /plantillas/nuevo - Monto inválido (no numérico)")
    void testCrearPlantillaConMontoInvalido() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/nuevo");
        when(request.getParameter("nombre")).thenReturn("Test");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("abc");
        when(request.getParameter("categoria")).thenReturn("SALARIO");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/plantillas/nuevo");
        verify(servicioPlantilla, never()).crearPlantilla(any());
    }

    @Test
    @DisplayName("POST /plantillas/nuevo - Categoría inválida")
    void testCrearPlantillaConCategoriaInvalida() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/nuevo");
        when(request.getParameter("nombre")).thenReturn("Test");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getParameter("categoria")).thenReturn("CATEGORIA_INVALIDA");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/plantillas/nuevo");
        verify(servicioPlantilla, never()).crearPlantilla(any());
    }

    @Test
    @DisplayName("POST /plantillas/nuevo - Cuenta no encontrada")
    void testCrearPlantillaConCuentaNoEncontrada() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/nuevo");
        when(request.getParameter("nombre")).thenReturn("Test");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getParameter("categoria")).thenReturn("SALARIO");
        when(request.getParameter("cuentaId")).thenReturn("999");
        when(servicioCuenta.buscarCuenta(999L)).thenReturn(null);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/plantillas/nuevo");
        verify(servicioPlantilla, never()).crearPlantilla(any());
    }

    @Test
    @DisplayName("POST /plantillas/nuevo - ID de cuenta inválido")
    void testCrearPlantillaConIdCuentaInvalido() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/nuevo");
        when(request.getParameter("nombre")).thenReturn("Test");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getParameter("categoria")).thenReturn("SALARIO");
        when(request.getParameter("cuentaId")).thenReturn("abc");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/plantillas/nuevo");
        verify(servicioPlantilla, never()).crearPlantilla(any());
    }

    // ===== PRUEBAS POST - EDITAR PLANTILLA =====

    @Test
    @DisplayName("POST /plantillas/editar - Editar exitosamente")
    void testEditarPlantillaExitoso() throws ServletException, IOException {
        Plantilla plantillaExistente = new Plantilla();
        plantillaExistente.setId(1L);
        plantillaExistente.setNombre("Nombre Original");

        when(request.getServletPath()).thenReturn("/plantillas/editar");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("nombre")).thenReturn("Nombre Actualizado");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("2500.00");
        when(request.getParameter("categoria")).thenReturn("SALARIO");
        when(request.getParameter("cuentaId")).thenReturn("");
        when(servicioPlantilla.buscarPorId(1L)).thenReturn(plantillaExistente);

        servlet.doPost(request, response);

        verify(servicioPlantilla).actualizarPlantilla(argThat(plantilla ->
                plantilla.getId().equals(1L) &&
                        plantilla.getNombre().equals("Nombre Actualizado") &&
                        plantilla.getMonto() == 2500.00
        ));
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
    }

    @Test
    @DisplayName("POST /plantillas/editar - Plantilla no encontrada")
    void testEditarPlantillaNoEncontrada() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/editar");
        when(request.getParameter("id")).thenReturn("999");
        when(request.getParameter("nombre")).thenReturn("Test");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getParameter("categoria")).thenReturn("SALARIO");
        when(servicioPlantilla.buscarPorId(999L)).thenReturn(null);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
        verify(servicioPlantilla, never()).actualizarPlantilla(any());
    }

    @Test
    @DisplayName("POST /plantillas/editar - Campos obligatorios vacíos")
    void testEditarPlantillaCamposVacios() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/editar");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("nombre")).thenReturn("");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getParameter("categoria")).thenReturn("SALARIO");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/plantillas/editar?id=1");
        verify(servicioPlantilla, never()).actualizarPlantilla(any());
    }

    // ===== PRUEBAS POST - ELIMINAR PLANTILLA =====

    @Test
    @DisplayName("POST /plantillas/eliminar - Eliminar exitosamente")
    void testEliminarPlantillaExitoso() throws ServletException, IOException {
        Plantilla plantilla = new Plantilla();
        plantilla.setId(1L);
        plantilla.setNombre("Plantilla a Eliminar");

        when(request.getServletPath()).thenReturn("/plantillas/eliminar");
        when(request.getParameter("id")).thenReturn("1");
        when(servicioPlantilla.buscarPorId(1L)).thenReturn(plantilla);

        servlet.doPost(request, response);

        verify(servicioPlantilla).eliminarPlantilla(1L);
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
    }

    @Test
    @DisplayName("POST /plantillas/eliminar - ID no proporcionado")
    void testEliminarPlantillaSinId() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/eliminar");
        when(request.getParameter("id")).thenReturn(null);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
        verify(servicioPlantilla, never()).eliminarPlantilla(anyLong());
    }

    @Test
    @DisplayName("POST /plantillas/eliminar - Plantilla no encontrada")
    void testEliminarPlantillaNoEncontrada() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/eliminar");
        when(request.getParameter("id")).thenReturn("999");
        when(servicioPlantilla.buscarPorId(999L)).thenReturn(null);

        servlet.doPost(request, response);

        verify(servicioPlantilla).buscarPorId(999L);
        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
        verify(servicioPlantilla, never()).eliminarPlantilla(anyLong());
    }

    @Test
    @DisplayName("POST /plantillas/eliminar - Error al eliminar")
    void testEliminarPlantillaConError() throws ServletException, IOException {
        Plantilla plantilla = new Plantilla();
        plantilla.setId(1L);
        plantilla.setNombre("Test");

        when(request.getServletPath()).thenReturn("/plantillas/eliminar");
        when(request.getParameter("id")).thenReturn("1");
        when(servicioPlantilla.buscarPorId(1L)).thenReturn(plantilla);
        doThrow(new RuntimeException("Error de BD"))
                .when(servicioPlantilla).eliminarPlantilla(1L);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
    }

    // ===== PRUEBAS DE CASOS EDGE =====

    @Test
    @DisplayName("GET - Path desconocido redirige a /movimientos")
    void testPathDesconocido() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/desconocido");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/EconoMe/movimientos");
    }

    @Test
    @DisplayName("POST - Path desconocido redirige a /movimientos")
    void testPostPathDesconocido() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/desconocido");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/EconoMe/movimientos");
    }

    @Test
    @DisplayName("POST /plantillas/nuevo - Error del servicio al crear")
    void testCrearPlantillaConErrorServicio() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/nuevo");
        when(request.getParameter("nombre")).thenReturn("Test");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getParameter("categoria")).thenReturn("SALARIO");
        when(request.getParameter("cuentaId")).thenReturn("");
        doThrow(new RuntimeException("Error de BD"))
                .when(servicioPlantilla).crearPlantilla(any());

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/plantillas/nuevo");
    }

    @Test
    @DisplayName("POST /plantillas/editar - Error del servicio al actualizar")
    void testEditarPlantillaConErrorServicio() throws ServletException, IOException {
        Plantilla plantilla = new Plantilla();
        plantilla.setId(1L);

        when(request.getServletPath()).thenReturn("/plantillas/editar");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("nombre")).thenReturn("Test");
        when(request.getParameter("tipo")).thenReturn("INGRESO");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getParameter("categoria")).thenReturn("SALARIO");
        when(request.getParameter("cuentaId")).thenReturn("");
        when(servicioPlantilla.buscarPorId(1L)).thenReturn(plantilla);
        doThrow(new RuntimeException("Error de BD"))
                .when(servicioPlantilla).actualizarPlantilla(any());

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/plantillas/editar?id=1");
    }

    @Test
    @DisplayName("GET /plantillas/buscar - Error al buscar plantillas")
    void testBuscarPlantillasConError() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/buscar");
        when(servicioCuenta.listarTodas()).thenThrow(new RuntimeException("Error de BD"));

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/movimientos");
    }

    @Test
    @DisplayName("POST - Crear plantilla con tipo inválido")
    void testCrearPlantillaConTipoInvalido() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/plantillas/nuevo");
        when(request.getParameter("nombre")).thenReturn("Test");
        when(request.getParameter("tipo")).thenReturn("TIPO_INVALIDO");
        when(request.getParameter("monto")).thenReturn("1000");
        when(request.getParameter("categoria")).thenReturn("SALARIO");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("mensajes"), any());
        verify(response).sendRedirect("/EconoMe/plantillas/nuevo");
        verify(servicioPlantilla, never()).crearPlantilla(any());
    }
}