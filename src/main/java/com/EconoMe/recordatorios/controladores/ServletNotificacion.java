    package com.EconoMe.recordatorios.controladores;

    import com.EconoMe.recordatorios.modelos.Recordatorio;
    import com.EconoMe.recordatorios.servicios.ServicioRecordatorio;
    import com.google.gson.Gson;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.annotation.WebServlet;
    import jakarta.servlet.http.HttpServlet;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;

    import java.io.IOException;
    import java.time.LocalDate;
    import java.util.*;

    @WebServlet("/notificaciones")
    public class ServletNotificacion extends HttpServlet {

        private ServicioRecordatorio servicioRecordatorio;
        private Gson gson;

        @Override
        public void init() {
            this.servicioRecordatorio = new ServicioRecordatorio();
            this.gson = new Gson();
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {

            // VALIDACIÓN DE SESIÓN (descomentar cuando tengas autenticación)
            /*
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuario") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                Map<String, String> error = new HashMap<>();
                error.put("error", "No autorizado");
                error.put("message", "Debe iniciar sesión para acceder a las notificaciones");

                response.getWriter().write(gson.toJson(error));
                return;
            }
            */

            try {
                // Obtener recordatorios activos
                List<Recordatorio> recordatoriosActivos = servicioRecordatorio.listarActivos();

                // Fecha de hoy
                LocalDate hoy = LocalDate.now();

                // Lista de notificaciones a mostrar
                List<Map<String, Object>> notificaciones = new ArrayList<>();

                // Filtrar recordatorios que deben notificarse hoy
                for (Recordatorio recordatorio : recordatoriosActivos) {
                    Optional<LocalDate> fechaNotificable = recordatorio.obtenerFechaNotificable(hoy);

                    if (fechaNotificable.isPresent()) {
                        Map<String, Object> notificacion = new HashMap<>();
                        notificacion.put("id", recordatorio.getId());
                        notificacion.put("descripcion", recordatorio.getDescripcion());
                        notificacion.put("monto", recordatorio.getMonto());
                        notificacion.put("fechaVencimiento", fechaNotificable.get().toString());
                        notificacion.put("recurrencia", recordatorio.getRecurrencia().name());
                        notificacion.put("diasDeAnticipacion", recordatorio.getDiasDeAnticipacion());

                        notificaciones.add(notificacion);
                    }
                }

                // Configurar respuesta JSON
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                // Enviar respuesta
                response.getWriter().write(gson.toJson(notificaciones));

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                Map<String, String> error = new HashMap<>();
                error.put("error", "Error interno del servidor");
                error.put("message", e.getMessage());

                response.getWriter().write(gson.toJson(error));
            }
        }
    }