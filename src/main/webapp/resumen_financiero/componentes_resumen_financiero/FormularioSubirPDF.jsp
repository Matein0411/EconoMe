<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<%-- 1. Header --%>
<jsp:include page="/comun/VistaHeader.jsp">
    <jsp:param name="title" value="Registrar Resumen Financiero"/>
</jsp:include>

<%-- 2. Variables Reutilizables --%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<%-- 3. Encabezado Principal --%>
<div class="page-header">
    <h1>Registrar Resumen Financiero</h1>
    <a href="${contextPath}/resumen_financiero/consultarResumenes" class="btn btn-secondary">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
        </svg>
        <span>Consultar Resúmenes</span>
    </a>
</div>

<%-- 4. Mensajes --%>
<jsp:include page="/comun/Mensajes.jsp" />

<%-- 5. Formulario con estilo empty-state --%>
<div class="empty-state">
    <div class="empty-state-icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" y1="13" x2="8" y2="13"></line>
            <line x1="16" y1="17" x2="8" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
        </svg>
    </div>
    <h2>Sube tu estado de cuenta</h2>
    <p>Envía tu archivo PDF del estado de cuenta bancario</p>

    <form action="${contextPath}/resumenes/subirPDF" method="post" enctype="multipart/form-data" style="max-width: 500px; margin: 2rem auto;">
        <div class="form-group">
            <label for="archivoPDF" class="btn btn-secondary" style="cursor: pointer; display: inline-block; margin-bottom: 1rem;">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="vertical-align: middle; margin-right: 0.5rem;">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                    <polyline points="7 10 12 15 17 10"></polyline>
                    <line x1="12" y1="15" x2="12" y2="3"></line>
                </svg>
                Seleccionar Archivo PDF
            </label>
            <input type="file" id="archivoPDF" name="archivoPDF" accept="application/pdf" required style="display: none;" onchange="updateFileName(this)">
            <p id="file-name" style="color: var(--text-primary); margin-top: 0.5rem; font-size: 0.9rem;">Ningún archivo seleccionado</p>
        </div>

        <div style="text-align: center; margin-top: 1.5rem;">
            <button type="submit" class="btn btn-primary">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                    <polyline points="17 8 12 3 7 8"></polyline>
                    <line x1="12" y1="3" x2="12" y2="15"></line>
                </svg>
                <span>Procesar PDF</span>
            </button>
        </div>
    </form>
</div>

<%-- 6. Scripts --%>
<script>
    function updateFileName(input) {
        const fileName = input.files[0]?.name || 'Ningún archivo seleccionado';
        document.getElementById('file-name').textContent = fileName;
    }

    window.addEventListener('load', function() {
        const fileInput = document.getElementById('archivoPDF');
        if (fileInput) {
            fileInput.value = '';
            document.getElementById('file-name').textContent = 'Ningún archivo seleccionado';
        }
    });
</script>

<%-- 7. Footer --%>
<jsp:include page="/comun/VistaFooter.jsp"/>