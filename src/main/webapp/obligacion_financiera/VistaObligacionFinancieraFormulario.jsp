<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%-- 1. Incluimos el header --%>
<jsp:include page="/comun/VistaHeader.jsp">
    <jsp:param name="pageTitle" value="Nueva Obligación Financiera"/>
</jsp:include>

<%-- 2. Contenido del Formulario --%>
<div class="form-container">

    <div class="page-header">
        <h1>Nueva Obligación Financiera</h1>
        <a href="${pageContext.request.contextPath}/obligacion_financiera/deudas" class="btn btn-secondary">Cancelar</a>
    </div>

    <jsp:include page="/comun/Mensajes.jsp" />

    <form method="POST" action="${pageContext.request.contextPath}/obligacion_financiera/deudas">
        <input type="hidden" name="accion" value="registrar">

        <div class="form-grid">
            <%-- Campo: Tipo de Obligación --%>
            <div class="form-group">
                <label for="tipo">
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <path d="M16 8h-6a2 2 0 1 0 0 4h4a2 2 0 1 1 0 4H8"></path>
                        <path d="M12 18V6"></path>
                    </svg>
                    Tipo *
                </label>
                <select id="tipo" name="tipo" required>
                    <option value="">Seleccione un tipo</option>
                    <option value="DEUDA" ${param.tipo == 'DEUDA' ? 'selected' : ''}>Deuda (Debo dinero)</option>
                    <option value="PRESTAMO" ${param.tipo == 'PRESTAMO' ? 'selected' : ''}>Préstamo (Me deben dinero)</option>
                </select>
            </div>

            <%-- Campo: Nombre de la Persona --%>
            <div class="form-group">
                <label for="nombrePersona">
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                        <circle cx="12" cy="7" r="4"></circle>
                    </svg>
                    Persona *
                </label>
                <input
                        type="text"
                        id="nombrePersona"
                        name="nombrePersona"
                        value="${param.nombrePersona}"
                        placeholder="Ej: Juan Pérez"
                        required
                        maxlength="100"
                />
            </div>

            <%-- Campo: Monto Total --%>
            <div class="form-group">
                <label for="montoTotal">
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="12" y1="1" x2="12" y2="23"></line>
                        <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
                    </svg>
                    Monto Total *
                </label>
                <input
                        type="number"
                        id="montoTotal"
                        name="montoTotal"
                        value="${param.montoTotal}"
                        placeholder="0.00"
                        step="0.01"
                        min="0.01"
                        onblur="if(this.value) this.value = parseFloat(this.value).toFixed(2)"
                        required
                />
            </div>

            <%-- Campo: Fecha de Pago --%>
            <div class="form-group">
                <label for="fechaPago">
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                        <line x1="16" y1="2" x2="16" y2="6"></line>
                        <line x1="8" y1="2" x2="8" y2="6"></line>
                        <line x1="3" y1="10" x2="21" y2="10"></line>
                    </svg>
                    Fecha de Pago *
                </label>
                <input
                        type="date"
                        id="fechaPago"
                        name="fechaPago"
                        value="${param.fechaPago}"
                        required
                />
            </div>
        </div>

        <%-- Botones de Acción --%>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="20 6 9 17 4 12"></polyline>
                </svg>
                <span>Registrar</span>
            </button>
        </div>
    </form>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const form = document.querySelector('form');
        const montoInput = document.getElementById('montoTotal');
        const tipoSelect = document.getElementById('tipo');

        // Cambiar color del icono según el tipo
        tipoSelect.addEventListener('change', function() {
            const formGroup = this.closest('.form-group');
            const svg = formGroup.querySelector('svg');

            if (this.value === 'DEUDA') {
                svg.style.color = '#c33';
            } else if (this.value === 'PRESTAMO') {
                svg.style.color = '#3c3';
            } else {
                svg.style.color = '';
            }
        });

        // Validación del monto antes de enviar
        form.addEventListener('submit', function(e) {
            const monto = parseFloat(montoInput.value);

            if (isNaN(monto) || monto <= 0) {
                e.preventDefault();
                alert('El monto debe ser un número mayor a cero');
                montoInput.focus();
                return false;
            }
        });

        // Formatear el input de monto
        montoInput.addEventListener('blur', function() {
            const value = parseFloat(this.value);
            if (!isNaN(value)) {
                this.value = value.toFixed(2);
            }
        });
    });
</script>

<%-- 3. Incluimos el footer --%>
<jsp:include page="/comun/VistaFooter.jsp" />