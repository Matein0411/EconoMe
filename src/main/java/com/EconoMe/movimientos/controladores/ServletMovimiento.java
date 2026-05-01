package com.EconoMe.movimientos.controladores;

import com.EconoMe.comun.mensajes.MensajeUtil;
import com.EconoMe.cuentas.dao.DAOCuenta;
import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.movimientos.modelos.CategoriaGasto;
import com.EconoMe.movimientos.modelos.CategoriaIngreso;
import com.EconoMe.movimientos.modelos.Movimiento;
import com.EconoMe.movimientos.servicios.ServicioMovimiento;
import com.EconoMe.plantillas.modelos.Plantilla;
import com.EconoMe.plantillas.servicios.ServicioPlantilla;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/movimientos/*")
public class ServletMovimiento extends HttpServlet {

    private ServicioMovimiento servicioMovimiento;
    private ServicioPlantilla servicioPlantilla;
    private DAOCuenta daoCuenta;

    @Override
    public void init() {
        this.servicioMovimiento = new ServicioMovimiento();
        this.servicioPlantilla = new ServicioPlantilla();
        this.daoCuenta = new DAOCuenta();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getPathInfo();
        if (action == null) {
            action = "/";
        }

        switch (action) {
            default:
                mostrarFormularioMovimiento(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String action = request.getPathInfo();
        if (action == null || "/".equals(action)) {
            registrarMovimiento(request, response);
        }
    }

//    Cambios para recuperar datos del ServicioPlantilla y llenar el formulario de registro.

    private void mostrarFormularioMovimiento(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession();

            // Obtener y limpiar mensajes usando MensajeUtil
            MensajeUtil.obtenerYLimpiarMensajes(request);

            // Obtener todas las cuentas para el dropdown
            List<Cuenta> listaCuentas = daoCuenta.listar();
            request.setAttribute("cuentas", listaCuentas);

            // **AGREGAR: Cargar plantillas**
            List<Plantilla> plantillas = servicioPlantilla.listarPlantillas();
            request.setAttribute("plantillas", plantillas);

            // **AGREGAR: Verificar si hay un movimiento precargado desde plantilla**
            Movimiento movimientoPrecargado = (Movimiento) session.getAttribute("movimientoDesdePlantilla");
            if (movimientoPrecargado != null) {
                request.setAttribute("movimientoPrecargado", movimientoPrecargado);
                session.removeAttribute("movimientoDesdePlantilla");
            }

            request.getRequestDispatcher("/movimiento/VistaMovimientos.jsp").forward(request, response);

        } catch (Exception e) {
            HttpSession session = request.getSession();
            MensajeUtil.agregarError(session, "Error al cargar el formulario: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/cuentas");
        }
    }

    private void registrarMovimiento(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession();
        String idLista = request.getParameter("idLista");
        try {
            // Obtener datos del formulario
            String cuentaIdStr = request.getParameter("cuentaId");
            String tipo = request.getParameter("tipo");
            String montoStr = request.getParameter("monto");
            String descripcion = request.getParameter("descripcion");
            String categoria = request.getParameter("categoria");

            // Validar campos vacíos
            if (cuentaIdStr == null || cuentaIdStr.trim().isEmpty()) {
                MensajeUtil.agregarError(session, "Rellena este campo: Cuenta");
                if (idLista != null) {
                    response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + idLista);
                    return;
                } else {
                    reenviarFormularioConDatos(request, response, cuentaIdStr, tipo, montoStr, descripcion, categoria);
                    return;
                }
            }

            if (tipo == null || tipo.trim().isEmpty()) {
                MensajeUtil.agregarError(session, "Rellena este campo: Tipo de Movimiento");
                if (idLista != null) {
                    response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + idLista);
                    return;
                } else {
                    reenviarFormularioConDatos(request, response, cuentaIdStr, tipo, montoStr, descripcion, categoria);
                    return;
                }
            }

            if (montoStr == null || montoStr.trim().isEmpty()) {
                MensajeUtil.agregarError(session, "Rellena este campo: Monto");
                if (idLista != null) {
                    response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + idLista);
                    return;
                } else {
                    reenviarFormularioConDatos(request, response, cuentaIdStr, tipo, montoStr, descripcion, categoria);
                    return;
                }
            }

            if (descripcion == null || descripcion.trim().isEmpty()) {
                MensajeUtil.agregarError(session, "Rellena este campo: Descripción");
                if (idLista != null) {
                    response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + idLista);
                    return;
                } else {
                    reenviarFormularioConDatos(request, response, cuentaIdStr, tipo, montoStr, descripcion, categoria);
                    return;
                }
            }

            if (categoria == null || categoria.trim().isEmpty()) {
                MensajeUtil.agregarError(session, "Rellena este campo: Categoría");
                if (idLista != null) {
                    response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + idLista);
                    return;
                } else {
                    reenviarFormularioConDatos(request, response, cuentaIdStr, tipo, montoStr, descripcion, categoria);
                    return;
                }
            }

            // Parsear datos
            Long cuentaId = Long.parseLong(cuentaIdStr);
            double monto = Double.parseDouble(montoStr);

            // Registrar según el tipo
            if ("INGRESO".equals(tipo)) {
                CategoriaIngreso categoriaIngreso = CategoriaIngreso.valueOf(categoria);
                servicioMovimiento.registrarIngreso(cuentaId, monto, descripcion.trim(), categoriaIngreso);
                MensajeUtil.agregarExito(session, "Ingreso registrado exitosamente");
            } else if ("GASTO".equals(tipo)) {
                CategoriaGasto categoriaGasto = CategoriaGasto.valueOf(categoria);
                servicioMovimiento.registrarGasto(cuentaId, monto, descripcion.trim(), categoriaGasto);
                MensajeUtil.agregarExito(session, "Gasto registrado exitosamente");
            } else {
                throw new IllegalArgumentException("Tipo de movimiento no válido");
            }

            // Redirección según origen
            if (idLista != null) {
                response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + idLista);
            } else {
                response.sendRedirect(request.getContextPath() + "/cuentas/detalle?id=" + cuentaId);
            }
        } catch (NumberFormatException e) {
            MensajeUtil.agregarError(session, "El monto debe ser un número válido");
            if (idLista != null) {
                response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + idLista);
            } else {
                reenviarFormularioConDatos(request, response,
                        request.getParameter("cuentaId"),
                        request.getParameter("tipo"),
                        request.getParameter("monto"),
                        request.getParameter("descripcion"),
                        request.getParameter("categoria"));
            }
        } catch (IllegalArgumentException e) {
            String mensaje = e.getMessage();

            // Mapear mensajes técnicos a mensajes de usuario
            if (mensaje.contains("mayor a")) {
                mensaje = "Monto inválido. Debe ser mayor a cero";
            } else if (mensaje.contains("Saldo insuficiente")) {
                mensaje = "Saldo insuficiente para realizar este gasto";
            }

            MensajeUtil.agregarError(session, mensaje);
            if (idLista != null) {
                response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + idLista);
            } else {
                reenviarFormularioConDatos(request, response,
                        request.getParameter("cuentaId"),
                        request.getParameter("tipo"),
                        request.getParameter("monto"),
                        request.getParameter("descripcion"),
                        request.getParameter("categoria"));
            }
        } catch (Exception e) {
            MensajeUtil.agregarError(session, "Error al registrar el movimiento: " + e.getMessage());
            if (idLista != null) {
                response.sendRedirect(request.getContextPath() + "/listas/detalle?id=" + idLista);
            } else {
                response.sendRedirect(request.getContextPath() + "/movimientos");
            }
        }
    }

    private void reenviarFormularioConDatos(HttpServletRequest request, HttpServletResponse response,
                                            String cuentaId, String tipo, String monto, String descripcion, String categoria)
            throws ServletException, IOException {

        // Mantener los datos ingresados
        request.setAttribute("cuentaIdIngresado", cuentaId);
        request.setAttribute("tipoIngresado", tipo);
        request.setAttribute("montoIngresado", monto);
        request.setAttribute("descripcionIngresada", descripcion);
        request.setAttribute("categoriaIngresada", categoria);

        // Obtener cuentas nuevamente
        List<Cuenta> listaCuentas = daoCuenta.listar();
        request.setAttribute("cuentas", listaCuentas);

        // **AGREGAR: Cargar plantillas también cuando hay error**
        List<Plantilla> plantillas = servicioPlantilla.listarPlantillas();
        request.setAttribute("plantillas", plantillas);

        request.getRequestDispatcher("/movimiento/VistaMovimientos.jsp").forward(request, response);
    }

    @Override
    public void destroy() {
        if (daoCuenta != null) {
            DAOCuenta.cerrarFactory();
        }
    }
}