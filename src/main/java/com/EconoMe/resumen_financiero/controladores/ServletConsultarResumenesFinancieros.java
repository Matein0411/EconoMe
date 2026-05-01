package com.EconoMe.resumen_financiero.controladores;

import com.EconoMe.comun.mensajes.MensajeUtil;
import com.EconoMe.resumen_financiero.modelos.ResumenFinanciero;
import com.EconoMe.resumen_financiero.servicios.ServicioResumenFinanciero;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/resumen_financiero/consultarResumenes"})
public class ServletConsultarResumenesFinancieros extends HttpServlet {

    private ServicioResumenFinanciero servicioResumenFinanciero;
    private static final String PATH = "/resumen_financiero/VistaResumenFinanciero.jsp";

    @Override
    public void init() {
        this.servicioResumenFinanciero = new ServicioResumenFinanciero();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        try {
            MensajeUtil.obtenerYLimpiarMensajes(request);

            // Obtener todos los resumenes de la base de datos
            List<ResumenFinanciero> resumenes = servicioResumenFinanciero.listarConDocumentosPDF();

            mostrarInformacionDeLosResumenesFinancieros(request, response, resumenes);

        } catch (Exception e) {
            e.printStackTrace();
            MensajeUtil.agregarError(session, "Error al consultar resúmenes: " + e.getMessage());
            request.getRequestDispatcher(PATH).forward(request, response);
        }
    }

    private static void mostrarInformacionDeLosResumenesFinancieros(HttpServletRequest request,
                                                                    HttpServletResponse response,
                                                                    List<ResumenFinanciero> resumenes)
            throws ServletException, IOException {
        // Enviar al JSP
        request.setAttribute("ResumenesFinancieros", resumenes);
        request.getRequestDispatcher(PATH).forward(request, response);
    }
}