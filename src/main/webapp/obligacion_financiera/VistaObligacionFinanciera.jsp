<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<%-- 1. Incluimos el header --%>
<jsp:include page="/comun/VistaHeader.jsp">
    <jsp:param name="pageTitle" value="Gestión - Deudas y Préstamos"/>
</jsp:include>

<style>
    /* Variables de color mejoradas */
    :root {
        --primary-color: #4F46E5;
        --primary-hover: #4338CA;
        --deuda-color: #EF4444;
        --prestamo-color: #10B981;
        --background-dark: #121826;
        --card-background: #1E293B;
        --card-hover: #334155;
        --text-primary: #F1F5F9;
        --text-secondary: #94A3B8;
        --border-color: #334155;
    }

    /* Contenedor principal */
    body {
        background: #121826;
        min-height: 100vh;
        color: var(--text-primary);
    }

    /* Header de página - estilo simple como en detalle de cuenta */
    .page-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 2rem;
    }

    .page-header h1 {
        font-size: 2rem;
        font-weight: 700;
        color: var(--text-primary);
        margin: 0;
    }

    /* Botones mejorados */
    .btn {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem 1.5rem;
        border-radius: 8px;
        font-weight: 600;
        text-decoration: none;
        transition: all 0.3s ease;
        border: none;
        cursor: pointer;
        font-size: 0.95rem;
    }

    .btn-primary {
        background: var(--primary-color);
        color: white;
        box-shadow: 0 4px 12px rgba(79, 70, 229, 0.4);
    }

    .btn-primary:hover {
        background: var(--primary-hover);
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(79, 70, 229, 0.5);
    }

    .btn-secondary {
        background: var(--card-hover);
        color: var(--text-primary);
        border: 1px solid var(--border-color);
    }

    .btn-secondary:hover {
        background: var(--border-color);
        transform: translateY(-2px);
    }

    .btn-full {
        width: 100%;
        justify-content: center;
    }

    /* Estado vacío mejorado */
    .empty-state {
        text-align: center;
        padding: 4rem 2rem;
        background: rgba(255, 255, 255, 0.03);
        border: 1px dashed rgba(255, 255, 255, 0.1);
        border-radius: 16px;
        margin: 2rem 0;
    }

    .empty-state-icon {
        margin-bottom: 1.5rem;
        color: var(--text-secondary);
    }

    .empty-state h2 {
        font-size: 1.75rem;
        margin-bottom: 0.75rem;
        color: var(--text-primary);
    }

    .empty-state p {
        color: var(--text-secondary);
        font-size: 1.1rem;
        margin-bottom: 2rem;
    }

    /* Grid mejorado - más compacto */
    .grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
        gap: 1.25rem;
        margin-top: 2rem;
    }

    /* Cards mejoradas - más compactas */
    .card {
        background: rgba(255, 255, 255, 0.05);
        backdrop-filter: blur(10px);
        border-radius: 12px;
        border: 1px solid rgba(255, 255, 255, 0.1);
        overflow: hidden;
        transition: all 0.3s ease;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    }

    .card:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
    }

    /* Header de card mejorado - más compacto */
    .card-header {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 1rem 1.25rem;
        background: rgba(79, 70, 229, 0.05);
        border-bottom: 1px solid rgba(255, 255, 255, 0.08);
    }

    .card-icon {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
    }

    .icon-deuda {
        background: rgba(239, 68, 68, 0.15);
        color: var(--deuda-color);
    }

    .icon-prestamo {
        background: rgba(16, 185, 129, 0.15);
        color: var(--prestamo-color);
    }

    .card-title {
        font-size: 1.15rem;
        font-weight: 700;
        margin: 0;
        color: var(--text-primary);
    }

    /* Body de card mejorado - más compacto */
    .card-body {
        padding: 1.25rem;
    }

    .card-body p {
        margin: 0.5rem 0;
        color: var(--text-secondary);
        font-size: 0.875rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .card-body strong {
        color: var(--text-primary);
        font-weight: 600;
    }

    /* Sección de montos - más compacta */
    .amounts-section {
        background: rgba(79, 70, 229, 0.05);
        padding: 0.75rem;
        border-radius: 8px;
        margin: 0.75rem 0;
        border-left: 3px solid var(--primary-color);
    }

    .amounts-section p {
        margin: 0.4rem 0;
    }

    .amount {
        font-size: 1rem;
    }

    .balance {
        color: var(--primary-color);
        font-weight: 700;
        font-size: 1.1rem;
    }

    /* Barra de progreso mejorada - más compacta */
    .progress-container {
        margin: 1rem 0 0.5rem 0;
    }

    .progress-bar {
        width: 100%;
        height: 10px;
        background: var(--border-color);
        border-radius: 5px;
        overflow: hidden;
        box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.2);
    }

    .progress-fill {
        height: 100%;
        background: linear-gradient(90deg, #10B981 0%, #059669 100%);
        border-radius: 5px;
        transition: width 0.5s ease;
        box-shadow: 0 0 8px rgba(16, 185, 129, 0.4);
    }

    .progress-text {
        display: block;
        text-align: center;
        margin-top: 0.4rem;
        font-weight: 600;
        color: var(--text-primary);
        font-size: 0.8rem;
    }

    /* Footer de card mejorado - más compacto */
    .card-footer {
        padding: 1rem 1.25rem;
        background: rgba(0, 0, 0, 0.15);
        border-top: 1px solid rgba(255, 255, 255, 0.08);
    }

    /* Formulario de abono mejorado - más compacto */
    .abono-form {
        display: flex;
        flex-direction: column;
        gap: 0.75rem;
    }

    .abono-inputs {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 0.6rem;
    }

    .btn-abonar {
        grid-column: 1 / -1;
        width: fit-content;
        justify-self: center;
        padding: 0.7rem 2rem;
    }

    .select-cuenta,
    .input-monto {
        width: 100%;
        padding: 0.7rem;
        border: 1px solid var(--border-color);
        border-radius: 8px;
        background: var(--background-dark);
        color: var(--text-primary);
        font-size: 0.875rem;
        transition: all 0.3s ease;
    }

    .select-cuenta:focus,
    .input-monto:focus {
        outline: none;
        border-color: var(--primary-color);
        box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.2);
    }

    .select-cuenta option {
        background: var(--background-dark);
        color: var(--text-primary);
    }

    .input-monto::placeholder {
        color: var(--text-secondary);
    }

    /* Responsive mejorado */
    @media (max-width: 768px) {
        .grid {
            grid-template-columns: 1fr;
            gap: 1rem;
        }

        .page-header {
            flex-direction: column;
            gap: 1rem;
            align-items: flex-start;
        }

        .page-header h1 {
            font-size: 1.5rem;
        }

        .abono-inputs {
            grid-template-columns: 1fr;
        }

        .btn-abonar {
            grid-column: auto;
        }
    }

    /* Animaciones sutiles */
    @keyframes fadeIn {
        from {
            opacity: 0;
            transform: translateY(10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .card {
        animation: fadeIn 0.4s ease-out;
    }

    .card:nth-child(1) { animation-delay: 0.05s; }
    .card:nth-child(2) { animation-delay: 0.1s; }
    .card:nth-child(3) { animation-delay: 0.15s; }
    .card:nth-child(4) { animation-delay: 0.2s; }
</style>

<%-- 2. Contenido específico --%>
<div class="page-header">
    <h1>Gestión de Deudas y Préstamos</h1>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/obligacion_financiera/nuevo">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        <span>Nueva Obligación</span>
    </a>
</div>

<jsp:include page="/comun/Mensajes.jsp" />

<%-- Estado vacío --%>
<c:if test="${empty deudas}">
    <div class="empty-state">
        <div class="empty-state-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <path d="M16 8h-6a2 2 0 1 0 0 4h4a2 2 0 1 1 0 4H8"></path>
                <path d="M12 18V6"></path>
            </svg>
        </div>
        <h2>No hay obligaciones financieras</h2>
        <p>No tienes deudas ni préstamos registrados. ¡Crea tu primera obligación financiera!</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/obligacion_financiera/nuevo">Registrar obligación</a>
    </div>
</c:if>

<%-- Grid de obligaciones --%>
<c:if test="${not empty deudas}">
    <section class="grid">
        <c:forEach var="deuda" items="${deudas}">
            <article class="card">
                <div class="card-header">
                    <div class="card-icon ${deuda.getClass().simpleName == 'Deuda' ? 'icon-deuda' : 'icon-prestamo'}">
                        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <circle cx="12" cy="12" r="10"></circle>
                            <path d="M16 8h-6a2 2 0 1 0 0 4h4a2 2 0 1 1 0 4H8"></path>
                            <path d="M12 18V6"></path>
                        </svg>
                    </div>
                    <h3 class="card-title"><c:out value="${deuda.nombrePersona}"/></h3>
                </div>

                <div class="card-body">
                    <p><strong>Tipo:</strong> <span>${deuda.getClass().simpleName}</span></p>
                    <p><strong>Estado:</strong> <span>${deuda.estado}</span></p>

                    <div class="amounts-section">
                        <p><strong>Monto Total:</strong> <span><fmt:formatNumber value="${deuda.montoTotal}" type="currency" currencySymbol="$" /></span></p>
                        <p><strong>Monto Pagado:</strong> <span><fmt:formatNumber value="${deuda.montoPagado}" type="currency" currencySymbol="$" /></span></p>
                        <p class="amount"><strong>Saldo Pendiente:</strong> <span class="balance"><fmt:formatNumber value="${deuda.calcularSaldoPendiente()}" type="currency" currencySymbol="$" /></span></p>
                        <p><strong>Fecha de Pago:</strong> <span>${deuda.fechaPago}</span></p>
                    </div>

                        <%-- Progreso del pago --%>
                    <c:set var="porcentajePagado" value="${(deuda.montoPagado / deuda.montoTotal) * 100}" />
                    <div class="progress-container">
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: ${porcentajePagado}%"></div>
                        </div>
                        <span class="progress-text">
                            <fmt:formatNumber value="${porcentajePagado}" pattern="#0" />% pagado
                        </span>
                    </div>
                </div>

                <div class="card-footer">
                    <form method="post" action="deudas" class="abono-form">
                        <input type="hidden" name="accion" value="abonar">
                        <input type="hidden" name="idDeuda" value="${deuda.id}">

                        <div class="abono-inputs">
                            <select name="idCartera" required class="select-cuenta">
                                <option value="">Seleccionar cuenta</option>
                                <c:forEach var="cuenta" items="${cuentas}">
                                    <option value="${cuenta.id}">
                                            ${cuenta.nombre} - <fmt:formatNumber value="${cuenta.monto}" type="currency" currencySymbol="$" />
                                    </option>
                                </c:forEach>
                            </select>

                            <input
                                    type="number"
                                    name="monto"
                                    min="0.01"
                                    max="${deuda.calcularSaldoPendiente()}"
                                    step="0.01"
                                    placeholder="Monto a abonar"
                                    required
                                    class="input-monto"
                            />

                            <button class="btn btn-primary btn-abonar" type="submit">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <polyline points="20 6 9 17 4 12"></polyline>
                                </svg>
                                Abonar
                            </button>
                        </div>
                    </form>
                </div>
            </article>
        </c:forEach>
    </section>
</c:if>

<%-- 3. Incluimos el footer --%>
<jsp:include page="/comun/VistaFooter.jsp" />