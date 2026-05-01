package com.EconoMe.listas_de_compras.controladores;

import com.EconoMe.comun.mensajes.MensajeUtil;
import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.cuentas.servicios.ServicioCuenta;
import com.EconoMe.listas_de_compras.modelos.ArticuloDeCompras;
import com.EconoMe.listas_de_compras.modelos.EstadoCompra;
import com.EconoMe.listas_de_compras.modelos.ListaDeCompras;
import com.EconoMe.listas_de_compras.servicios.ServicioArticuloDeCompras;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/listas/*")
public class ServletListaCompras extends HttpServlet {

    private ServicioArticuloDeCompras servicioArticulos;
    private ServicioCuenta servicioCuenta;

    @Override
    public void init() {
        this.servicioArticulos = new ServicioArticuloDeCompras();
        this.servicioCuenta = new ServicioCuenta();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getPathInfo();
        if (action == null) {
            action = "/";
        }

        switch (action) {
            case "/nuevo":
                mostrarFormularioNuevaLista(request, response);
                break;
            case "/detalle":
                mostrarDetalleLista(request, response);
                break;
            default:
                listarListas(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String action = request.getPathInfo();

        if ("/crear".equals(action)) {
            crearLista(request, response);
        } else if ("/agregarItem".equals(action)) {
            agregarArticulo(request, response);
        } else if ("/eliminarItem".equals(action)) {
            eliminarArticulo(request, response);
        } else if ("/marcarComprado".equals(action)) {
            marcarArticuloComprado(request, response);
        }
    }

    // ========== MÉTODOS GET ==========

    private void mostrarFormularioNuevaLista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MensajeUtil.obtenerYLimpiarMensajes(request);
        request.getRequestDispatcher("/lista_compras/VistaFormularioLista.jsp").forward(request, response);
    }

    private void mostrarDetalleLista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String listaIdStr = request.getParameter("id");

        if (listaIdStr == null || listaIdStr.trim().isEmpty()) {
            MensajeUtil.agregarError(session, "ID de lista no válido");
            response.sendRedirect(request.getContextPath() + "/listas");
            return;
        }

        try {
            Long listaId = Long.parseLong(listaIdStr);
            ListaDeCompras lista = servicioArticulos.buscarListaPorId(listaId);

            if (lista == null) {
                MensajeUtil.agregarError(session, "La lista no existe");
                response.sendRedirect(request.getContextPath() + "/listas");
                return;
            }

            // Calcular total planificado
            double totalEstimado = lista.getArticulos().stream()
                    .mapToDouble(ArticuloDeCompras::getPrecioUnitario)
                    .sum();

            lista.setPrecioTotal(totalEstimado);

            // Cargar cuentas para el formulario de gastos
            List<Cuenta> cuentas = servicioCuenta.listarTodas();
            request.setAttribute("cuentas", cuentas);

            MensajeUtil.obtenerYLimpiarMensajes(request);
            request.setAttribute("lista", lista);
            request.getRequestDispatcher("/lista_compras/VistaDetalleLista.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            MensajeUtil.agregarError(session, "ID de lista no válido");
            response.sendRedirect(request.getContextPath() + "/listas");
        } catch (Exception e) {
            MensajeUtil.agregarError(session, "Error al cargar la lista: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/listas");
        }
    }

    private void listarListas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MensajeUtil.obtenerYLimpiarMensajes(request);

        // Obtener todas las listas
        List<ListaDeCompras> listas = servicioArticulos.obtenerListas();

        // Calcular totales para cada lista
        for (ListaDeCompras lista : listas) {
            double total = lista.getArticulos().stream()
                    .mapToDouble(ArticuloDeCompras::getPrecioUnitario)
                    .sum();
            lista.setPrecioTotal(total);
        }

        request.setAttribute("listas", listas);
        request.getRequestDispatcher("/lista_compras/VistaListas.jsp").forward(request, response);
    }
    // ========== MÉTODOS POST ==========

    private void crearLista(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession();

        try {
            String nombre = request.getParameter("nombre");

            // Validación: nombre vacío
            if (nombre == null || nombre.trim().isEmpty()) {
                MensajeUtil.agregarError(session, "El nombre de la lista es requerido");
                reenviarFormularioNuevaLista(request, response, nombre);
                return;
            }

            // Crear lista
            boolean exito = servicioArticulos.registrarListaDeCompras(nombre.trim());

            if (exito) {
                MensajeUtil.agregarExito(session, "Lista creada exitosamente");
                // Redirigir a listar listas (cuando lo implementes)
                response.sendRedirect(request.getContextPath() + "/listas");
            }

        } catch (IllegalArgumentException e) {
            MensajeUtil.agregarError(session, e.getMessage());
            reenviarFormularioNuevaLista(request, response, request.getParameter("nombre"));
        } catch (Exception e) {
            MensajeUtil.agregarError(session, "Error al crear la lista: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/listas/nuevo");
        }
    }

    private void agregarArticulo(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession();
        String listaIdStr = request.getParameter("idLista");

        try {
            Long listaId = Long.parseLong(listaIdStr);
            String nombre = request.getParameter("nombre");
            String precioStr = request.getParameter("precio");

            // Validación: nombre vacío
            if (nombre == null || nombre.trim().isEmpty()) {
                MensajeUtil.agregarError(session, "El nombre del artículo es requerido");
                response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + listaId);
                return;
            }

            // Validación: precio vacío o inválido
            if (precioStr == null || precioStr.trim().isEmpty()) {
                MensajeUtil.agregarError(session, "El precio es requerido");
                response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + listaId);
                return;
            }

            double precio = Double.parseDouble(precioStr);

            // Validación: precio debe ser mayor a cero
            if (precio <= 0) {
                MensajeUtil.agregarError(session, "El precio debe ser mayor a cero");
                response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + listaId);
                return;
            }

            // Buscar lista
            ListaDeCompras lista = servicioArticulos.buscarListaPorId(listaId);

            if (lista == null) {
                MensajeUtil.agregarError(session, "Lista no encontrada");
                response.sendRedirect(request.getContextPath() + "/listas");
                return;
            }

            // Agregar artículo
            boolean exito = servicioArticulos.registrarArticuloDeCompras(nombre.trim(), precio, lista);

            if (exito) {
                MensajeUtil.agregarExito(session, "Artículo agregado exitosamente");
            }

            response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + listaId);

        } catch (NumberFormatException e) {
            MensajeUtil.agregarError(session, "Datos inválidos");
            response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + listaIdStr);
        } catch (IllegalArgumentException e) {
            MensajeUtil.agregarError(session, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + listaIdStr);
        } catch (Exception e) {
            MensajeUtil.agregarError(session, "Error al agregar artículo: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + listaIdStr);
        }
    }

    private void eliminarArticulo(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        String itemIdStr = request.getParameter("idItem");
        String listaIdStr = request.getParameter("idLista");

        try {
            Long itemId = Long.parseLong(itemIdStr);
            Long listaId = Long.parseLong(listaIdStr);

            servicioArticulos.eliminarArticulo(itemId);
            MensajeUtil.agregarExito(session, "Artículo eliminado exitosamente");

            response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + listaId);

        } catch (Exception e) {
            MensajeUtil.agregarError(session, "Error al eliminar artículo: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + listaIdStr);
        }
    }

    private void marcarArticuloComprado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        String itemIdStr = request.getParameter("idItem");
        String listaIdStr = request.getParameter("idLista");
        String compradoStr = request.getParameter("comprado");

        try {
            Long itemId = Long.parseLong(itemIdStr);
            Long listaId = Long.parseLong(listaIdStr);
            boolean comprado = Boolean.parseBoolean(compradoStr);

            ArticuloDeCompras articulo = servicioArticulos.buscarArticuloPorId(itemId);

            if (articulo != null) {
                articulo.setEstado(comprado ? EstadoCompra.COMPLETADA : EstadoCompra.PENDIENTE);
                servicioArticulos.actualizarArticulo(articulo);
            }

            response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + listaId);

        } catch (Exception e) {
            MensajeUtil.agregarError(session, "Error al actualizar artículo: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + listaIdStr);
        }
    }
    // ========== HELPERS ==========A

    private void reenviarFormularioNuevaLista(HttpServletRequest request, HttpServletResponse response,
                                              String nombreIngresado)
            throws ServletException, IOException {

        request.setAttribute("nombreIngresado", nombreIngresado);
        request.getRequestDispatcher("/lista_compras/VistaFormularioLista.jsp").forward(request, response);
    }
}