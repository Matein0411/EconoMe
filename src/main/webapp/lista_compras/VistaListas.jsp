<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/comun/VistaHeader.jsp">
    <jsp:param name="pageTitle" value="Mis Listas de Compras"/>
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
        background: rgba(139, 92, 246, 0.15);
        color: #8B5CF6;
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

    .card-stats {
        display: flex;
        gap: 1rem;
        margin: 0.75rem 0;
    }

    .stat {
        flex: 1;
    }

    .stat-label {
        font-size: 0.75rem;
        color: #94A3B8;
        margin-bottom: 0.25rem;
    }

    .stat-value {
        font-size: 1.1rem;
        font-weight: 700;
        color: #FFFFFF;
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
    <h2>Gestión de Listas de Compras</h2>
    <a class="btn" href="${pageContext.request.contextPath}/listas/nuevo">
        + Nueva Lista
    </a>
</div>

<jsp:include page="/comun/Mensajes.jsp"/>

<c:if test="${empty listas}">
    <div class="empty-state">
        <h3>No tienes listas de compras</h3>
        <p>Crea tu primera lista para organizar tus compras</p>
        <a class="btn" href="${pageContext.request.contextPath}/listas/nuevo">
            Crear lista
        </a>
    </div>
</c:if>

<c:if test="${not empty listas}">
    <section class="grid">
        <c:forEach var="lista" items="${listas}">
            <article class="card">
                <div class="card-header">
                    <div class="card-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="8" y1="6" x2="21" y2="6"></line>
                            <line x1="8" y1="12" x2="21" y2="12"></line>
                            <line x1="8" y1="18" x2="21" y2="18"></line>
                            <line x1="3" y1="6" x2="3.01" y2="6"></line>
                            <line x1="3" y1="12" x2="3.01" y2="12"></line>
                            <line x1="3" y1="18" x2="3.01" y2="18"></line>
                        </svg>
                    </div>
                    <div class="card-info">
                        <h3><c:out value="${lista.nombre}"/></h3>
                        <div class="card-subtitle">
                                ${lista.fechaFormateada}
                        </div>
                    </div>
                </div>

                <div class="card-stats">
                    <div class="stat">
                        <div class="stat-label">Progreso</div>
                        <div class="stat-value">${lista.articulosComprados}/${lista.totalArticulos}</div>
                    </div>
                    <div class="stat">
                        <div class="stat-label">Total</div>
                        <div class="stat-value">
                            <fmt:formatNumber value="${lista.precioTotal}" type="currency" currencySymbol="$"/>
                        </div>
                    </div>
                </div>

                <a class="detalle-link" href="${pageContext.request.contextPath}/listas/detalle?id=${lista.id}">
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