package com.EconoMe.resumen_financiero.controladores;

import com.EconoMe.resumen_financiero.dao.DAOResumenFinanciero;
import com.EconoMe.resumen_financiero.modelos.DocumentoPDF;
import com.EconoMe.resumen_financiero.modelos.ResumenFinanciero;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServletDescargaPDFTest {

    @Mock
    private DAOResumenFinanciero daoResumenFinanciero;

    private ServletDescargaPDF servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private ServletOutputStream outputStream;

    @BeforeEach
    void setUp() throws ServletException {
        servlet = new ServletDescargaPDF() {
            @Override
            public void init() throws ServletException {
                // Inyectar el mock en lugar de crear la instancia real
                try {
                    java.lang.reflect.Field field = ServletDescargaPDF.class.getDeclaredField("daoResumenFinanciero");
                    field.setAccessible(true);
                    field.set(this, daoResumenFinanciero);
                } catch (Exception e) {
                    throw new ServletException(e);
                }
            }
        };
        servlet.init();
    }

    @Test
    void testDescargarPDF_Exitoso() throws Exception {
        // Arrange
        byte[] pdfContent = "PDF content".getBytes();
        DocumentoPDF documento = new DocumentoPDF("estado_cuenta.pdf", pdfContent);
        ResumenFinanciero resumen = crearResumenConPDF(documento);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter("resumenId")).thenReturn("1");
        when(daoResumenFinanciero.buscarPorId(1L)).thenReturn(resumen);
        when(response.getOutputStream()).thenReturn(outputStream);

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(response).setContentType("application/pdf");
        verify(response).setHeader(eq("Content-Disposition"), contains("estado_cuenta.pdf"));
        verify(response).setContentLength(pdfContent.length);
        verify(outputStream).write(pdfContent);
        verify(outputStream).flush();
    }

    @Test
    void testDescargarPDF_IdInvalido() throws Exception {
        // Arrange
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("resumenId")).thenReturn("abc");
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/EconoMe/resumenes");
        verify(response, never()).getOutputStream();
    }

    @Test
    void testDescargarPDF_ResumenNoEncontrado() throws Exception {
        // Arrange
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("resumenId")).thenReturn("999");
        when(daoResumenFinanciero.buscarPorId(999L)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/EconoMe/resumenes");
    }

    @Test
    void testDescargarPDF_SinDocumento() throws Exception {
        // Arrange
        ResumenFinanciero resumenSinPDF = new ResumenFinanciero();
        resumenSinPDF.setId(1L);
        resumenSinPDF.setDocumentoPDF(null);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter("resumenId")).thenReturn("1");
        when(daoResumenFinanciero.buscarPorId(1L)).thenReturn(resumenSinPDF);
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/EconoMe/resumenes");
    }

    @Test
    void testDescargarPDF_NombreConCaracteresEspeciales() throws Exception {
        // Arrange
        byte[] pdfContent = "PDF content".getBytes();
        DocumentoPDF documento = new DocumentoPDF("estado cuenta <>.pdf", pdfContent);
        ResumenFinanciero resumen = crearResumenConPDF(documento);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter("resumenId")).thenReturn("1");
        when(daoResumenFinanciero.buscarPorId(1L)).thenReturn(resumen);
        when(response.getOutputStream()).thenReturn(outputStream);

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(response).setHeader(eq("Content-Disposition"),
                contains("estado_cuenta___.pdf"));
    }

    // Helper
    private ResumenFinanciero crearResumenConPDF(DocumentoPDF documento) {
        ResumenFinanciero resumen = new ResumenFinanciero();
        resumen.setId(1L);
        resumen.setIngresosTotales(5000.0);
        resumen.setGastosTotales(3000.0);
        resumen.setAhorroNeto(2000.0);
        resumen.setFechaPeriodoAnterior(LocalDate.of(2024, 1, 1));
        resumen.setFechaPeriodoActual(LocalDate.of(2024, 1, 31));
        resumen.setDocumentoPDF(documento);
        return resumen;
    }
}