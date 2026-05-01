package com.EconoMe.resumen_financiero;

import com.EconoMe.resumen_financiero.modelos.DocumentoPDF;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class DAODocumentoPDFTest {

    @Test
    void given_nombreYBytes_when_guardarPDF_then_documentoCreado() {
        String nombre = "reporte_enero.pdf";
        byte[] archivoPdf = "contenido del pdf".getBytes();

        DocumentoPDF documento = new DocumentoPDF(nombre, archivoPdf, (long) archivoPdf.length);

        assertNotNull(documento);
        assertEquals(nombre, documento.getNombre());
        assertEquals(archivoPdf.length, documento.getTamanio());
        assertArrayEquals(archivoPdf, documento.getArchivoPdf());
    }

    @Test
    void given_pdfVacio_when_guardarPDF_then_tamanioEsCero() {
        String nombre = "vacio.pdf";
        byte[] archivoPdf = new byte[0];

        DocumentoPDF documento = new DocumentoPDF(nombre, archivoPdf, (long) archivoPdf.length);

        assertEquals(0, documento.getTamanio());
    }

    @Test
    void given_pdfGrande_when_guardarPDF_then_tamanioEsCorrecto() {
        String nombre = "grande.pdf";
        byte[] archivoPdf = new byte[1024 * 1024]; // 1 MB

        DocumentoPDF documento = new DocumentoPDF(nombre, archivoPdf, (long) archivoPdf.length);

        assertEquals(1024 * 1024, documento.getTamanio());
    }
}