package com.EconoMe.listas_de_compras.controladores;

import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.cuentas.servicios.ServicioCuenta;
import com.EconoMe.listas_de_compras.modelos.ArticuloDeCompras;
import com.EconoMe.listas_de_compras.modelos.EstadoCompra;
import com.EconoMe.listas_de_compras.modelos.ListaDeCompras;
import com.EconoMe.listas_de_compras.servicios.ServicioArticuloDeCompras;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServletListaComprasTest {

    @Mock
    private ServicioArticuloDeCompras servicioArticulos;

    @Mock
    private ServicioCuenta servicioCuenta;

    private ServletListaCompras servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        servlet = new ServletListaCompras() {
            @Override
            public void init() {
                try {
                    java.lang.reflect.Field fieldArticulos = ServletListaCompras.class
                            .getDeclaredField("servicioArticulos");
                    fieldArticulos.setAccessible(true);
                    fieldArticulos.set(this, servicioArticulos);

                    java.lang.reflect.Field fieldCuenta = ServletListaCompras.class
                            .getDeclaredField("servicioCuenta");
                    fieldCuenta.setAccessible(true);
                    fieldCuenta.set(this, servicioCuenta);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        servlet.init();
    }

    // ========== TESTS DOGET ==========

    @Test
    void testDoGet_MostrarFormularioNuevo() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/nuevo");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoGet_ListarListas() throws Exception {
        // Arrange
        List<ListaDeCompras> listas = crearListasDePrueba();
        when(request.getPathInfo()).thenReturn("/");
        when(servicioArticulos.obtenerListas()).thenReturn(listas);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(request).setAttribute(eq("listas"), any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoGet_MostrarDetalle_ListaValida() throws Exception {
        // Arrange
        ListaDeCompras lista = crearListaDePrueba(1L, "Lista Test");
        when(request.getPathInfo()).thenReturn("/detalle");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(servicioArticulos.buscarListaPorId(1L)).thenReturn(lista);
        when(servicioCuenta.listarTodas()).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(request).setAttribute(eq("lista"), eq(lista));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoGet_MostrarDetalle_IdVacio() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/detalle");
        when(request.getParameter("id")).thenReturn("");
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/EconoMe/listas");
    }

    @Test
    void testDoGet_MostrarDetalle_IdInvalido() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/detalle");
        when(request.getParameter("id")).thenReturn("abc");
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/EconoMe/listas");
    }

    @Test
    void testDoGet_MostrarDetalle_ListaNoExiste() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/detalle");
        when(request.getParameter("id")).thenReturn("999");
        when(request.getSession()).thenReturn(session);
        when(servicioArticulos.buscarListaPorId(999L)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/EconoMe/listas");
    }

    // ========== TESTS DOPOST ==========

    @Test
    void testDoPost_CrearLista_Exitoso() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/crear");
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("nombre")).thenReturn("Mi Lista");
        when(servicioArticulos.registrarListaDeCompras("Mi Lista")).thenReturn(true);
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(response).sendRedirect("/EconoMe/listas");
    }

    @Test
    void testDoPost_CrearLista_NombreVacio() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/crear");
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("nombre")).thenReturn("");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoPost_AgregarArticulo_Exitoso() throws Exception {
        // Arrange
        ListaDeCompras lista = crearListaDePrueba(1L, "Lista Test");
        when(request.getPathInfo()).thenReturn("/agregarItem");
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("idLista")).thenReturn("1");
        when(request.getParameter("nombre")).thenReturn("Articulo Test");
        when(request.getParameter("precio")).thenReturn("10.5");
        when(servicioArticulos.buscarListaPorId(1L)).thenReturn(lista);
        when(servicioArticulos.registrarArticuloDeCompras(anyString(), anyDouble(), any())).thenReturn(true);
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(response).sendRedirect(contains("/listas/detalle?id=1"));
    }

    @Test
    void testDoPost_AgregarArticulo_NombreVacio() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/agregarItem");
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("idLista")).thenReturn("1");
        when(request.getParameter("nombre")).thenReturn("");
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(response).sendRedirect(contains("/listas/detalle?id=1"));
    }

    @Test
    void testDoPost_AgregarArticulo_PrecioVacio() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/agregarItem");
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("idLista")).thenReturn("1");
        when(request.getParameter("nombre")).thenReturn("Articulo");
        when(request.getParameter("precio")).thenReturn("");
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(response).sendRedirect(contains("/listas/detalle?id=1"));
    }

    @Test
    void testDoPost_AgregarArticulo_PrecioNegativo() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/agregarItem");
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("idLista")).thenReturn("1");
        when(request.getParameter("nombre")).thenReturn("Articulo");
        when(request.getParameter("precio")).thenReturn("-5.0");
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(response).sendRedirect(contains("/listas/detalle?id=1"));
    }

    @Test
    void testDoPost_AgregarArticulo_ListaNoExiste() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/agregarItem");
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("idLista")).thenReturn("999");
        when(request.getParameter("nombre")).thenReturn("Articulo");
        when(request.getParameter("precio")).thenReturn("10.0");
        when(servicioArticulos.buscarListaPorId(999L)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(response).sendRedirect(contains("/listas"));
    }

    @Test
    void testDoPost_EliminarArticulo() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/eliminarItem");
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("idItem")).thenReturn("1");
        when(request.getParameter("idLista")).thenReturn("1");
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(servicioArticulos).eliminarArticulo(1L);
        verify(response).sendRedirect(contains("/listas/detalle?id=1"));
    }

    @Test
    void testDoPost_MarcarArticuloComprado() throws Exception {
        // Arrange
        ArticuloDeCompras articulo = new ArticuloDeCompras("Test", 10.0, EstadoCompra.PENDIENTE, new ListaDeCompras());
        when(request.getPathInfo()).thenReturn("/marcarComprado");
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("idItem")).thenReturn("1");
        when(request.getParameter("idLista")).thenReturn("1");
        when(request.getParameter("comprado")).thenReturn("true");
        when(servicioArticulos.buscarArticuloPorId(1L)).thenReturn(articulo);
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(servicioArticulos).actualizarArticulo(articulo);
        verify(response).sendRedirect(contains("/listas/detalle?id=1"));
    }

    @Test
    void testDoPost_MarcarArticuloNoComprado() throws Exception {
        // Arrange
        ArticuloDeCompras articulo = new ArticuloDeCompras("nombre", 2.0, EstadoCompra.PENDIENTE, new ListaDeCompras());
        articulo.setEstado(EstadoCompra.COMPLETADA);
        when(request.getPathInfo()).thenReturn("/marcarComprado");
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("idItem")).thenReturn("1");
        when(request.getParameter("idLista")).thenReturn("1");
        when(request.getParameter("comprado")).thenReturn("false");
        when(servicioArticulos.buscarArticuloPorId(1L)).thenReturn(articulo);
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(servicioArticulos).actualizarArticulo(articulo);
        verify(response).sendRedirect(contains("/listas/detalle?id=1"));
    }

    // ========== HELPERS ==========

    private List<ListaDeCompras> crearListasDePrueba() {
        ListaDeCompras lista1 = crearListaDePrueba(1L, "Lista 1");
        ListaDeCompras lista2 = crearListaDePrueba(2L, "Lista 2");
        return Arrays.asList(lista1, lista2);
    }

    private ListaDeCompras crearListaDePrueba(Long id, String nombre) {
        ListaDeCompras lista = new ListaDeCompras();
        lista.setId(id);
        lista.setNombre(nombre);
        lista.setArticulos(new ArrayList<>());

        // Agregar algunos artículos
        ArticuloDeCompras articulo1 = new ArticuloDeCompras("Item 1", 10.0, EstadoCompra.PENDIENTE, new ListaDeCompras());
        ArticuloDeCompras articulo2 = new ArticuloDeCompras("Item 2", 20.0, EstadoCompra.PENDIENTE, new ListaDeCompras());
        lista.getArticulos().add(articulo1);
        lista.getArticulos().add(articulo2);

        return lista;
    }
}