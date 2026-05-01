package com.EconoMe.comun.mensajes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class MensajeUtil {
    private static final String ATRIBUTO_MENSAJES = "mensajes";

    /**
     * Agrega un mensaje a la sesión
     */
    public static void agregarMensaje(HttpSession session, TipoMensaje tipo, String texto) {
        List<Mensaje> mensajes = obtenerMensajes(session);
        mensajes.add(new Mensaje(tipo, texto));
        session.setAttribute(ATRIBUTO_MENSAJES, mensajes);
    }

    /**
     * Métodos de conveniencia para tipos específicos
     */
    public static void agregarExito(HttpSession session, String texto) {
        agregarMensaje(session, TipoMensaje.EXITO, texto);
    }

    public static void agregarError(HttpSession session, String texto) {
        agregarMensaje(session, TipoMensaje.ERROR, texto);
    }

    public static void agregarAdvertencia(HttpSession session, String texto) {
        agregarMensaje(session, TipoMensaje.ADVERTENCIA, texto);
    }

    public static void agregarInfo(HttpSession session, String texto) {
        agregarMensaje(session, TipoMensaje.INFO, texto);
    }

    /**
     * Obtiene los mensajes de la sesión
     */
    @SuppressWarnings("unchecked")
    private static List<Mensaje> obtenerMensajes(HttpSession session) {
        List<Mensaje> mensajes = (List<Mensaje>) session.getAttribute(ATRIBUTO_MENSAJES);
        if (mensajes == null) {
            mensajes = new ArrayList<>();
        }
        return mensajes;
    }

    /**
     * Obtiene y limpia los mensajes (para mostrarlos una sola vez)
     */
    @SuppressWarnings("unchecked")
    public static List<Mensaje> obtenerYLimpiarMensajes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return new ArrayList<>();
        }

        List<Mensaje> mensajes = (List<Mensaje>) session.getAttribute(ATRIBUTO_MENSAJES);
        if (mensajes == null) {
            mensajes = new ArrayList<>();
        }

        // Limpiamos los mensajes después de obtenerlos
        session.removeAttribute(ATRIBUTO_MENSAJES);

        // Los ponemos en el request para que estén disponibles en el JSP
        request.setAttribute(ATRIBUTO_MENSAJES, mensajes);

        return mensajes;
    }
}