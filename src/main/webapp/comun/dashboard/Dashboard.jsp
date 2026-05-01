<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/comun/VistaHeader.jsp">
    <jsp:param name="pageTitle" value="Dashboard"/>
</jsp:include>

<div class="page-header">
    <h1>
        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="vertical-align: middle; margin-right: 8px;">
            <rect x="3" y="3" width="7" height="7"></rect>
            <rect x="14" y="3" width="7" height="7"></rect>
            <rect x="14" y="14" width="7" height="7"></rect>
            <rect x="3" y="14" width="7" height="7"></rect>
        </svg>
        Dashboard
    </h1>
</div>

<jsp:include page="/comun/Mensajes.jsp" />

<%-- Mostrar error si existe --%>
<c:if test="${not empty mensajeError}">
    <div class="alert alert-error">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="8" x2="12" y2="12"></line>
            <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
        <span>${mensajeError}</span>
    </div>
</c:if>

<%-- ESTADO: SIN CUENTAS --%>
<c:if test="${estatus == 'SIN_CUENTAS'}">
    <div class="empty-state-modern">
        <div class="empty-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect>
                <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
            </svg>
        </div>
        <h3>Aún no tienes cuentas registradas</h3>
        <p>Comienza creando tu primera cuenta para gestionar tus finanzas</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/cuentas/nuevo">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="12" y1="5" x2="12" y2="19"></line>
                <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            Crear cuenta
        </a>
    </div>
</c:if>

<%-- ESTADO: OK o SIN_MOVIMIENTOS --%>
<c:if test="${estatus == 'OK' || estatus == 'SIN_MOVIMIENTOS'}">

    <%-- Tarjetas principales: Saldo, Gastos, Ingresos --%>
    <div class="dashboard-cards">
        <div class="dash-card saldo-card">
            <div class="card-header">
                <span class="card-label">Saldo Cartera</span>
            </div>
            <div class="card-valor-principal">
                <fmt:formatNumber value="${dashboard.saldoTotal}" type="currency" currencySymbol="$" />
            </div>
        </div>

        <div class="dash-card gastos-card">
            <div class="card-header">
                <span class="card-label">Gastos Totales</span>
            </div>
            <div class="card-valor-principal negativo">
                -<fmt:formatNumber value="${dashboard.gastosTotal}" type="currency" currencySymbol="$" />
            </div>
        </div>

        <div class="dash-card ingresos-card">
            <div class="card-header">
                <span class="card-label">Ingresos Totales</span>
            </div>
            <div class="card-valor-principal positivo">
                <fmt:formatNumber value="${dashboard.ingresosTotal}" type="currency" currencySymbol="$" />
            </div>
        </div>
    </div>

    <%-- Sección de cuentas y transacciones --%>
    <div class="dashboard-grid">

            <%-- Saldos (últimos conocidos) --%>
        <div class="dashboard-panel">
            <div class="panel-header">
                <h2>Saldos en cuentas</h2>
            </div>

            <div class="saldos-lista">
                <c:forEach var="cuenta" items="${dashboard.cuentas}">
                    <div class="saldo-item saldo-clickable"
                         onclick="window.location.href='${pageContext.request.contextPath}/cuentas/detalle?id=${cuenta.id}'"
                         style="cursor: pointer;">
                        <div class="saldo-info">
                            <div class="saldo-icono">
                                <c:choose>
                                    <c:when test="${cuenta.tipoCuenta == 'EFECTIVO'}">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <line x1="12" y1="1" x2="12" y2="23"></line>
                                            <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
                                        </svg>
                                    </c:when>
                                    <c:when test="${cuenta.tipoCuenta == 'CORRIENTE'}">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <rect x="2" y="5" width="20" height="14" rx="2"></rect>
                                            <line x1="2" y1="10" x2="22" y2="10"></line>
                                        </svg>
                                    </c:when>
                                    <c:when test="${cuenta.tipoCuenta == 'AHORROS'}">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <path d="M19 5c-1.5 0-2.8 1.4-3 2-3.5-1.5-11-.3-11 5 0 1.8 0 3 2 4.5V20h4v-2h3v2h4v-4c1-.5 1.7-1 2-2h2v-4h-2c0-1-.5-1.5-1-2h0V5z"></path>
                                            <path d="M2 9v1c0 1.1.9 2 2 2h1"></path>
                                            <path d="M16 11h0"></path>
                                        </svg>
                                    </c:when>
                                    <c:otherwise>
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect>
                                            <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
                                        </svg>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="saldo-detalles">
                                <span class="saldo-nombre"><c:out value="${cuenta.nombre}"/></span>
                                <span class="saldo-tipo">${cuenta.tipoCuenta}</span>
                            </div>
                        </div>
                        <div class="saldo-valor">
                            <fmt:formatNumber value="${cuenta.monto}" type="currency" currencySymbol="$" />
                            <svg class="saldo-arrow" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <polyline points="9 18 15 12 9 6"></polyline>
                            </svg>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>

            <%-- Transacciones recientes --%>
        <div class="dashboard-panel">
            <div class="panel-header">
                <h2>Transacciones</h2>
            </div>

            <c:choose>
                <c:when test="${empty dashboard.ultimosMovimientos}">
                    <div class="empty-state-small">
                        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline>
                        </svg>
                        <p>No hay movimientos registrados todavía</p>
                        <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/movimientos">
                            Crear movimiento
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="transacciones-lista">
                        <c:forEach var="movimiento" items="${dashboard.ultimosMovimientos}">
                            <div class="transaccion-item">
                                <div class="trans-info">
                                    <div class="trans-descripcion">
                                        <c:out value="${movimiento.descripcion}"/>
                                    </div>
                                    <div class="trans-meta">
                                        <span class="trans-fecha">${movimiento.fechaFormateada}</span>
                                        <span class="trans-separador">•</span>
                                        <span class="trans-cuenta">${movimiento.cuenta.nombre}</span>
                                    </div>
                                </div>
                                <div class="trans-monto ${movimiento.tipo == 'INGRESO' ? 'positivo' : 'negativo'}">
                                    <c:choose>
                                        <c:when test="${movimiento.tipo == 'INGRESO'}">
                                            <fmt:formatNumber value="${movimiento.monto}" type="currency" currencySymbol="$" />
                                        </c:when>
                                        <c:otherwise>
                                            -<fmt:formatNumber value="${movimiento.monto}" type="currency" currencySymbol="$" />
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

