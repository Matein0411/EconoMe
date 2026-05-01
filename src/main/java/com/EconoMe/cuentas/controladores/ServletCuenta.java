package com.EconoMe.cuentas.controladores;

import com.EconoMe.comun.mensajes.MensajeUtil;
import com.EconoMe.cuentas.dao.DAOCuenta;
import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.cuentas.servicios.ServicioCuenta;
import com.EconoMe.cuentas.modelos.TipoCuenta;
import com.EconoMe.movimientos.modelos.Movimiento;
import com.EconoMe.movimientos.servicios.ServicioMovimiento;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/cuentas/*")
public class ServletCuenta extends HttpServlet {

    private static final int MOVIMIENTOS_POR_PAGINA = 10;

    private ServicioCuenta servicioCuenta;
    private DAOCuenta daoCuenta;
    private ServicioMovimiento servicioMovimiento;

    @Override
    public void init() {
        this.daoCuenta = new DAOCuenta();
        this.servicioCuenta = new ServicioCuenta(daoCuenta);
        this.servicioMovimiento = new ServicioMovimiento();
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
                mostrarFormularioNuevo(request, response);
                break;
            case "/detalle":
                mostrarDetalleCuenta(request, response);
                break;
            default:
                listarCuentas(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String action = request.getPathInfo();
        if (action == null || "/".equals(action)) {
            crearCuenta(request, response);
        }
    }

    private void listarCuentas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Obtener y limpiar mensajes de la sesión usando MensajeUtil
            MensajeUtil.obtenerYLimpiarMensajes(request);

            // Obtener todas las cuentas directamente del DAO
            List<Cuenta> listaCuentas = daoCuenta.listar();

            // Pasar datos a la vista
            request.setAttribute("cuentas", listaCuentas);
            request.getRequestDispatcher("/cuenta/VistaCuentas.jsp").forward(request, response);

        } catch (Exception e) {
            HttpSession session = request.getSession();
            MensajeUtil.agregarError(session, "Error al cargar las cuentas: " + e.getMessage());
            request.getRequestDispatcher("/cuenta/VistaCuentas.jsp").forward(request, response);
        }
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener y limpiar mensajes usando MensajeUtil
        MensajeUtil.obtenerYLimpiarMensajes(request);

        // Pasar los tipos de cuenta disponibles al formulario
        request.setAttribute("tipos", TipoCuenta.values());
        request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp").forward(request, response);
    }

    private void mostrarDetalleCuenta(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String cuentaIdStr = request.getParameter("id");
        String paginaStr = request.getParameter("pagina");

        if (cuentaIdStr == null || cuentaIdStr.trim().isEmpty()) {
            MensajeUtil.agregarError(session, "ID de cuenta no válido");
            response.sendRedirect(request.getContextPath() + "/cuentas");
            return;
        }

        try {
            Long cuentaId = Long.parseLong(cuentaIdStr);

            // Validar y obtener número de página
            int paginaActual = validarYObtenerPagina(paginaStr);

            // Parámetros de filtro
            String tipo = request.getParameter("tipo");
            String fechaDesde = request.getParameter("fechaDesde");
            String fechaHasta = request.getParameter("fechaHasta");
            String categoria = request.getParameter("categoria");

            // Buscar la cuenta
            Cuenta cuenta = servicioCuenta.buscarCuenta(cuentaId);

            if (cuenta == null) {
                MensajeUtil.agregarError(session, "La cuenta no existe");
                response.sendRedirect(request.getContextPath() + "/cuentas");
                return;
            }

            // Obtener estadísticas generales (sin paginación)
            double totalIngresos = servicioMovimiento.sumarIngresosPorCuenta(cuentaId);
            double totalGastos = servicioMovimiento.sumarGastosPorCuenta(cuentaId);

            // Calcular datos de paginación con filtros
            int totalPaginas = servicioMovimiento.obtenerTotalPaginasConFiltros(
                    cuentaId, tipo, categoria, fechaDesde, fechaHasta, MOVIMIENTOS_POR_PAGINA);
            long cantidadTotal = totalPaginas > 0 ? (long) Math.ceil(totalPaginas * MOVIMIENTOS_POR_PAGINA) : 0;

            // Validar que la página esté en rango
            if (paginaActual > totalPaginas && totalPaginas > 0) {
                paginaActual = totalPaginas;
            }

            // Obtener movimientos paginados y filtrados
            List<Movimiento> movimientos = servicioMovimiento.listarMovimientosConFiltros(
                    cuentaId, tipo, categoria, fechaDesde, fechaHasta, paginaActual, MOVIMIENTOS_POR_PAGINA);

            // Calcular indicadores de paginación
            int mostrandoDesde = cantidadTotal > 0 ? ((paginaActual - 1) * MOVIMIENTOS_POR_PAGINA + 1) : 0;
            int mostrandoHasta = mostrandoDesde + movimientos.size() - 1;

            // Limpiar mensajes usando MensajeUtil
            MensajeUtil.obtenerYLimpiarMensajes(request);

            // Pasar datos a la vista
            request.setAttribute("cuenta", cuenta);
            request.setAttribute("movimientos", movimientos);
            request.setAttribute("totalIngresos", totalIngresos);
            request.setAttribute("totalGastos", totalGastos);
            request.setAttribute("cantidadMovimientos", cantidadTotal);

            // Datos de paginación
            request.setAttribute("paginaActual", paginaActual);
            request.setAttribute("totalPaginas", totalPaginas);
            request.setAttribute("mostrandoDesde", mostrandoDesde);
            request.setAttribute("mostrandoHasta", mostrandoHasta);
            request.setAttribute("esPrimeraPagina", paginaActual == 1);
            request.setAttribute("esUltimaPagina", paginaActual >= totalPaginas || totalPaginas == 0);

            request.getRequestDispatcher("/cuenta/VistaDetalleCuenta.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            MensajeUtil.agregarError(session, "ID de cuenta no válido");
            response.sendRedirect(request.getContextPath() + "/cuentas");
        } catch (IllegalArgumentException e) {
            // Error de validación de página
            MensajeUtil.agregarError(session, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/cuentas/detalle?id=" + cuentaIdStr);
        } catch (Exception e) {
            MensajeUtil.agregarError(session, "Error al cargar los detalles de la cuenta: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/cuentas");
        }
    }

    /**
     * Valida y obtiene el número de página desde el parámetro
     * @param paginaStr Parámetro de página del request
     * @return Número de página válido (mínimo 1)
     */
    private int validarYObtenerPagina(String paginaStr) {
        if (paginaStr == null || paginaStr.trim().isEmpty()) {
            return 1; // Página por defecto
        }

        try {
            int pagina = Integer.parseInt(paginaStr);

            if (pagina < 1) {
                throw new IllegalArgumentException("El número de página debe ser mayor o igual a 1");
            }

            return pagina;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El número de página debe ser un entero válido");
        }
    }

    private void crearCuenta(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession();

        try {
            // Obtener datos del formulario
            String nombre = request.getParameter("nombre");
            String tipoStr = request.getParameter("tipo");
            String montoStr = request.getParameter("monto");

            // Validar que los campos no estén vacíos
            if (nombre == null || nombre.trim().isEmpty()) {
                MensajeUtil.agregarError(session, "Rellena este campo: Nombre de la Cuenta");
                reenviarFormularioConDatos(request, response, nombre, tipoStr, montoStr);
                return;
            }

            if (tipoStr == null || tipoStr.trim().isEmpty()) {
                MensajeUtil.agregarError(session, "Rellena este campo: Tipo de Cuenta");
                reenviarFormularioConDatos(request, response, nombre, tipoStr, montoStr);
                return;
            }

            if (montoStr == null || montoStr.trim().isEmpty()) {
                MensajeUtil.agregarError(session, "Rellena este campo: Saldo Inicial");
                reenviarFormularioConDatos(request, response, nombre, tipoStr, montoStr);
                return;
            }

            // Parsear tipo y monto
            TipoCuenta tipo = TipoCuenta.valueOf(tipoStr);
            double monto = Double.parseDouble(montoStr);

            // Crear cuenta
            Cuenta nuevaCuenta = new Cuenta(nombre.trim(), tipo, monto);

            // Intentar crear la cuenta
            servicioCuenta.crearCuenta(nuevaCuenta);

            MensajeUtil.agregarExito(session, "Cuenta creada exitosamente");
            response.sendRedirect(request.getContextPath() + "/cuentas");

        } catch (NumberFormatException e) {
            // Error al parsear el monto
            MensajeUtil.agregarError(session, "El monto debe ser un número válido");
            reenviarFormularioConDatos(request, response,
                    request.getParameter("nombre"),
                    request.getParameter("tipo"),
                    request.getParameter("monto"));

        } catch (IllegalArgumentException e) {
            // Validaciones de ServicioCuenta
            String mensaje = e.getMessage();

            // Mapear mensajes técnicos a mensajes de usuario
            if (mensaje.contains("mayor a")) {
                mensaje = "Monto inválido. Debe ser mayor a cero";
            } else if (mensaje.contains("obligatorio")) {
                mensaje = "Rellena este campo.";
            }

            MensajeUtil.agregarError(session, mensaje);
            reenviarFormularioConDatos(request, response,
                    request.getParameter("nombre"),
                    request.getParameter("tipo"),
                    request.getParameter("monto"));

        } catch (IllegalStateException e) {
            // Cuenta duplicada
            MensajeUtil.agregarError(session, "Ya existe una cuenta del mismo nombre y tipo");
            reenviarFormularioConDatos(request, response,
                    request.getParameter("nombre"),
                    request.getParameter("tipo"),
                    request.getParameter("monto"));

        } catch (Exception e) {
            // Error inesperado
            MensajeUtil.agregarError(session, "Error al crear la cuenta: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/cuentas/nuevo");
        }
    }

    private void reenviarFormularioConDatos(HttpServletRequest request, HttpServletResponse response,
                                            String nombre, String tipo, String monto)
            throws ServletException, IOException {

        // Mantener los datos ingresados
        request.setAttribute("nombreIngresado", nombre);
        request.setAttribute("tipoIngresado", tipo);
        request.setAttribute("montoIngresado", monto);
        request.setAttribute("tipos", TipoCuenta.values());

        request.getRequestDispatcher("/cuenta/VistaFormularioCuenta.jsp").forward(request, response);
    }

    @Override
    public void destroy() {
        // Cerrar recursos si es necesario
        if (daoCuenta != null) {
            DAOCuenta.cerrarFactory();
        }
    }
}