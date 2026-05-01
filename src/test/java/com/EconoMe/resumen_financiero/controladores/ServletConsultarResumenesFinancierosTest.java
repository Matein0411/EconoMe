package com.EconoMe.resumen_financiero.controladores;

import com.EconoMe.resumen_financiero.modelos.DocumentoPDF;
import com.EconoMe.resumen_financiero.modelos.ResumenFinanciero;
import com.EconoMe.resumen_financiero.servicios.ServicioResumenFinanciero;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServletConsultarResumenesFinancierosTest {

    @Mock
    private ServicioResumenFinanciero servicioResumenFinanciero;

    private ServletConsultarResumenesFinancieros servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        servlet = new ServletConsultarResumenesFinancieros() {
            @Override
            public void init() {
                // Inyectar el mock en lugar de crear la instancia real
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
        servlet.init();
    }

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
        servlet.doGet(request, response);

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
        servlet.doGet(request, response);

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
        servlet.doGet(request, response);

        // Assert
        verify(dispatcher).forward(request, response);
    }

    // Helper
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
}