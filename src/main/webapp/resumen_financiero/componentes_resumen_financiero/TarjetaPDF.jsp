<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%-- Definir que esta página generará HTML con codificación UTF-8 --%>
<%@ page isELIgnored="false" %> <%--  Habilita Expression Language (EL) para usar sintaxis ${variable} -->
<%-- Taglibs: Importa bibliotecas JSTL --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %> <%-- c para control de flujo --%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> <%-- fmt para formateo de numeros y fechas --%>

<%-- Usar pageContext para obtener el contextPath --%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<article class="card">
    <div class="card-header">
        <div class="card-icon" style="color: #3b82f6;">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
                <line x1="16" y1="13" x2="8" y2="13"></line>
                <line x1="16" y1="17" x2="8" y2="17"></line>
                <polyline points="10 9 9 9 8 9"></polyline>
            </svg>
        </div>
        <h3 class="card-title">Documento PDF</h3>
    </div>
    <div class="card-body">
        <div style="text-align: center;">
            <p style="font-weight: 600; color: var(--text-primary); margin: 0.25rem 0;">
                ${param.documentoNombre}
            </p>
            <p style="color: var(--text-secondary); font-size: 0.9rem; margin: 0.25rem 0;">
                <%-- Convertir tamaño a número antes de dividir --%>
                <fmt:parseNumber var="tamanioKB" value="${param.documentoTamanio / 1024}" integerOnly="true"/>
                ${tamanioKB} KB
            </p>
        </div>
        <div style="margin-top: 1rem; display: flex; gap: 0.5rem; justify-content: center;">
            <a href="${contextPath}/descargarPDF?id=${param.documentoId}"
               class="btn btn-primary" style="padding: 0.5rem 1rem; font-size: 0.9rem;">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                    <polyline points="7 10 12 15 17 10"></polyline>
                    <line x1="12" y1="15" x2="12" y2="3"></line>
                </svg>
                Descargar
            </a>
        </div>
    </div>
</article>