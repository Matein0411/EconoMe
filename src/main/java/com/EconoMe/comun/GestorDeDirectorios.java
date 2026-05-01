package com.EconoMe.comun;

import jakarta.servlet.GenericServlet;

import java.io.File;

public class GestorDeDirectorios {

    public static String crearDirectorioTemporal(GenericServlet genericServlet) {
        // Crear directorio temporal si no existe
        String rutaDeSubida = genericServlet.getServletContext().getRealPath("") + File.separator + "temp";
        File uploadDir = new File(rutaDeSubida);
        if(!uploadDir.exists()){
            uploadDir.mkdir();
        }
        return rutaDeSubida;
    }
}
