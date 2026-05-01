package com.EconoMe.comun.dashboard;

import com.EconoMe.comun.mensajes.MensajeUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ServletDashboard", urlPatterns = {"/dashboard"})
public class ServletDashboard extends HttpServlet {

    private ServicioDashboard servicioDashboard;

    @Override
    public void init() throws ServletException {
        this.servicioDashboard = new ServicioDashboard();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Obtener datos del dashboard
            DatosDashboard datos = servicioDashboard.obtenerResumen();

            // 2. Limpiar mensajes previos
            MensajeUtil.obtenerYLimpiarMensajes(request);

            // 3. Almacenar en request
            request.setAttribute("dashboard", datos);
            request.setAttribute("estatus", datos.getEstatus());

            // 4. Forward al JSP único
            request.getRequestDispatcher("/comun/dashboard/Dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            // Log del error (en producción usar un logger apropiado)
            System.err.println("Error en ServletDashboard: " + e.getMessage());
            e.printStackTrace();

            // Crear un objeto de datos con error para mostrar en el JSP
            DatosDashboard datosError = new DatosDashboard(EstatusDashboard.SIN_CUENTAS);
            request.setAttribute("dashboard", datosError);
            request.setAttribute("estatus", EstatusDashboard.SIN_CUENTAS);
            request.setAttribute("mensajeError", "Error al cargar el dashboard");

            request.getRequestDispatcher("/comun/dashboard/Dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // El dashboard solo responde a GET
        doGet(request, response);
    }

    // Método para testing - permite inyectar un servicio mock
    protected void setServicioDashboard(ServicioDashboard servicioDashboard) {
        this.servicioDashboard = servicioDashboard;
    }
}