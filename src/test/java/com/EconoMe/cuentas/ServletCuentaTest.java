//package com.EconoMe.cuentas;
//
//import com.EconoMe.cuentas.dao.DAOCuenta;
//import com.EconoMe.cuentas.modelos.Cuenta;
//import com.EconoMe.cuentas.modelos.ServicioCuenta;
//import com.EconoMe.cuentas.modelos.TipoCuenta;
//import com.EconoMe.cuentas.controladores.ServletCuenta;
//
//import jakarta.servlet.RequestDispatcher;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ServletCuentaTest {
//
//    @Mock
//    private HttpServletRequest request;
//
//    @Mock
//    private HttpServletResponse response;
//
//    @Mock
//    private HttpSession session;
//
//    @Mock
//    private RequestDispatcher dispatcher;
//
//    @Mock
//    private DAOCuenta daoCuenta;
//
//    @Mock
//    private ServicioCuenta servicioCuenta;
//
//    private ServletCuenta servlet;
//
//    @BeforeEach
//    void setUp() {
//        servlet = new ServletCuenta();
//        // Inyectar los mocks manualmente
//        servlet = new ServletCuenta() {
//            @Override
//            public void init() {
//                // No llamamos super.init() para inyectar nuestros mocks
//            }
//        };
//        // Usando reflexión para inyectar los mocks
//        try {
//            java.lang.reflect.Field daoCuentaField = ServletCuenta.class.getDeclaredField("daoCuenta");
//            daoCuentaField.setAccessible(true);
//            daoCuentaField.set(servlet, daoCuenta);
//
//            java.lang.reflect.Field servicioCuentaField = ServletCuenta.class.getDeclaredField("servicioCuenta");
//            servicioCuentaField.setAccessible(true);
//            servicioCuentaField.set(servlet, servicioCuenta);
//        } catch (Exception e) {
//            throw new RuntimeException("Error al inyectar mocks", e);
//        }
//    }
//
//    // ========================================
//    // TESTS para GET /cuentas - Listar cuentas
//    // ========================================
//
//    @Test
//    void givenExistingAccounts_whenListAccounts_thenForwardToViewWithAccountsList()
//            throws ServletException, IOException {
//        // Given
//        List<Cuenta> cuentas = Arrays.asList(
//                new Cuenta("Ahorros", TipoCuenta.AHORROS, 1000.0),
//                new Cuenta("Corriente", TipoCuenta.CORRIENTE, 500.0)
//        );
//
//        when(request.getPathInfo()).thenReturn(null);
//        when(request.getSession(false)).thenReturn(null);
//        when(daoCuenta.listar()).thenReturn(cuentas);
//        when(request.getRequestDispatcher("/cuenta/VistaCuentas.jsp")).thenReturn(dispatcher);
//
//        // When
//        servlet.doGet(request, response);
//
//        // Then
//        verify(daoCuenta).listar();
//        verify(request).setAttribute("cuentas", cuentas);
//        verify(dispatcher).forward(request, response);
//    }
//
//    @Test
//    void givenNoAccounts_whenListAccounts_thenForwardToViewWithEmptyList()
//            throws ServletException, IOException {
//        // Given
//        List<Cuenta> cuentasVacias = new ArrayList<>();
//
//        when(request.getPathInfo()).thenReturn(null);
//        when(request.getSession(false)).thenReturn(null);
//        when(daoCuenta.listar()).thenReturn(cuentasVacias);
//        when(request.getRequestDispatcher("/cuenta/VistaCuentas.jsp")).thenReturn(dispatcher);
//
//        // When
//        servlet.doGet(request, response);
//
//        // Then
//        verify(daoCuenta).listar();
//        verify(request).setAttribute("cuentas", cuentasVacias);
//        verify(dispatcher).forward(request, response);
//    }
//
//    @Test
//    void givenDatabaseError_whenListAccounts_thenForwardWithErrorMessage()
//            throws ServletException, IOException {
//        // Given
//        when(request.getPathInfo()).thenReturn(null);
//        when(request.getSession(false)).thenReturn(null);
//        when(request.getSession()).thenReturn(session);
//        when(daoCuenta.listar()).thenThrow(new RuntimeException("Database connection error"));
//        when(request.getRequestDispatcher("/cuenta/VistaCuentas.jsp")).thenReturn(dispatcher);
//
//        // When
//        servlet.doGet(request, response);
//
//        // Then
//        verify(session).setAttribute(eq("mensajeError"), contains("Error al cargar las cuentas"));
//        verify(dispatcher).forward(request, response);
//    }
//
//    // ========================================
//    // TESTS para GET /cuentas/nuevo - Mostrar formulario
//    // ========================================
//
//    @Test
//    void givenNewAccountRequest_whenShowForm_thenForwardToFormWithAccountTypes()
//            throws ServletException, IOException {
//        // Given
//        when(request.getPathInfo()).thenReturn("/nuevo");
//        when(request.getSession(false)).thenReturn(null);
//        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);
//
//        // When
//        servlet.doGet(request, response);
//
//        // Then
//        verify(request).setAttribute(eq("tipos"), any(TipoCuenta[].class));
//        verify(dispatcher).forward(request, response);
//    }
//
//    // ========================================
//    // TESTS para POST /cuentas - Crear cuenta
//    // HU02 - Escenario 1: Creación exitosa
//    // ========================================
//
//    @Test
//    void givenValidAccountData_whenCreateAccount_thenRedirectWithSuccessMessage()
//            throws ServletException, IOException {
//        // Given
//        when(request.getPathInfo()).thenReturn(null);
//        when(request.getParameter("nombre")).thenReturn("Ahorros Principal");
//        when(request.getParameter("tipo")).thenReturn("AHORROS");
//        when(request.getParameter("monto")).thenReturn("1000.00");
//        when(request.getSession()).thenReturn(session);
//        when(request.getContextPath()).thenReturn("/EconoMe");
//
//        doNothing().when(servicioCuenta).crearCuenta(any(Cuenta.class));
//
//        // When
//        servlet.doPost(request, response);
//
//        // Then
//        verify(servicioCuenta).crearCuenta(any(Cuenta.class));
//        verify(session).setAttribute("mensajeExito", "Cuenta creada exitosamente");
//        verify(response).sendRedirect("/EconoMe/cuentas");
//    }
//
//    // ========================================
//    // HU02 - Escenario 2: Campos vacíos
//    // ========================================
//
//    @Test
//    void givenEmptyName_whenCreateAccount_thenShowErrorAndReturnToForm()
//            throws ServletException, IOException {
//        // Given
//        when(request.getPathInfo()).thenReturn(null);
//        when(request.getParameter("nombre")).thenReturn("");
//        when(request.getParameter("tipo")).thenReturn("AHORROS");
//        when(request.getParameter("monto")).thenReturn("1000.00");
//        when(request.getSession()).thenReturn(session);
//        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);
//
//        // When
//        servlet.doPost(request, response);
//
//        // Then
//        verify(session).setAttribute("mensajeError", "Rellena este campo: Nombre de la Cuenta");
//        verify(dispatcher).forward(request, response);
//        verify(servicioCuenta, never()).crearCuenta(any());
//    }
//
//    @Test
//    void givenEmptyType_whenCreateAccount_thenShowErrorAndReturnToForm()
//            throws ServletException, IOException {
//        // Given
//        when(request.getPathInfo()).thenReturn(null);
//        when(request.getParameter("nombre")).thenReturn("Ahorros");
//        when(request.getParameter("tipo")).thenReturn("");
//        when(request.getParameter("monto")).thenReturn("1000.00");
//        when(request.getSession()).thenReturn(session);
//        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);
//
//        // When
//        servlet.doPost(request, response);
//
//        // Then
//        verify(session).setAttribute("mensajeError", "Rellena este campo: Tipo de Cuenta");
//        verify(dispatcher).forward(request, response);
//        verify(servicioCuenta, never()).crearCuenta(any());
//    }
//
//    @Test
//    void givenEmptyAmount_whenCreateAccount_thenShowErrorAndReturnToForm()
//            throws ServletException, IOException {
//        // Given
//        when(request.getPathInfo()).thenReturn(null);
//        when(request.getParameter("nombre")).thenReturn("Ahorros");
//        when(request.getParameter("tipo")).thenReturn("AHORROS");
//        when(request.getParameter("monto")).thenReturn("");
//        when(request.getSession()).thenReturn(session);
//        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);
//
//        // When
//        servlet.doPost(request, response);
//
//        // Then
//        verify(session).setAttribute("mensajeError", "Rellena este campo: Saldo Inicial");
//        verify(dispatcher).forward(request, response);
//        verify(servicioCuenta, never()).crearCuenta(any());
//    }
//
//    // ========================================
//    // HU02 - Escenario 3: Cuenta duplicada
//    // ========================================
//
//    @Test
//    void givenDuplicateAccount_whenCreateAccount_thenShowErrorAndReturnToForm()
//            throws ServletException, IOException {
//        // Given
//        when(request.getPathInfo()).thenReturn(null);
//        when(request.getParameter("nombre")).thenReturn("Ahorros");
//        when(request.getParameter("tipo")).thenReturn("AHORROS");
//        when(request.getParameter("monto")).thenReturn("1000.00");
//        when(request.getSession()).thenReturn(session);
//        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);
//
//        doThrow(new IllegalStateException("Ya existe una cuenta con el nombre 'Ahorros' y tipo AHORROS"))
//                .when(servicioCuenta).crearCuenta(any(Cuenta.class));
//
//        // When
//        servlet.doPost(request, response);
//
//        // Then
//        verify(session).setAttribute("mensajeError", "Ya existe una cuenta del mismo nombre y tipo");
//        verify(dispatcher).forward(request, response);
//    }
//
//    // ========================================
//    // Tests adicionales: Validaciones de monto
//    // ========================================
//
//    @Test
//    void givenInvalidAmount_whenCreateAccount_thenShowErrorAndReturnToForm()
//            throws ServletException, IOException {
//        // Given
//        when(request.getPathInfo()).thenReturn(null);
//        when(request.getParameter("nombre")).thenReturn("Ahorros");
//        when(request.getParameter("tipo")).thenReturn("AHORROS");
//        when(request.getParameter("monto")).thenReturn("abc"); // Monto inválido
//        when(request.getSession()).thenReturn(session);
//        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);
//
//        // When
//        servlet.doPost(request, response);
//
//        // Then
//        verify(session).setAttribute("mensajeError", "El monto debe ser un número válido");
//        verify(dispatcher).forward(request, response);
//        verify(servicioCuenta, never()).crearCuenta(any());
//    }
//
//    @Test
//    void givenNegativeAmount_whenCreateAccount_thenShowErrorAndReturnToForm()
//            throws ServletException, IOException {
//        // Given
//        when(request.getPathInfo()).thenReturn(null);
//        when(request.getParameter("nombre")).thenReturn("Ahorros");
//        when(request.getParameter("tipo")).thenReturn("AHORROS");
//        when(request.getParameter("monto")).thenReturn("-100.00");
//        when(request.getSession()).thenReturn(session);
//        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);
//
//        doThrow(new IllegalArgumentException("El saldo debe ser mayor a 0.01"))
//                .when(servicioCuenta).crearCuenta(any(Cuenta.class));
//
//        // When
//        servlet.doPost(request, response);
//
//        // Then
//        verify(session).setAttribute("mensajeError", "Monto inválido. Debe ser mayor a cero");
//        verify(dispatcher).forward(request, response);
//    }
//
//    @Test
//    void givenZeroAmount_whenCreateAccount_thenShowErrorAndReturnToForm()
//            throws ServletException, IOException {
//        // Given
//        when(request.getPathInfo()).thenReturn(null);
//        when(request.getParameter("nombre")).thenReturn("Ahorros");
//        when(request.getParameter("tipo")).thenReturn("AHORROS");
//        when(request.getParameter("monto")).thenReturn("0.00");
//        when(request.getSession()).thenReturn(session);
//        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);
//
//        doThrow(new IllegalArgumentException("El saldo debe ser mayor a 0.01"))
//                .when(servicioCuenta).crearCuenta(any(Cuenta.class));
//
//        // When
//        servlet.doPost(request, response);
//
//        // Then
//        verify(session).setAttribute("mensajeError", "Monto inválido. Debe ser mayor a cero");
//        verify(dispatcher).forward(request, response);
//    }
//
//    // ========================================
//    // Test de preservación de datos en caso de error
//    // ========================================
//
//    @Test
//    void givenErrorOnCreate_whenReturningToForm_thenPreserveEnteredData()
//            throws ServletException, IOException {
//        // Given
//        String nombre = "Cuenta Test";
//        String tipo = "EFECTIVO";
//        String monto = "500.00";
//
//        when(request.getPathInfo()).thenReturn(null);
//        when(request.getParameter("nombre")).thenReturn(nombre);
//        when(request.getParameter("tipo")).thenReturn(tipo);
//        when(request.getParameter("monto")).thenReturn(monto);
//        when(request.getSession()).thenReturn(session);
//        when(request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp")).thenReturn(dispatcher);
//
//        doThrow(new IllegalStateException("Ya existe una cuenta"))
//                .when(servicioCuenta).crearCuenta(any(Cuenta.class));
//
//        // When
//        servlet.doPost(request, response);
//
//        // Then
//        verify(request).setAttribute("nombreIngresado", nombre);
//        verify(request).setAttribute("tipoIngresado", tipo);
//        verify(request).setAttribute("montoIngresado", monto);
//        verify(request).setAttribute(eq("tipos"), any(TipoCuenta[].class));
//        verify(dispatcher).forward(request, response);
//    }
//}