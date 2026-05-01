<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%-- 1. Determinar si estamos en modo de edición o creación --%>
<c:set var="modoEdicion" value="${not empty recordatorio.id}" />

<%-- 2. Incluimos el header --%>
<jsp:include page="/comun/VistaHeader.jsp">
    <jsp:param name="pageTitle" value="${modoEdicion ? 'Editar Recordatorio' : 'Nuevo Recordatorio'}"/>
</jsp:include>

<%-- 3. Contenido del Formulario --%>
<div class="form-container">

    <div class="page-header">
        <h1>${modoEdicion ? 'Editar Recordatorio' : 'Nuevo Recordatorio'}</h1>
        <a href="${pageContext.request.contextPath}/recordatorios" class="btn btn-secondary">Cancelar</a>
    </div>

    <jsp:include page="/comun/Mensajes.jsp" />

    <form method="POST" action="${pageContext.request.contextPath}/recordatorios">

        <%-- Campos ocultos para el modo de edición --%>
        <c:if test="${modoEdicion}">
            <input type="hidden" name="id" value="${recordatorio.id}">
            <%-- Para simular un método PUT en el servlet --%>
            <input type="hidden" name="_method" value="PUT">
        </c:if>

        <div class="form-grid">
            <%-- Campo: Descripción --%>
            <div class="form-group">
                <label for="descripcion">Descripción</label>
                <input type="text" id="descripcion" name="descripcion"
                       value="${not empty recordatorio.descripcion ? recordatorio.descripcion : param.descripcion}"
                       required placeholder="Ej: Pago de Netflix">
            </div>

            <%-- Campo: Monto --%>
            <div class="form-group">
                <label for="monto">Monto</label>
                <input type="number" id="monto" name="monto"
                       value="${not empty recordatorio.monto ? recordatorio.monto : param.monto}"
                       required step="0.01" min="0" placeholder="Ej: 12.99">
            </div>

            <%-- Campo: Recurrencia --%>
            <div class="form-group">
                <label for="recurrencia">Recurrencia</label>
                <select id="recurrencia" name="recurrencia" required>
                    <c:forEach var="rec" items="${recurrencias}">
                        <option value="${rec}"
                                <c:if test="${rec.name() == (not empty recordatorio.recurrencia ? recordatorio.recurrencia.name() : param.recurrencia)}">selected</c:if>>
                                ${rec}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <%-- Campo: Días de Anticipación --%>
            <div class="form-group">
                <label for="diasDeAnticipacion">Días de Anticipación</label>
                <input type="number" id="diasDeAnticipacion" name="diasDeAnticipacion"
                       value="${not empty recordatorio.diasDeAnticipacion ? recordatorio.diasDeAnticipacion : param.diasDeAnticipacion}"
                       required min="0" placeholder="Ej: 3">
            </div>

            <%-- Campo: Fecha de Inicio --%>
            <div class="form-group">
                <label for="fechaInicio">Fecha de Inicio</label>
                <input type="date" id="fechaInicio" name="fechaInicio"
                       value="${not empty recordatorio.fechaInicio ? recordatorio.fechaInicio : param.fechaInicio}"
                       required>
            </div>

            <%-- Campo: Fecha de Fin --%>
            <div class="form-group">
                <label for="fechaFin">Fecha de Fin</label>
                <input type="date" id="fechaFin" name="fechaFin"
                       value="${not empty recordatorio.fechaFin ? recordatorio.fechaFin : param.fechaFin}"
                       required>
            </div>
        </div>

        <%-- Botones de Acción --%>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">
                ${modoEdicion ? 'Actualizar' : 'Registrar'}
            </button>
        </div>
    </form>
</div>


<%-- 4. Finalmente, incluimos el footer --%>
<jsp:include page="/comun/VistaFooter.jsp" />