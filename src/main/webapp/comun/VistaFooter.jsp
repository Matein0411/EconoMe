<%@ page isELIgnored="false" %>

<%-- RUTA: /comun/VistaFooter.jsp --%>
</main> <%-- Cierra la etiqueta <main> abierta en VistaHeader.jsp --%>

<footer class="footer">
    <div class="container">
        <p>&copy; 2025 EconoMe. Todos los derechos reservados.</p>
    </div>
</footer>

<script>
    const CONTEXT_PATH = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/resources/js/app.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/toast-manager.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/modal-confirm.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/notificaciones.js"></script>
</body>
</html>