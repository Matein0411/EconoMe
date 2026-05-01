<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<%-- 1. HEADER --%>
<jsp:include page="/comun/VistaHeader.jsp">
    <jsp:param name="pageTitle" value="Detalle de Lista"/>
</jsp:include>

<div class="container" style="padding-top: 2rem; padding-bottom: 2rem;">

    <%-- Botón Volver --%>
    <div style="margin-bottom: 1.5rem;">
        <a href="${pageContext.request.contextPath}/listas" class="btn-volver">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="19" y1="12" x2="5" y2="12"></line>
                <polyline points="12 19 5 12 12 5"></polyline>
            </svg>
            Volver a mis listas
        </a>
    </div>

    <%-- Encabezado Principal --%>
    <div class="header-flex">
        <div>
            <h1 style="margin: 0; font-size: 2rem;">
                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#3b82f6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-bottom: -5px; margin-right: 10px;">
                    <circle cx="9" cy="21" r="1"></circle><circle cx="20" cy="21" r="1"></circle>
                    <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                </svg>
                <c:out value="${lista.nombre}" default="Lista de Compras"/>
            </h1>
            <p style="color: #94a3b8; margin-top: 0.5rem;">
                ${not empty lista.articulosCompletados ? lista.articulosCompletados.size() : 0} de ${lista.articulos.size()} artículos comprados
            </p>
        </div>

        <%-- TOTAL PLANIFICADO (Arriba) --%>
        <div class="total-badge">
            <span class="total-label">Total Planificado</span>
            <span class="total-amount">
                <fmt:formatNumber value="${lista.precioTotal}" type="currency" currencySymbol="$"/>
            </span>
        </div>
    </div>

    <jsp:include page="/comun/Mensajes.jsp" />

    <%-- Formulario de Agregar Items --%>
    <div class="mini-form-container">
        <form action="${pageContext.request.contextPath}/listas/agregarItem" method="POST" class="inline-form">
            <input type="hidden" name="idLista" value="${lista.id}">

            <div class="input-wrapper">
                <svg class="input-icon" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"></path><line x1="7" y1="7" x2="7.01" y2="7"></line></svg>
                <input type="text" name="nombre" class="form-control-custom" placeholder="¿Qué vamos a comprar? (ej. Leche, Pan)" required autocomplete="off">
            </div>

            <div class="input-wrapper short">
                <svg class="input-icon" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"></line><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path></svg>
                <input type="number" name="precio" class="form-control-custom" placeholder="0.00" step="0.01" min="0" required>
            </div>

            <button type="submit" class="btn btn-primary" style="white-space: nowrap;">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
                Agregar
            </button>
        </form>
    </div>

    <%-- ==========================
         SECCIÓN: ARTÍCULOS PENDIENTES
       ========================== --%>
    <c:if test="${not empty lista.articulosPendientes}">
        <div class="section-header">
            <h3>Por Comprar</h3>
            <span class="badge" style="background-color: rgba(59, 130, 246, 0.15); color: #60a5fa;">${lista.articulosPendientes.size()} items</span>
        </div>
        <div class="items-grid">
            <c:forEach var="item" items="${lista.articulosPendientes}">
                <div class="item-row">
                    <div class="checkbox-wrapper">
                        <form action="${pageContext.request.contextPath}/listas/marcarComprado" method="POST" style="margin: 0;">
                            <input type="hidden" name="idItem" value="${item.id}">
                            <input type="hidden" name="idLista" value="${lista.id}">
                            <input type="hidden" name="comprado" value="true">
                            <input type="checkbox" class="checkbox-articulo" onchange="this.form.submit()">
                        </form>
                    </div>

                    <div class="item-name">
                        <c:out value="${item.nombre}"/>
                    </div>

                    <div class="item-price-tag">
                        <fmt:formatNumber value="${item.precioUnitario}" type="currency" currencySymbol="$"/>
                    </div>

                    <div class="item-actions">
                        <form action="${pageContext.request.contextPath}/listas/eliminarItem" method="POST"
                              onsubmit="return confirm('¿Eliminar este artículo?');">
                            <input type="hidden" name="idItem" value="${item.id}">
                            <input type="hidden" name="idLista" value="${lista.id}">
                            <button type="submit" class="btn btn-danger" style="padding: 0.5rem; border-radius: 8px;">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
                            </button>
                        </form>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:if>

    <%-- ==========================
         SECCIÓN: ARTÍCULOS COMPRADOS + PAGO
       ========================== --%>
    <c:if test="${not empty lista.articulosCompletados}">

        <%-- 1. Sumar total --%>
        <c:set var="totalComprado" value="0" />
        <c:forEach var="item" items="${lista.articulosCompletados}">
            <c:set var="totalComprado" value="${totalComprado + item.precioUnitario}" />
        </c:forEach>

        <%-- 2. CABECERA CON LÍNEA SUPERIOR + TOTAL (Estilo idéntico al de arriba) --%>
        <div class="comprados-header">
            <div class="header-left-group">
                <h3 style="margin: 0;">Comprados</h3>
                <span class="badge badge-success" style="background-color: rgba(16, 185, 129, 0.15); color: #10b981;">
                    ${lista.articulosCompletados.size()} items
                </span>
            </div>

            <%-- TOTAL COMPRADO (Reutilizando la clase total-badge para consistencia) --%>
            <div class="total-badge">
                <span class="total-label">Total Comprado</span>
                <span class="total-amount" style="color: #4F46E5;"> <%-- Color Indigo para diferenciar un poco --%>
                    <fmt:formatNumber value="${totalComprado}" type="currency" currencySymbol="$"/>
                </span>
            </div>
        </div>

        <%-- 3. BARRA DE PAGO (Selector + Botón Azul) --%>
        <form action="${pageContext.request.contextPath}/movimientos" method="POST" class="payment-toolbar">
            <input type="hidden" name="idLista" value="${lista.id}">
            <input type="hidden" name="tipo" value="GASTO">
            <input type="hidden" name="monto" value="${totalComprado}">
            <input type="hidden" name="descripcion" value="Compra Lista: ${lista.nombre}">
            <input type="hidden" name="categoria" value="OTROS">

            <%-- Se agregaron oninvalid y oninput para el mensaje personalizado --%>
            <select name="cuentaId"
                    required
                    class="form-control-custom select-cuenta-compact"
                    oninvalid="this.setCustomValidity('Primero debe seleccionar una cuenta')"
                    oninput="this.setCustomValidity('')">

                <option value="">Seleccionar cuenta de pago...</option>
                <c:forEach var="cuenta" items="${cuentas}">
                    <option value="${cuenta.id}">
                        ${cuenta.nombre} - <fmt:formatNumber value="${cuenta.monto}" type="currency" currencySymbol="$" />
                    </option>
                </c:forEach>
            </select>

            <button type="submit" class="btn btn-primary">

                Registrar Gasto
            </button>
        </form>
        <%-- 4. LISTA DE ITEMS --%>
        <div class="items-grid">
            <c:forEach var="item" items="${lista.articulosCompletados}">
                <div class="item-row item-completado">
                    <div class="checkbox-wrapper">
                        <form action="${pageContext.request.contextPath}/listas/marcarComprado" method="POST" style="margin: 0;">
                            <input type="hidden" name="idItem" value="${item.id}">
                            <input type="hidden" name="idLista" value="${lista.id}">
                            <input type="hidden" name="comprado" value="false">
                            <input type="checkbox" class="checkbox-articulo" checked onchange="this.form.submit()">
                        </form>
                    </div>

                    <div class="item-name item-name-tachado">
                        <c:out value="${item.nombre}"/>
                    </div>

                    <div class="item-price-tag">
                        <fmt:formatNumber value="${item.precioUnitario}" type="currency" currencySymbol="$"/>
                    </div>

                    <div class="item-actions">
                        <form action="${pageContext.request.contextPath}/listas/eliminarItem" method="POST"
                              onsubmit="return confirm('¿Eliminar este artículo?');">
                            <input type="hidden" name="idItem" value="${item.id}">
                            <input type="hidden" name="idLista" value="${lista.id}">
                            <button type="submit" class="btn btn-danger" style="padding: 0.5rem; border-radius: 8px;">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
                            </button>
                        </form>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:if>

    <%-- ESTADO VACÍO --%>
    <c:if test="${empty lista.articulos}">
        <div class="empty-state">
            <div class="empty-state-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round"><circle cx="9" cy="21" r="1"></circle><circle cx="20" cy="21" r="1"></circle><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path></svg>
            </div>
            <h2>Lista vacía</h2>
            <p>Usa el formulario de arriba para agregar tus primeros productos.</p>
        </div>
    </c:if>

