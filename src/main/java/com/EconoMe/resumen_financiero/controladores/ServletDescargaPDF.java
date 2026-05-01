package com.EconoMe.resumen_financiero.controladores;

import com.EconoMe.comun.mensajes.MensajeUtil;
import com.EconoMe.resumen_financiero.dao.DAOResumenFinanciero;
import com.EconoMe.resumen_financiero.modelos.DocumentoPDF;
import com.EconoMe.resumen_financiero.modelos.ResumenFinanciero;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {"/resumenes/descargarPDF"})
public class ServletDescargaPDF extends HttpServlet {

    private DAOResumenFinanciero daoResumenFinanciero;

    @Override
    public void init() throws ServletException {
        super.init();
        this.daoResumenFinanciero = new DAOResumenFinanciero();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        try {
            // 1. Obtener y validar ID del resumen financiero
            Long resumenId = obtenerIdDelResumen(request);

            if (resumenId == null) {
                MensajeUtil.agregarError(session, "ID de resumen inválido");
                response.sendRedirect(request.getContextPath() + "/resumenes");
                return;
            }

            // 2. Buscar el resumen (SIN validación de usuario)
            ResumenFinanciero resumen = daoResumenFinanciero.buscarPorId(resumenId);

            if (resumen == null) {
                MensajeUtil.agregarError(session, "Resumen financiero no encontrado");
                response.sendRedirect(request.getContextPath() + "/resumenes");
                return;
            }

            // 3. Verificar que el PDF exista
            DocumentoPDF documento = resumen.getDocumentoPDF();

            if (documento == null || documento.getArchivoPdf() == null) {
                MensajeUtil.agregarError(session, "PDF no encontrado");
                response.sendRedirect(request.getContextPath() + "/resumenes");
                return;
            }

            // 4. Configurar headers HTTP para descarga
            configurarRespuestaDeDescarga(response, documento);

            // 5. Escribir el PDF en la respuesta
            escribirContenidoDelArchivoEnLaRespuesta(response, documento);

        } catch (NumberFormatException e) {
            MensajeUtil.agregarError(session, "ID inválido");
            response.sendRedirect(request.getContextPath() + "/resumenes");
        } catch (Exception e) {
            System.err.println("Error al descargar el PDF: " + e.getMessage());
            e.printStackTrace();
            MensajeUtil.agregarError(session, "Error al procesar la descarga del PDF");
            response.sendRedirect(request.getContextPath() + "/resumenes");
        }
    }

    /**
     * Configura los headers HTTP para la descarga del PDF
     */
    private void configurarRespuestaDeDescarga(HttpServletResponse response, DocumentoPDF documento) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + sanitizarNombreArchivo(documento.getNombre()) + "\"");
        response.setContentLength(documento.getArchivoPdf().length);

        // Headers de seguridad
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }

    /**
     * Escribe el contenido del PDF en la respuesta HTTP
     */
    private void escribirContenidoDelArchivoEnLaRespuesta(HttpServletResponse response, DocumentoPDF documento)
            throws IOException {
        try (ServletOutputStream out = response.getOutputStream()) {
            out.write(documento.getArchivoPdf());
            out.flush();
        }
    }

    /**
     * Sanitiza el nombre del archivo removiendo caracteres peligrosos
     */
    private String sanitizarNombreArchivo(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "resumen_financiero.pdf";
        }

        // Remover caracteres peligrosos y espacios
        String nombreSanitizado = nombre.replaceAll("[^a-zA-Z0-9._-]", "_");

        // Asegurar que termine en .pdf
        if (!nombreSanitizado.toLowerCase().endsWith(".pdf")) {
            nombreSanitizado += ".pdf";
        }

        return nombreSanitizado;
    }

    /**
     * Obtiene y valida el ID del resumen financiero desde los parámetros
     */
    private Long obtenerIdDelResumen(HttpServletRequest request) {
        String idParam = request.getParameter("resumenId");

        if (idParam == null || idParam.trim().isEmpty()) {
            return null;
        }

        try {
            return Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}