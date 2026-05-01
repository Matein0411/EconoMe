<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%-- Definir que esta página generará HTML con codificación UTF-8 --%>
<%@ page isELIgnored="false" %> <%--  Habilita Expression Language (EL) para usar sintaxis ${variable} -->
<%-- Taglibs: Importa bibliotecas JSTL --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %> <%-- c para control de flujo --%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> <%-- fmt para formateo de numeros y fechas --%>

<%-- Parámetros: valor, tamanoFuente, descripcionPersonalizada --%>
<c:set var="tamanoFuente" value="${empty param.tamanoFuente ? '1.75rem' : param.tamanoFuente}"/>

<%-- Convertir el valor String a número --%>
<fmt:parseNumber var="valorNumerico" value="${param.valor}" type="number" pattern="#,##0.##" parseLocale="en_US"/>

<%-- Determinar color basado en el valor --%>
<c:choose>
    <c:when test="${valorNumerico >= 0}">
        <c:set var="color" value="#10b981"/>
        <c:set var="descripcion" value="${empty param.descripcionPersonalizada ? 'Balance positivo' : param.descripcionPersonalizada}"/>
    </c:when>
    <c:otherwise>
        <c:set var="color" value="#ef4444"/>
        <c:set var="descripcion" value="${empty param.descripcionPersonalizada ? 'Balance negativo' : param.descripcionPersonalizada}"/>
    </c:otherwise>
</c:choose>

<article class="card">
    <div class="card-header">
        <div class="card-icon" style="color: ${color};">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline>
            </svg>
        </div>
        <h3 class="card-title">Ahorro Neto</h3>
    </div>
    <div class="card-body">
        <p style="font-size: ${tamanoFuente}; font-weight: 600; color: ${color}; margin: 0;">
            <fmt:setLocale value="en_US"/>
            $<fmt:formatNumber value="${valorNumerico}" type="number" minFractionDigits="2" maxFractionDigits="2" />
        </p>
        <p style="margin-top: 0.5rem; color: var(--text-secondary); font-size: 0.9rem;">
            ${descripcion}
        </p>
    </div>
</article>