</c:if>

<style>
    /* Tarjetas principales */
    .dashboard-cards {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
        gap: 1.5rem;
        margin-bottom: 2rem;
    }

    .dash-card {
        background: rgba(255, 255, 255, 0.05);
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 16px;
        padding: 1.5rem;
        position: relative;
        overflow: hidden;
    }

    .dash-card::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 3px;
    }

    .saldo-card::before {
        background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
    }

    .gastos-card::before {
        background: #f43f5e;
    }

    .ingresos-card::before {
        background: #10b981;
    }

    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
    }

    .card-label {
        font-size: 0.85rem;
        color: rgba(255, 255, 255, 0.6);
        text-transform: uppercase;
        letter-spacing: 0.5px;
        font-weight: 600;
    }

    .card-periodo {
        font-size: 0.75rem;
        color: rgba(255, 255, 255, 0.4);
    }

    .card-valor-principal {
        font-size: 2.25rem;
        font-weight: 700;
        color: #fff;
    }

    .card-valor-principal.negativo {
        color: #f43f5e;
    }

    .card-valor-principal.positivo {
        color: #10b981;
    }

    /* Grid de paneles */
    .dashboard-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
        gap: 1.5rem;
        margin-bottom: 2rem;
    }

    .dashboard-panel {
        background: rgba(255, 255, 255, 0.05);
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 16px;
        padding: 1.5rem;
    }

    .panel-header {
        margin-bottom: 1.5rem;
        padding-bottom: 1rem;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    }

    .panel-header h2 {
        font-size: 1.25rem;
        font-weight: 700;
        color: #fff;
        margin: 0 0 0.25rem 0;
    }

    .panel-periodo {
        font-size: 0.8rem;
        color: rgba(255, 255, 255, 0.4);
    }

    /* Lista de saldos */
    .saldos-lista {
        display: flex;
        flex-direction: column;
        gap: 0.75rem;
    }

    .saldo-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem;
        background: rgba(255, 255, 255, 0.03);
        border: 1px solid rgba(255, 255, 255, 0.08);
        border-radius: 10px;
        transition: all 0.2s ease;
    }

    .saldo-item:hover {
        background: rgba(255, 255, 255, 0.06);
        border-color: rgba(255, 255, 255, 0.15);
    }

    .saldo-info {
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }

    .saldo-icono {
        width: 32px;
        height: 32px;
        background: rgba(102, 126, 234, 0.15);
        color: #667eea;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
    }

    .saldo-nombre {
        font-size: 0.95rem;
        font-weight: 600;
        color: #fff;
    }

    .saldo-valor {
        font-size: 1.1rem;
        font-weight: 700;
        color: #fff;
    }

    /* Lista de transacciones */
    .transacciones-lista {
        display: flex;
        flex-direction: column;
        gap: 0.75rem;
    }

    .transaccion-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem;
        background: rgba(255, 255, 255, 0.03);
        border: 1px solid rgba(255, 255, 255, 0.08);
        border-radius: 10px;
        transition: all 0.2s ease;
    }

    .transaccion-item:hover {
        background: rgba(255, 255, 255, 0.06);
        border-color: rgba(255, 255, 255, 0.15);
    }

    .trans-info {
        flex: 1;
        min-width: 0;
    }

    .trans-descripcion {
        font-size: 0.95rem;
        font-weight: 600;
        color: #fff;
        margin-bottom: 0.25rem;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .trans-meta {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.8rem;
        color: rgba(255, 255, 255, 0.4);
    }

    .trans-separador {
        opacity: 0.5;
    }

    .trans-monto {
        font-size: 1.1rem;
        font-weight: 700;
        text-align: right;
        flex-shrink: 0;
        margin-left: 1rem;
    }

    .trans-monto.positivo {
        color: #10b981;
    }

    .trans-monto.negativo {
        color: #f43f5e;
    }

    /* Estado vacío pequeño */
    .empty-state-small {
        text-align: center;
        padding: 2rem 1rem;
        color: rgba(255, 255, 255, 0.5);
    }

    .empty-state-small svg {
        margin-bottom: 1rem;
        opacity: 0.3;
    }

    .empty-state-small p {
        margin-bottom: 1rem;
        font-size: 0.9rem;
    }

    .btn-sm {
        padding: 0.5rem 1rem;
        font-size: 0.85rem;
    }

    /* Estado vacío principal */
    .empty-state-modern {
        text-align: center;
        padding: 4rem 2rem;
        background: rgba(255, 255, 255, 0.03);
        border: 1px dashed rgba(255, 255, 255, 0.1);
        border-radius: 16px;
    }

    /* Alerta de error */
    .alert {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 1rem 1.25rem;
        border-radius: 12px;
        margin-bottom: 1.5rem;
        font-size: 0.95rem;
    }

    .alert-error {
        background: rgba(244, 63, 94, 0.1);
        border: 1px solid rgba(244, 63, 94, 0.3);
        color: #f43f5e;
    }

    .alert svg {
        flex-shrink: 0;
    }

    .empty-icon {
        width: 80px;
        height: 80px;
        margin: 0 auto 1.5rem;
        background: rgba(102, 126, 234, 0.1);
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #667eea;
    }

    .empty-state-modern h3 {
        font-size: 1.5rem;
        font-weight: 700;
        margin-bottom: 0.5rem;
        color: #fff;
    }

    .empty-state-modern p {
        color: rgba(255, 255, 255, 0.5);
        margin-bottom: 2rem;
    }

    /* Responsive */
    @media (max-width: 768px) {
        .dashboard-cards {
            grid-template-columns: 1fr;
        }

        .dashboard-grid {
            grid-template-columns: 1fr;
        }

        .card-valor-principal {
            font-size: 1.75rem;
        }

        .saldo-valor,
        .trans-monto {
            font-size: 1rem;
        }
    }
</style>

<jsp:include page="/comun/VistaFooter.jsp" />