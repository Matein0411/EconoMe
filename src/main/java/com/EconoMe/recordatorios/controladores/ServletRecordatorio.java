package com.EconoMe.recordatorios.controladores;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;


import com.EconoMe.comun.mensajes.MensajeUtil;
import com.EconoMe.recordatorios.modelos.Recordatorio;
import com.EconoMe.recordatorios.modelos.Recurrencia;
import com.EconoMe.recordatorios.servicios.ServicioRecordatorio;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/recordatorios/*")
public class ServletRecordatorio extends HttpServlet {

    private ServicioRecordatorio servicioRecordatorio;

    @Override
    public void init() {
        this.servicioRecordatorio = new ServicioRecordatorio();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getPathInfo();
        if (action == null) action = "/";

        switch (action) {
            case "/nuevo":
                mostrarFormulario(request, response, new Recordatorio());
                break;
            case "/editar":
                mostrarFormularioEditar(request, response);
                break;
            default:
                listarRecordatorios(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String action = request.getPathInfo();
        String method = request.getParameter("_method");

        if ("/borrar".equals(action)) {
            borrarRecordatorio(request, response);
        } else {
            // Tanto crear (POST) como editar (PUT) usan la misma lógica de extracción
            boolean esEdicion = "PUT".equalsIgnoreCase(method);
            procesarGuardado(request, response, esEdicion);
        }
    }

    // --- Métodos de Listado y Vistas ---

    private void listarRecordatorios(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MensajeUtil.obtenerYLimpiarMensajes(request);

        try {
            List<Recordatorio> recordatorios = obtenerRecordatoriosActivos();
            request.setAttribute("recordatorios", recordatorios);
        } catch (Exception e) {
            MensajeUtil.agregarError(
                    request.getSession(),
                    "No se pudieron cargar los recordatorios"
            );
            request.setAttribute("recordatorios", List.of());
        }

        reenviarAVistaListado(request, response);
    }

    private List<Recordatorio> obtenerRecordatoriosActivos() {
        return servicioRecordatorio.listarActivos();
    }

    private void reenviarAVistaListado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/recordatorio/VistaRecordatorio.jsp")
                .forward(request, response);
    }



    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response, Recordatorio recordatorio)
            throws ServletException, IOException {
        MensajeUtil.obtenerYLimpiarMensajes(request);
        request.setAttribute("recordatorio", recordatorio);
        request.setAttribute("recurrencias", Recurrencia.values());
        request.getRequestDispatcher("/recordatorio/VistaFormularioRecordatorio.jsp").forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            Recordatorio recordatorio = servicioRecordatorio.buscarPorId(id);
            if (recordatorio != null) {
                mostrarFormulario(request, response, recordatorio);
            } else {
                MensajeUtil.agregarError(request.getSession(), "Recordatorio no encontrado");
                response.sendRedirect(request.getContextPath() + "/recordatorios");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/recordatorios");
        }
    }

    // --- Lógica Unificada de Guardado (Crear y Editar) ---

    private void procesarGuardado(HttpServletRequest request, HttpServletResponse response, boolean esEdicion)
            throws IOException, ServletException {

        HttpSession session = request.getSession();
        Recordatorio recordatorio = new Recordatorio(); // Objeto temporal para rellenar datos

        try {
            // 1. Extraer datos del request
            llenarRecordatorioDesdeRequest(recordatorio, request);

            // 2. Si es edición, necesitamos el ID y buscar el original si fuera necesario
            // (Aquí simplificamos usando el objeto nuevo con el ID setatado)
            if (esEdicion) {
                Long id = Long.parseLong(request.getParameter("id"));
                recordatorio.setId(id);

                // Verificamos existencia antes de guardar
                if (servicioRecordatorio.buscarPorId(id) == null) {
                    throw new IllegalArgumentException("El recordatorio a editar no existe.");
                }

                servicioRecordatorio.actualizarRecordatorio(recordatorio);
                MensajeUtil.agregarExito(session, "Recordatorio actualizado exitosamente");
            } else {
                servicioRecordatorio.crearRecordatorio(recordatorio);
                MensajeUtil.agregarExito(session, "Recordatorio creado exitosamente");
            }

            response.sendRedirect(request.getContextPath() + "/recordatorios");

        } catch (DateTimeParseException e) {
            manejarErrorFormulario(request, response, recordatorio, "Error: Formato de fecha inválido");
        } catch (IllegalArgumentException e) {
            manejarErrorFormulario(request, response, recordatorio, e.getMessage());
        } catch (Exception e) {
            manejarErrorFormulario(request, response, recordatorio, "Error inesperado: " + e.getMessage());
        }
    }

    // --- Helpers ---

    private void llenarRecordatorioDesdeRequest(Recordatorio r, HttpServletRequest request) {
        r.setDescripcion(request.getParameter("descripcion"));
        r.setFechaInicio(LocalDate.parse(request.getParameter("fechaInicio")));

        String fechaFinStr = request.getParameter("fechaFin");
        if (fechaFinStr != null && !fechaFinStr.isEmpty()) {
            r.setFechaFin(LocalDate.parse(fechaFinStr));
        }

        r.setRecurrencia(Recurrencia.valueOf(request.getParameter("recurrencia")));
        r.setDiasDeAnticipacion(Integer.parseInt(request.getParameter("diasDeAnticipacion")));
        try {
            r.setMonto(Double.parseDouble(request.getParameter("monto")));
        } catch (NumberFormatException e) {
            r.setMonto(0.0);
        }
    }

    private void manejarErrorFormulario(HttpServletRequest request, HttpServletResponse response,
                                        Recordatorio datosIngresados, String mensajeError)
            throws ServletException, IOException {
        MensajeUtil.agregarError(request.getSession(), mensajeError);
        // Volvemos a mostrar el formulario con los datos que el usuario ya escribió
        mostrarFormulario(request, response, datosIngresados);
    }

    private void borrarRecordatorio(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();

        try {
            String idParam = request.getParameter("id");

            // Validar que el parámetro no sea nulo o vacío
            if (idParam == null || idParam.trim().isEmpty()) {
                throw new IllegalArgumentException("ID de recordatorio no proporcionado");
            }

            Long id = Long.parseLong(idParam);
            servicioRecordatorio.eliminarRecordatorio(id);
            MensajeUtil.agregarExito(session, "Recordatorio eliminado exitosamente");

        } catch (NumberFormatException e) {
            MensajeUtil.agregarError(session, "El ID del recordatorio no es válido");
        } catch (IllegalArgumentException e) {
            MensajeUtil.agregarError(session, e.getMessage());
        } catch (Exception e) {
            MensajeUtil.agregarError(session, "Error inesperado al eliminar el recordatorio");
        }

        response.sendRedirect(request.getContextPath() + "/recordatorios");
    }
}