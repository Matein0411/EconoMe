<%-- webapp/comun/Mensajes.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<c:if test="${not empty mensajes}">
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            <c:forEach var="mensaje" items="${mensajes}">
            ToastManager.show(
                '<c:out value="${mensaje.texto}" escapeXml="true"/>',
                '${mensaje.tipoString}'
            );
            </c:forEach>
        });
    </script>
</c:if>