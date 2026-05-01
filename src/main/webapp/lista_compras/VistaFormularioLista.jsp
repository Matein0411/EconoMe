<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%-- 1. Incluimos el header --%>
<jsp:include page="/comun/VistaHeader.jsp">
    <jsp:param name="pageTitle" value="Nueva Lista de Compras"/>
</jsp:include>

<div class="form-container">

    <div class="page-header">
        <h1>Nueva Lista de Compras</h1>
    </div>

    <%-- 2. Bloque para mensajes --%>
    <jsp:include page="/comun/Mensajes.jsp" />

    <%-- 3. Formulario simple --%>
    <form method="POST" action="${pageContext.request.contextPath}/listas/crear">

        <div class="form-grid">
            <%-- Campo: Nombre de la lista --%>
            <div class="form-group full-width">
                <label for="nombre">Nombre de la lista *</label>
                <input type="text"
                       id="nombre"
                       name="nombre"
                       class="form-control"
                       placeholder="Ej: Compras del Supermercado"
                       required
                       autofocus>
            </div>
        </div>

        <%-- 4. Botones de Acción --%>
        <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/listas"
                       class="btn btn-secondary"
                       style="margin-right: 15px;">
                       Cancelar
                    </a>

                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-plus-circle"></i> Crear lista
                    </button>
        </div>
    </form>
</div>

<%-- 5. Incluimos el footer --%>
<jsp:include page="/comun/VistaFooter.jsp" />