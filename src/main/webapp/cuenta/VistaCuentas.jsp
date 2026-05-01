<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/comun/VistaHeader.jsp">
    <jsp:param name="pageTitle" value="Mis Cuentas"/>
</jsp:include>

<style>
    body {
        background:#121826;
        min-height: 100vh;
        color: #F1F5F9;
    }

    .page-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 2rem;
    }

    .page-header h2 {
        font-size: 2rem;
        font-weight: 700;
        margin: 0;
    }

    .btn {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem 1.5rem;
        border-radius: 8px;
        background: #4F46E5;
        color: white;
        text-decoration: none;
        font-weight: 600;
        transition: all 0.3s;
    }

    .btn:hover {
        background: #4338CA;
        transform: translateY(-2px);
    }

    .grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
        gap: 1.25rem;
    }

    .card {
        background: rgba(255, 255, 255, 0.05);
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 12px;
        padding: 1rem;
        transition: all 0.3s;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    }

    .card:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
    }

    .card-header {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        margin-bottom: 0.75rem;
    }

    .card-icon {
        width: 38px;
        height: 38px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
    }

    .icon-efectivo {
        background: rgba(245, 158, 11, 0.15);
        color: #F59E0B;
    }

    .icon-corriente {
        background: rgba(59, 130, 246, 0.15);
        color: #3B82F6;
    }

    .icon-ahorros {
        background: rgba(16, 185, 129, 0.15);
        color: #10B981;
    }

    .card-info h3 {
        font-size: 1.1rem;
        margin: 0 0 0.15rem 0;
        font-weight: 700;
    }

    .card-subtitle {
        font-size: 0.8rem;
        color: #94A3B8;
    }

    .monto {
        font-size: 1.5rem;
        font-weight: 700;
        color: #FFFFFF;
        margin: 0.6rem 0 0.75rem 0;
    }

    .detalle-link {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        width: 100%;
        padding: 0.6rem;
        background: rgba(79, 70, 229, 0.15);
        color: #818CF8;
        text-decoration: none;
        border-radius: 8px;
        font-weight: 600;
        font-size: 0.85rem;
        transition: all 0.3s;
        border: 1px solid rgba(79, 70, 229, 0.3);
    }

    .detalle-link:hover {
        background: rgba(79, 70, 229, 0.25);
        border-color: rgba(79, 70, 229, 0.5);
    }

    .empty-state {
        text-align: center;
        padding: 4rem 2rem;
        background: rgba(255, 255, 255, 0.03);
        border: 1px dashed rgba(255, 255, 255, 0.1);
        border-radius: 12px;
    }

    .empty-state h3 {
        font-size: 1.5rem;
        margin-bottom: 0.5rem;
    }

    .empty-state p {
        color: #94A3B8;
        margin-bottom: 2rem;
    }

    @media (max-width: 768px) {
        .grid {
            grid-template-columns: 1fr;
        }
    }
</style>

<div class="page-header">
    <h2>Gestión de Cuentas</h2>
    <a class="btn" href="${pageContext.request.contextPath}/cuentas/nuevo">
        + Nueva Cuenta
    </a>
</div>

<jsp:include page="/comun/Mensajes.jsp"/>

<c:if test="${empty cuentas}">
    <div class="empty-state">
        <h3>No tienes cuentas registradas</h3>
        <p>Crea tu primera cuenta para comenzar a gestionar tus finanzas</p>
        <a class="btn" href="${pageContext.request.contextPath}/cuentas/nuevo">
            Crear cuenta
        </a>
    </div>
</c:if>

<c:if test="${not empty cuentas}">
    <section class="grid">
        <c:forEach var="cuenta" items="${cuentas}">
            <article class="card">
                <div class="card-header">
                    <c:choose>
                        <c:when test="${cuenta.tipoCuenta == 'EFECTIVO'}">
                            <div class="card-icon icon-efectivo">
                                <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <line x1="12" y1="1" x2="12" y2="23"></line>
                                    <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
                                </svg>
                            </div>
                        </c:when>
                        <c:when test="${cuenta.tipoCuenta == 'CORRIENTE'}">
                            <div class="card-icon icon-corriente">
                                <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <rect x="2" y="5" width="20" height="14" rx="2"></rect>
                                    <line x1="2" y1="10" x2="22" y2="10"></line>
                                </svg>
                            </div>
                        </c:when>
                        <c:when test="${cuenta.tipoCuenta == 'AHORROS'}">
                            <div class="card-icon icon-ahorros">
                                <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M19 5c-1.5 0-2.8 1.4-3 2-3.5-1.5-11-.3-11 5 0 1.8 0 3 2 4.5V20h4v-2h3v2h4v-4c1-.5 1.7-1 2-2h2v-4h-2c0-1-.5-1.5-1-2h0V5z"></path>
                                </svg>
                            </div>
                        </c:when>
                    </c:choose>
                    <div class="card-info">
                        <h3><c:out value="${cuenta.nombre}"/></h3>
                        <div class="card-subtitle">${cuenta.tipoCuenta}</div>
                    </div>
                </div>

                <div class="monto">
                    <fmt:formatNumber value="${cuenta.monto}" type="currency" currencySymbol="$"/>
                </div>

                <a class="detalle-link" href="${pageContext.request.contextPath}/cuentas/detalle?id=${cuenta.id}">
                    Ver detalle
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="5" y1="12" x2="19" y2="12"></line>
                        <polyline points="12 5 19 12 12 19"></polyline>
                    </svg>
                </a>
            </article>
        </c:forEach>
    </section>
</c:if>

<jsp:include page="/comun/VistaFooter.jsp"/>