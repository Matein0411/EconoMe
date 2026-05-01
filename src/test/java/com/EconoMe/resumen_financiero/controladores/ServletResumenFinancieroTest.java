package com.EconoMe.resumen_financiero.controladores;

import com.EconoMe.resumen_financiero.dao.DAOResumenFinanciero;
import com.EconoMe.resumen_financiero.dao.DAODocumentoPDF;
import com.EconoMe.resumen_financiero.modelos.DocumentoPDF;
import com.EconoMe.resumen_financiero.modelos.ResumenFinanciero;
import com.EconoMe.resumen_financiero.servicios.ServicioResumenFinanciero;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServletsResumenFinancieroTest {

    // ============== ServletConsultarResumenesFinancieros ==============

    @Mock
    private ServicioResumenFinanciero servicioResumenFinanciero;

    @Mock
    private DAOResumenFinanciero daoResumenFinanciero;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private ServletOutputStream outputStream;

    @Mock
    private Part filePart;

    private ServletConsultarResumenesFinancieros servletConsultar;
    private ServletDescargaPDF servletDescarga;

    @BeforeEach
    void setUp() {
        servletConsultar = new ServletConsultarResumenesFinancieros() {
            @Override
            public void init() {
                try {
                    java.lang.reflect.Field field = ServletConsultarResumenesFinancieros.class
                            .getDeclaredField("servicioResumenFinanciero");
                    field.setAccessible(true);
                    field.set(this, servicioResumenFinanciero);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        servletConsultar.init();

        servletDescarga = new ServletDescargaPDF() {
            @Override
            public void init() throws ServletException {
                try {
                    java.lang.reflect.Field field = ServletDescargaPDF.class
                            .getDeclaredField("daoResumenFinanciero");
                    field.setAccessible(true);
                    field.set(this, daoResumenFinanciero);
                } catch (Exception e) {
                    throw new ServletException(e);
                }
            }
        };
        try {
            servletDescarga.init();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    // ========== TESTS SERVLET CONSULTAR ==========

    @Test
    void testConsultarResumenes_ConDatos() throws Exception {
        // Arrange
        List<ResumenFinanciero> resumenes = Arrays.asList(
                crearResumenDePrueba(1L, 5000.0, 3000.0),
                crearResumenDePrueba(2L, 6000.0, 4000.0)
        );

        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
        when(servicioResumenFinanciero.listarConDocumentosPDF()).thenReturn(resumenes);

        // Act
        servletConsultar.doGet(request, response);

        // Assert
        verify(request).setAttribute("ResumenesFinancieros", resumenes);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testConsultarResumenes_SinDatos() throws Exception {
        // Arrange
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
        when(servicioResumenFinanciero.listarConDocumentosPDF()).thenReturn(Collections.emptyList());

        // Act
        servletConsultar.doGet(request, response);

        // Assert
        verify(request).setAttribute("ResumenesFinancieros", Collections.emptyList());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testConsultarResumenes_ErrorEnServicio() throws Exception {
        // Arrange
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
        when(servicioResumenFinanciero.listarConDocumentosPDF())
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Act
        servletConsultar.doGet(request, response);

        // Assert
        verify(dispatcher).forward(request, response);
    }

    // ========== TESTS SERVLET DESCARGA PDF ==========

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
        servletDescarga.doGet(request, response);

        // Assert
        verify(response).setContentType("application/pdf");
        verify(response).setHeader(eq("Content-Disposition"), contains("estado_cuenta.pdf"));
        verify(response).setContentLength(pdfContent.length);
        verify(outputStream).write(pdfContent);
        verify(outputStream).flush();
    }

    @Test
    void testDescargarPDF_IdNull() throws Exception {
        // Arrange
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("resumenId")).thenReturn(null);
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servletDescarga.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/EconoMe/resumenes");
        verify(response, never()).getOutputStream();
    }

    @Test
    void testDescargarPDF_IdVacio() throws Exception {
        // Arrange
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("resumenId")).thenReturn("   ");
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servletDescarga.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/EconoMe/resumenes");
    }

    @Test
    void testDescargarPDF_IdInvalido() throws Exception {
        // Arrange
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("resumenId")).thenReturn("abc");
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servletDescarga.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/EconoMe/resumenes");
    }

    @Test
    void testDescargarPDF_ResumenNoEncontrado() throws Exception {
        // Arrange
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("resumenId")).thenReturn("999");
        when(daoResumenFinanciero.buscarPorId(999L)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servletDescarga.doGet(request, response);

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
        servletDescarga.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/EconoMe/resumenes");
    }

    @Test
    void testDescargarPDF_DocumentoSinContenido() throws Exception {
        // Arrange
        DocumentoPDF documentoVacio = new DocumentoPDF();
        documentoVacio.setNombre("test.pdf");
        documentoVacio.setArchivoPdf(null);

        ResumenFinanciero resumen = new ResumenFinanciero();
        resumen.setId(1L);
        resumen.setDocumentoPDF(documentoVacio);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter("resumenId")).thenReturn("1");
        when(daoResumenFinanciero.buscarPorId(1L)).thenReturn(resumen);
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servletDescarga.doGet(request, response);

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
        servletDescarga.doGet(request, response);

        // Assert
        verify(response).setHeader(eq("Content-Disposition"),
                contains("estado_cuenta___.pdf"));
    }

    @Test
    void testDescargarPDF_NombreSinExtension() throws Exception {
        // Arrange
        byte[] pdfContent = "PDF content".getBytes();
        DocumentoPDF documento = new DocumentoPDF("documento_sin_extension", pdfContent);
        ResumenFinanciero resumen = crearResumenConPDF(documento);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter("resumenId")).thenReturn("1");
        when(daoResumenFinanciero.buscarPorId(1L)).thenReturn(resumen);
        when(response.getOutputStream()).thenReturn(outputStream);

        // Act
        servletDescarga.doGet(request, response);

        // Assert
        verify(response).setHeader(eq("Content-Disposition"),
                contains(".pdf"));
    }

    @Test
    void testDescargarPDF_NombreNull() throws Exception {
        // Arrange
        byte[] pdfContent = "PDF content".getBytes();
        DocumentoPDF documento = new DocumentoPDF(null, pdfContent);
        ResumenFinanciero resumen = crearResumenConPDF(documento);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter("resumenId")).thenReturn("1");
        when(daoResumenFinanciero.buscarPorId(1L)).thenReturn(resumen);
        when(response.getOutputStream()).thenReturn(outputStream);

        // Act
        servletDescarga.doGet(request, response);

        // Assert
        verify(response).setHeader(eq("Content-Disposition"),
                contains("resumen_financiero.pdf"));
    }

    @Test
    void testDescargarPDF_ErrorAlObtenerOutputStream() throws Exception {
        // Arrange
        byte[] pdfContent = "PDF content".getBytes();
        DocumentoPDF documento = new DocumentoPDF("test.pdf", pdfContent);
        ResumenFinanciero resumen = crearResumenConPDF(documento);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter("resumenId")).thenReturn("1");
        when(daoResumenFinanciero.buscarPorId(1L)).thenReturn(resumen);
        when(response.getOutputStream()).thenThrow(new RuntimeException("Error de I/O"));
        when(request.getContextPath()).thenReturn("/EconoMe");

        // Act
        servletDescarga.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/EconoMe/resumenes");
    }

    @Test
    void testDescargarPDF_HeadersDeSeguridad() throws Exception {
        // Arrange
        byte[] pdfContent = "PDF content".getBytes();
        DocumentoPDF documento = new DocumentoPDF("test.pdf", pdfContent);
        ResumenFinanciero resumen = crearResumenConPDF(documento);

        when(request.getSession()).thenReturn(session);
        when(request.getParameter("resumenId")).thenReturn("1");
        when(daoResumenFinanciero.buscarPorId(1L)).thenReturn(resumen);
        when(response.getOutputStream()).thenReturn(outputStream);

        // Act
        servletDescarga.doGet(request, response);

        // Assert - Verificar headers de seguridad
        verify(response).setHeader("X-Content-Type-Options", "nosniff");
        verify(response).setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        verify(response).setHeader("Pragma", "no-cache");
        verify(response).setHeader("Expires", "0");
    }

    // ========== HELPERS ==========

    private ResumenFinanciero crearResumenDePrueba(Long id, double ingresos, double gastos) {
        ResumenFinanciero resumen = new ResumenFinanciero();
        resumen.setId(id);
        resumen.setIngresosTotales(ingresos);
        resumen.setGastosTotales(gastos);
        resumen.setAhorroNeto(ingresos - gastos);
        resumen.setFechaPeriodoAnterior(LocalDate.of(2024, 1, 1));
        resumen.setFechaPeriodoActual(LocalDate.of(2024, 1, 31));
        resumen.setDocumentoPDF(new DocumentoPDF("test.pdf", new byte[0]));
        return resumen;
    }

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