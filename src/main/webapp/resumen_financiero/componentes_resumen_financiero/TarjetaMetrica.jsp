<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%-- Definir que esta página generará HTML con codificación UTF-8 --%>
<%@ page isELIgnored="false" %> <%--  Habilita Expression Language (EL) para usar sintaxis ${variable} -->
<%-- Taglibs: Importa bibliotecas JSTL --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %> <%-- c para control de flujo --%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> <%-- fmt para formateo de numeros y fechas --%>

<c:set var="tamanoFuente" value="${empty param.tamanoFuente ? '1.75rem' : param.tamanoFuente}"/>

<%-- Convertir el valor String a número --%>
<fmt:parseNumber var="valorNumerico" value="${param.valor}" type="number" pattern="#,##0.##" parseLocale="en_US"/>

<article class="card">
    <div class="card-header">
        <div class="card-icon" style="color: ${param.color};">
            <c:choose>
                <c:when test="${param.icono == 'ingresos'}">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="12" y1="1" x2="12" y2="23"></line>
                        <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
                    </svg>
                </c:when>
                <c:when test="${param.icono == 'gastos'}">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="15" y1="9" x2="9" y2="15"></line>
                        <line x1="9" y1="9" x2="15" y2="15"></line>
                    </svg>
                </c:when>
            </c:choose>
        </div>
        <h3 class="card-title">${param.titulo}</h3>
    </div>
    <div class="card-body">
        <p style="font-size: ${tamanoFuente}; font-weight: 600; color: ${param.color}; margin: 0;">
            <fmt:setLocale value="en_US"/>
            $<fmt:formatNumber value="${valorNumerico}" type="number" minFractionDigits="2" maxFractionDigits="2" />
        </p>
        <p style="margin-top: 0.5rem; color: var(--text-secondary); font-size: 0.9rem;">
            ${param.descripcion}
        </p>
    </div>
</article>