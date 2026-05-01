package com.EconoMe.comun;

import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class GestorDeArchivos {

    public static Part obtenerArchivo(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Part archivoDeFormulario = request.getPart("archivoPDF"); // Obtener archivo del formulario

        if(archivoDeFormulario == null || archivoDeFormulario.getSize() == 0){ // Validar archivo
            request.setAttribute("error", "Por favor selecciona un archivo PDF");
            request.getRequestDispatcher("/VistaResumenFinanciero.jsp").forward(request, response);
            return null;
        }
        return archivoDeFormulario;
    }

    public static void eliminarArchivo(String rutaDeArchivo) {
        new File(rutaDeArchivo).delete(); // Eliminar archivo
    }

    public static byte[] transformarArchivoABytes(Part filePart) throws IOException {
        byte[] archivoBytes;
        try (InputStream inputStream = filePart.getInputStream()){ // Leer archivo PDF como bytes[]
            archivoBytes = inputStream.readAllBytes();
        }
        return archivoBytes;
    }

    public static void guardarArchivoTemporal(Part filePart, String filePath) {
        try (InputStream inputStream = filePart.getInputStream()){
            Files.copy(inputStream, new File(filePath).toPath(), // guardar el archivo en una ubicación
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String obtenerRutaDeArchivoTemporal(GenericServlet genericServlet, HttpServletRequest request,
                                                      HttpServletResponse response) throws ServletException, IOException {
        String rutaDeSubida = GestorDeDirectorios.crearDirectorioTemporal(genericServlet);
        String nombreArchivoTemporal = "temp_" + System.currentTimeMillis() + ".pdf";
        String rutaDelArchivo = rutaDeSubida + File.separator + nombreArchivoTemporal;

        Part archivoDeFormulario = obtenerArchivo(request, response);
        if (archivoDeFormulario == null) return null;

        guardarArchivoTemporal(archivoDeFormulario, rutaDelArchivo);
        return rutaDelArchivo;
    }
}