</div>

<jsp:include page="/comun/VistaFooter.jsp" />

<style>
    /* === ESTILOS GENERALES === */
    .btn-volver {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.65rem 1.25rem;
        background-color: rgba(255, 255, 255, 0.05);
        border: 1px solid rgba(255, 255, 255, 0.1);
        color: #94a3b8;
        text-decoration: none;
        border-radius: 8px;
        font-size: 0.95rem;
        font-weight: 500;
        transition: all 0.3s;
    }
    .btn-volver:hover {
        background-color: rgba(255, 255, 255, 0.1);
        border-color: rgba(255, 255, 255, 0.2);
        color: #e2e8f0;
        transform: translateX(-3px);
    }

    .header-flex {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 2rem;
        flex-wrap: wrap;
        gap: 1rem;
    }

    /* ESTILO COMPARTIDO PARA AMBOS TOTALES */
    .total-badge {
        background-color: var(--bg-med, #1a2233);
        border: 1px solid var(--border-color, #374151);
        padding: 0.75rem 1.5rem;
        border-radius: 12px;
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        min-width: 180px; /* Asegura un ancho mínimo consistente */
    }
    .total-label { font-size: 0.85rem; color: #94a3b8; text-transform: uppercase; letter-spacing: 0.5px; }
    .total-amount { font-size: 1.5rem; font-weight: 700; color: #3b82f6; }

    /* Formulario de Agregar */
    .mini-form-container {
        background-color: var(--bg-med, #1a2233);
        padding: 1.25rem;
        border-radius: 12px;
        border: 1px solid var(--border-color, #374151);
        margin-bottom: 2rem;
        box-shadow: 0 4px 6px rgba(0,0,0,0.2);
    }
    .inline-form { display: flex; gap: 1rem; align-items: center; flex-wrap: wrap; }
    .input-wrapper { position: relative; flex-grow: 1; }
    .input-wrapper.short { flex-grow: 0; flex-basis: 140px; }
    .input-icon { position: absolute; left: 12px; top: 50%; transform: translateY(-50%); color: #64748b; pointer-events: none; }

    .form-control-custom {
        width: 100%;
        background-color: var(--bg-dark, #121826);
        border: 1px solid var(--border-color, #374151);
        color: #e2e8f0;
        padding: 0.75rem 1rem 0.75rem 2.5rem;
        border-radius: 8px;
        transition: all 0.2s;
        height: 48px; /* Altura fija para alinear con botones */
    }
    .form-control-custom:focus { outline: none; border-color: #3b82f6; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2); }

    /* Listados */
    .section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .section-header h3 { font-size: 1.25rem; font-weight: 600; margin: 0; }

    .badge { background-color: rgba(59, 130, 246, 0.15); color: #60a5fa; padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.85rem; font-weight: 600; }

    .items-grid { display: flex; flex-direction: column; gap: 0.75rem; }

    .item-row {
        display: grid;
        grid-template-columns: auto 1fr auto auto;
        align-items: center;
        gap: 1.5rem;
        background-color: var(--bg-med, #1a2233);
        padding: 1rem 1.5rem;
        border-radius: 10px;
        border: 1px solid transparent;
        transition: transform 0.2s, border-color 0.2s, opacity 0.2s;
    }
    .item-row:hover { border-color: #3b82f6; transform: translateX(5px); }
    .item-completado { opacity: 0.6; }
    .item-completado:hover { opacity: 1; }

    .item-name { font-weight: 500; font-size: 1.1rem; }
    .item-name-tachado { text-decoration: line-through; color: #94a3b8; }
    .item-price-tag { background-color: rgba(59, 130, 246, 0.1); color: #60a5fa; padding: 0.25rem 0.75rem; border-radius: 20px; font-weight: 600; font-size: 0.9rem; }
    .checkbox-wrapper input[type="checkbox"] { width: 20px; height: 20px; cursor: pointer; accent-color: #3b82f6; }

    .empty-state { text-align: center; padding: 4rem 2rem; background: rgba(255, 255, 255, 0.03); border: 1px dashed rgba(255, 255, 255, 0.1); border-radius: 12px; }
    .empty-state-icon { margin-bottom: 1rem; opacity: 0.5; }
    .empty-state h2 { font-size: 1.5rem; margin-bottom: 0.5rem; }
    .empty-state p { color: #94a3b8; }

    /* === ESTILOS PARA LA SECCIÓN DE PAGO (Diseño 100% igualado) === */
    .comprados-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 3rem;
        padding-top: 1.5rem;
        border-top: 1px solid rgba(255, 255, 255, 0.1); /* Línea separadora */
        margin-bottom: 1rem;
        flex-wrap: wrap;
        gap: 1rem;
    }

    .header-left-group { display: flex; align-items: center; gap: 1rem; }

    .payment-toolbar {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 1.5rem;
        width: fit-content;
    }

    .select-cuenta-compact {
        width: 300px; /* Tamaño medio para el selector */
        padding-left: 1rem; /* Ajuste para el texto */
    }

    /* Asegura que el botón "Generar Gasto" sea idéntico a "Agregar" */
    .btn-primary {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        padding: 0.75rem 1.5rem;
        background-color: #3b82f6;
        color: white;
        border-radius: 8px;
        border: none;
        font-weight: 600;
        cursor: pointer;
        transition: background-color 0.2s;
        height: 48px; /* Altura fija igual al input */
        font-size: 1rem;
    }
    .btn-primary:hover { background-color: #2563eb; }

    @media (max-width: 600px) {
        .comprados-header { flex-direction: column; align-items: flex-start; }
        .payment-toolbar { width: 100%; flex-direction: column; align-items: stretch; }
        .select-cuenta-compact { width: 100%; }
        .total-badge { width: 100%; align-items: center; }
    }
</style>