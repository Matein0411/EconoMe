package com.EconoMe.resumen_financiero.servicios;

import com.EconoMe.comun.ExtractorTexto;
import com.EconoMe.comun.GestorDeArchivos;
import com.EconoMe.resumen_financiero.dao.DAOResumenFinanciero;
import com.EconoMe.resumen_financiero.modelos.DocumentoPDF;
import com.EconoMe.resumen_financiero.modelos.ResumenFinanciero;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ServicioResumenFinanciero {

    private static final int POSICION_GRUPO_PARENTESIS = 1;
    private static final DateTimeFormatter FORMATTER_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final DAOResumenFinanciero daoResumenFinanciero;

    public ServicioResumenFinanciero() {
        this.daoResumenFinanciero = new DAOResumenFinanciero();
    }

    public static Double extraerMonto(String patron, String textoPDF) {
        try {
            String fragmento = ExtractorTexto.extraerFragmentoDeUnTexto(textoPDF, patron, POSICION_GRUPO_PARENTESIS);
            if (fragmento == null) {
                return null;
            }
            return Double.parseDouble(fragmento);
        } catch (NumberFormatException e) {
            System.err.println("Error al parsear monto: " + e.getMessage());
            return null;
        }
    }

    // Extrae una fecha del texto del PDF usando el patrón proporcionado
    public static LocalDate extraerFecha(String patron, String textoPDF) {
        try {
            String fragmento = ExtractorTexto.extraerFragmentoDeUnTexto(textoPDF, patron, POSICION_GRUPO_PARENTESIS);
            if (fragmento == null) {
                return null;
            }
            return LocalDate.parse(fragmento, FORMATTER_FECHA);
        } catch (DateTimeParseException e) {
            System.err.println("Error al parsear fecha con patrón " + patron + ": " + e.getMessage());
            return null;
        }
    }

    public static ResumenFinanciero procesarInformacion(String rutaArchivo, DocumentoPDF documentoPDF) {
        try {
            // Extraer texto del PDF
            String textoPDF = ExtractorTexto.extraerTextoDePDF(rutaArchivo);
            if (textoPDF == null || textoPDF.trim().isEmpty()) {
                System.err.println("No se pudo extraer texto del PDF");
                return null;
            }

            // Patrones de extracción
            String patronIngresos = "DEPÓSITO / CRÉDITOS\\s*\\(\\d+\\)\\s+(\\d+\\.\\d+)";
            String patronGastos = "CHEQUES / DÉBITOS\\s*\\(\\d+\\)\\s+(\\d+\\.\\d+)";
            String patronFechaPeriodoAnterior = "FECHA ÚLTIMO CORTE\\s*\\(FACTURA\\)\\s*(\\d{2}-\\d{2}-\\d{4})";
            String patronFechaPeriodoActual = "FECHA ESTE CORTE\\s*\\(FACTURA\\)\\s*(\\d{2}\\-\\d{2}\\-\\d{4})";

            // Extraer información
            Double ingresos = extraerMonto(patronIngresos, textoPDF);
            if (ingresos == null) {
                System.err.println("No se pudieron extraer los ingresos");
                return null;
            }

            Double gastos = extraerMonto(patronGastos, textoPDF);
            if (gastos == null) {
                System.err.println("No se pudieron extraer los gastos");
                return null;
            }

            LocalDate fechaPeriodoAnterior = extraerFecha(patronFechaPeriodoAnterior, textoPDF);
            if (fechaPeriodoAnterior == null) {
                System.err.println("No se pudo extraer la fecha del período anterior");
                return null;
            }

            LocalDate fechaPeriodoActual = extraerFecha(patronFechaPeriodoActual, textoPDF);
            if (fechaPeriodoActual == null) {
                System.err.println("No se pudo extraer la fecha del período actual");
                return null;
            }

            // Eliminar archivo temporal
            GestorDeArchivos.eliminarArchivo(rutaArchivo);

            // Crear y retornar el resumen financiero
            ResumenFinanciero resumen = new ResumenFinanciero(ingresos, gastos, fechaPeriodoAnterior, fechaPeriodoActual, documentoPDF);
            return resumen;

        } catch (Exception e) {
            System.err.println("Error al procesar información del PDF: " + e.getMessage());
            e.printStackTrace();
            try {
                GestorDeArchivos.eliminarArchivo(rutaArchivo);
            } catch (Exception ex) {
                System.err.println("Error al eliminar archivo temporal: " + ex.getMessage());
            }
            return null;
        }
    }

    /**
     * Lista todos los resúmenes financieros con sus documentos PDF
     */
    public List<ResumenFinanciero> listarConDocumentosPDF() {
        return daoResumenFinanciero.listarConDocumentosPDF();
    }
}