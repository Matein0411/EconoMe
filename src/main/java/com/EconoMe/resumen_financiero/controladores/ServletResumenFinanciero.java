package com.EconoMe.resumen_financiero.controladores;

import com.EconoMe.comun.mensajes.MensajeUtil;
import com.EconoMe.resumen_financiero.dao.DAOResumenFinanciero;
import com.EconoMe.resumen_financiero.dao.DAODocumentoPDF;
import com.EconoMe.resumen_financiero.modelos.DocumentoPDF;
import com.EconoMe.resumen_financiero.modelos.ResumenFinanciero;
import com.EconoMe.resumen_financiero.servicios.ServicioResumenFinanciero;
import com.EconoMe.comun.GestorDeArchivos;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(urlPatterns = {"/resumenes/subirPDF"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,       // 10 MB
        maxRequestSize = 1024 * 1024 * 50     // 50 MB
)
public class ServletResumenFinanciero extends HttpServlet {

    private DAOResumenFinanciero daoResumenFinanciero;
    private DAODocumentoPDF daoDocumentoPDF;
    private ServicioResumenFinanciero servicioResumenFinanciero;
    private static final String PATH = "/resumen_financiero/VistaResumenFinanciero.jsp";

    @Override
    public void init() throws ServletException {
        super.init();
        this.daoResumenFinanciero = new DAOResumenFinanciero();
        this.daoDocumentoPDF = new DAODocumentoPDF();
        this.servicioResumenFinanciero = new ServicioResumenFinanciero();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        try {
            // 1. Obtener archivo del request
            Part archivo = GestorDeArchivos.obtenerArchivo(request, response);
            if (archivo == null) {
                MensajeUtil.agregarError(session, "No se pudo cargar el archivo PDF");
                response.sendRedirect(request.getContextPath() + "/resumenes");
                return;
            }

            // 2. Guardar PDF en BD con su nombre y contenido en bytes
            byte[] archivoEnBytes = GestorDeArchivos.transformarArchivoABytes(archivo);
            DocumentoPDF documentoPDF = daoDocumentoPDF.guardarPDF(
                    archivo.getSubmittedFileName(),
                    archivoEnBytes
            );

            // 3. Obtener ruta temporal donde se va a guardar el reporte
            String rutaArchivo = GestorDeArchivos.obtenerRutaDeArchivoTemporal(this, request, response);
            if (rutaArchivo == null) {
                MensajeUtil.agregarError(session, "Error al procesar el archivo temporal");
                response.sendRedirect(request.getContextPath() + "/resumenes");
                return;
            }

            // 4. Procesar la información del contenido del PDF (SIN usuario)
            ResumenFinanciero resumenFinanciero = servicioResumenFinanciero.procesarInformacion(
                    rutaArchivo,
                    documentoPDF
            );

            if (resumenFinanciero == null) {
                MensajeUtil.agregarError(session, "No se pudo procesar la información del PDF");
                response.sendRedirect(request.getContextPath() + "/resumenes");
                return;
            }

            // 5. Guardar en la BD el resumen financiero
            daoResumenFinanciero.crear(resumenFinanciero);
            MensajeUtil.agregarExito(session, "Resumen financiero procesado exitosamente");

            // 6. Preparar vista y mostrar resultado
            prepararVistaResumenFinanciero(request, resumenFinanciero);
            request.getRequestDispatcher(PATH).forward(request, response);

        } catch (Exception e) {
            System.err.println("Error al procesar el PDF: " + e.getMessage());
            e.printStackTrace();
            MensajeUtil.agregarError(session, "Error al procesar el PDF: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/resumenes");
        }
    }

    /**
     * Prepara los atributos para mostrar el resumen en la vista
     */
    private void prepararVistaResumenFinanciero(HttpServletRequest request, ResumenFinanciero resumenFinanciero) {
        request.setAttribute("Ingresos", resumenFinanciero.getIngresosTotales());
        request.setAttribute("Gastos", resumenFinanciero.getGastosTotales());
        request.setAttribute("AhorroNeto", resumenFinanciero.getAhorroNeto());
        request.setAttribute("fechaPeriodoAnterior", resumenFinanciero.getFechaPeriodoAnterior());
        request.setAttribute("fechaPeriodoActual", resumenFinanciero.getFechaPeriodoActual());
        request.setAttribute("fechaCreacionFormateada", resumenFinanciero.getFechaCreacionFormateada());
    }
}