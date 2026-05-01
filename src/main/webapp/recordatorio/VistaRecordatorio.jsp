<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<%-- 1. Incluimos el header que abre <html>, <body> y <main> --%>
<jsp:include page="/comun/VistaHeader.jsp">
    <jsp:param name="pageTitle" value="Mis Recordatorios"/>
</jsp:include>

<%-- 2. Aquí va el contenido específico de ESTA página --%>
<div class="page-header">
    <h1>Gestión de Recordatorios</h1>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/recordatorios/nuevo">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
        <span>Nuevo Recordatorio</span>
    </a>
</div>

<jsp:include page="/comun/Mensajes.jsp" />

<section class="controls">
    <div class="search-wrapper">
<%--        <input id="search-input" class="search" type="text" placeholder="Buscar por descripción..." />--%>
<%--        <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>--%>
    </div>
</section>

<c:if test="${empty recordatorios}">
    <div class="empty-state">
        <div class="empty-state-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M8 2v4"></path><path d="M16 2v4"></path><path d="M21 13V6a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h8"></path><path d="M3 10h18"></path><path d="M16 19h6"></path><path d="M19 16v6"></path></svg>
        </div>
        <h2>No hay recordatorios</h2>
        <p>¡Parece que no tienes ningún recordatorio registrado todavía. Anímate y crea el primero!</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/recordatorios/nuevo">Crear mi primer recordatorio</a>
    </div>
</c:if>

<section id="reminders-grid" class="grid">
    <c:forEach var="r" items="${recordatorios}">
        <article class="card" data-description="<c:out value='${r.descripcion}'/>">
            <div class="card-header">
                <div class="card-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
                </div>
                <h3 class="card-title">${r.descripcion}</h3>
            </div>
            <div class="card-body">
                <p><strong>Monto:</strong> <fmt:formatNumber value="${r.monto}" type="currency" currencySymbol="$" /></p>
                <p><strong>Recurrencia:</strong> ${r.recurrencia}</p>
                <p><strong>Período:</strong>
                    <fmt:parseDate value="${r.fechaInicio}" pattern="yyyy-MM-dd" var="parsedFechaInicio" />
                    <fmt:parseDate value="${r.fechaFin}" pattern="yyyy-MM-dd" var="parsedFechaFin" />
                    <fmt:formatDate value="${parsedFechaInicio}" pattern="dd/MM/yyyy" /> -
                    <fmt:formatDate value="${parsedFechaFin}" pattern="dd/MM/yyyy" />
                </p>
                <p><strong>Anticipación:</strong> ${r.diasDeAnticipacion} día(s)</p>
            </div>
            <div class="card-footer">
                <a href="${pageContext.request.contextPath}/recordatorios/editar?id=${r.id}" class="btn btn-secondary">
                        <%-- SVG de Editar --%>
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                    </svg>
                    Editar
                </a>
                <form class="delete-form"
                      action="${pageContext.request.contextPath}/recordatorios/borrar?id=${r.id}"
                      method="POST"
                      data-id="${r.id}"
                      data-descripcion="<c:out value='${r.descripcion}'/>"
                      data-monto="<fmt:formatNumber value='${r.monto}' type='currency' currencySymbol='$' />"
                      data-recurrencia="${r.recurrencia}">

                    <button type="button" class="btn btn-danger delete-btn">

                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="3 6 5 6 21 6"></polyline>
                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                        </svg>
                        Eliminar

                    </button>
                </form>
            </div>
        </article>
    </c:forEach>
</section>

<!-- Modal de confirmación de eliminación (Bootstrap) -->
<div class="modal fade" id="confirmDeleteModal" tabindex="-1" aria-labelledby="confirmDeleteModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="confirmDeleteModalLabel">Confirmar eliminación</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <p>¿Estás seguro de que deseas eliminar este recordatorio?</p>
        <ul>
          <li><strong>Descripción:</strong> <span id="modal-descripcion"></span></li>
          <li><strong>Monto:</strong> <span id="modal-monto"></span></li>
          <li><strong>Recurrencia:</strong> <span id="modal-recurrencia"></span></li>
        </ul>
        <p class="text-danger">Esta acción no se puede deshacer.</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
        <button type="button" class="btn btn-danger" id="confirmDeleteBtn">Confirmar eliminación</button>
      </div>
    </div>
  </div>
</div>

<!-- Formulario oculto único para enviar la eliminación por id -->
<form id="deleteRecordatorioForm" action="${pageContext.request.contextPath}/recordatorios/borrar" method="POST" style="display:none;">
    <input type="hidden" name="id" id="idRecordatorioToDelete" value="" />
</form>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const deleteForms = document.querySelectorAll('.delete-form');
        const modalDescripcion = document.getElementById('modal-descripcion');
        const modalMonto = document.getElementById('modal-monto');
        const modalRecurrencia = document.getElementById('modal-recurrencia');
        const idInput = document.getElementById('idRecordatorioToDelete');
        const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
        const confirmModalEl = document.getElementById('confirmDeleteModal');
        let currentModalInstance = null;

        deleteForms.forEach(form => {
            const deleteBtn = form.querySelector('.delete-btn');

            deleteBtn.addEventListener('click', function(e) {
                e.preventDefault();

                // Obtener datos desde data-attributes del formulario
                const id = form.getAttribute('data-id');
                const descripcion = form.getAttribute('data-descripcion');
                const monto = form.getAttribute('data-monto');
                const recurrencia = form.getAttribute('data-recurrencia');

                // Poblar modal
                if(modalDescripcion) modalDescripcion.textContent = descripcion || '';
                if(modalMonto) modalMonto.textContent = monto || '';
                if(modalRecurrencia) modalRecurrencia.textContent = recurrencia || '';

                // Guardar id en formulario oculto
                if(idInput) idInput.value = id || '';

                // Mostrar modal usando Bootstrap JS
                if (typeof bootstrap !== 'undefined') {
                    currentModalInstance = new bootstrap.Modal(confirmModalEl);
                    currentModalInstance.show();
                } else {
                    // Fallback: confirm() si Bootstrap no está disponible
                    const mensaje = `¿Estás seguro de que deseas eliminar este recordatorio?\n\n` +
                        `Esta acción no se puede deshacer.`;

                    const confirmado = window.confirm(mensaje);

                    if (confirmado) {
                        // enviar el formulario oculto como fallback
                        document.getElementById('deleteRecordatorioForm').submit();
                    }
                }
            });
        });

        // Al confirmar en el modal, enviar el formulario oculto
        if (confirmDeleteBtn) {
            confirmDeleteBtn.addEventListener('click', function() {
                document.getElementById('deleteRecordatorioForm').submit();
                if (currentModalInstance) currentModalInstance.hide();
            });
        }
    });
</script>

<%-- 3. Finalmente, incluimos el footer que cierra <main>, <body> y <html> --%>
<jsp:include page="/comun/VistaFooter.jsp" />