<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ page import="com.EconoMe.movimientos.modelos.CategoriaIngreso" %>
<%@ page import="com.EconoMe.movimientos.modelos.CategoriaGasto" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/comun/VistaHeader.jsp">
    <jsp:param name="pageTitle" value="Detalle de Cuenta"/>
</jsp:include>

<%-- LÓGICA: Detectar si hay filtros activos en la URL --%>
<c:set var="hayFiltros" value="${not empty param.tipo or not empty param.fechaDesde or not empty param.fechaHasta or not empty param.categoria}" />

<div class="page-header">
    <h1>
        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="vertical-align: middle; margin-right: 8px;">
            <c:choose>
                <c:when test="${cuenta.tipoCuenta == 'EFECTIVO'}">
                    <line x1="12" y1="1" x2="12" y2="23"></line>
                    <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
                </c:when>
                <c:when test="${cuenta.tipoCuenta == 'CORRIENTE'}">
                    <rect x="2" y="5" width="20" height="14" rx="2"></rect>
                    <line x1="2" y1="10" x2="22" y2="10"></line>
                </c:when>
                <c:when test="${cuenta.tipoCuenta == 'AHORROS'}">
                    <path d="M19 5c-1.5 0-2.8 1.4-3 2-3.5-1.5-11-.3-11 5 0 1.8 0 3 2 4.5V20h4v-2h3v2h4v-4c1-.5 1.7-1 2-2h2v-4h-2c0-1-.5-1.5-1-2h0V5z"></path>
                    <path d="M2 9v1c0 1.1.9 2 2 2h1"></path>
                    <path d="M16 11h0"></path>
                </c:when>
                <c:otherwise>
                    <rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect>
                    <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
                </c:otherwise>
            </c:choose>
        </svg>
        <c:out value="${cuenta.nombre}"/>
    </h1>
    <a href="${pageContext.request.contextPath}/cuentas" class="btn btn-secondary">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="19" y1="12" x2="5" y2="12"></line>
            <polyline points="12 19 5 12 12 5"></polyline>
        </svg>
        <span>Volver</span>
    </a>
</div>

<jsp:include page="/comun/Mensajes.jsp" />

<%-- 1. RESUMEN COMPACTO --%>
<div class="resumen-compacto">
    <div class="resumen-item saldo-principal">
        <span class="resumen-label">Saldo Actual</span>
        <span class="resumen-valor"><fmt:formatNumber value="${cuenta.monto}" type="currency" currencySymbol="$" /></span>
    </div>
    <div class="resumen-divisor"></div>
    <div class="resumen-item">
        <span class="resumen-label">Tipo</span>
        <span class="resumen-valor tipo-badge">${cuenta.tipoCuenta}</span>
    </div>
    <div class="resumen-divisor"></div>
    <div class="resumen-item ingreso">
        <span class="resumen-label">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="12" y1="19" x2="12" y2="5"></line>
                <polyline points="5 12 12 5 19 12"></polyline>
            </svg>
            Ingresos
        </span>
        <span class="resumen-valor"><fmt:formatNumber value="${totalIngresos}" type="currency" currencySymbol="$" /></span>
    </div>
    <div class="resumen-divisor"></div>
    <div class="resumen-item gasto">
        <span class="resumen-label">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="12" y1="5" x2="12" y2="19"></line>
                <polyline points="19 12 12 19 5 12"></polyline>
            </svg>
            Gastos
        </span>
        <span class="resumen-valor"><fmt:formatNumber value="${totalGastos}" type="currency" currencySymbol="$" /></span>
    </div>
    <div class="resumen-divisor"></div>
    <div class="resumen-item">
        <span class="resumen-label">Movimientos</span>
        <span class="resumen-valor">${cantidadMovimientos}</span>
    </div>
</div>

<%-- 2. SECCIÓN DE FILTROS --%>
<div class="filters-section">
    <form method="get" action="${pageContext.request.contextPath}/cuentas/detalle" class="filters-form" id="formFiltros">
        <input type="hidden" name="id" value="${cuenta.id}">
        <div class="filters-grid">
            <div class="filter-group">
                <label for="filtroTipo">Tipo</label>
                <select name="tipo" id="filtroTipo" class="form-control-sm">
                    <option value="">Todos</option>
                    <option value="INGRESO" ${param.tipo == 'INGRESO' ? 'selected' : ''}>Ingresos</option>
                    <option value="GASTO" ${param.tipo == 'GASTO' ? 'selected' : ''}>Gastos</option>
                </select>
            </div>
            <div class="filter-group">
                <label for="fechaDesde">Desde</label>
                <input type="date" name="fechaDesde" id="fechaDesde" class="form-control-sm" value="${param.fechaDesde}">
            </div>
            <div class="filter-group">
                <label for="fechaHasta">Hasta</label>
                <input type="date" name="fechaHasta" id="fechaHasta" class="form-control-sm" value="${param.fechaHasta}">
            </div>
            <div class="filter-group">
                <label for="filtroCategoria">Categoría</label>
                <select name="categoria" id="filtroCategoria" class="form-control-sm" disabled>
                    <option value="">Todas</option>
                    <optgroup label="Ingresos" id="catIngresos">
                        <c:forEach var="cat" items="<%= CategoriaIngreso.values() %>">
                            <option value="${cat.name()}" data-tipo="INGRESO" ${param.categoria == cat.name() ? 'selected' : ''}>${cat.name()}</option>
                        </c:forEach>
                    </optgroup>
                    <optgroup label="Gastos" id="catGastos">
                        <c:forEach var="cat" items="<%= CategoriaGasto.values() %>">
                            <option value="${cat.name()}" data-tipo="GASTO" ${param.categoria == cat.name() ? 'selected' : ''}>${cat.name()}</option>
                        </c:forEach>
                    </optgroup>
                </select>
            </div>
        </div>
        <div class="filters-actions">
            <button type="submit" class="btn-filter-apply">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"></polygon></svg>
                Filtrar
            </button>
            <a href="${pageContext.request.contextPath}/cuentas/detalle?id=${cuenta.id}" class="btn-filter-clear">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                Limpiar
            </a>
        </div>
    </form>
</div>

<%-- SCRIPT PARA FILTROS --%>
<script>
    const filtroTipo = document.getElementById('filtroTipo');
    const filtroCategoria = document.getElementById('filtroCategoria');
    const catIngresos = document.getElementById('catIngresos');
    const catGastos = document.getElementById('catGastos');

    function actualizarCategorias() {
        const tipo = filtroTipo.value;
        if (!tipo) {
            filtroCategoria.disabled = true;
            filtroCategoria.value = '';
            catIngresos.style.display = '';
            catGastos.style.display = '';
        } else {
            filtroCategoria.disabled = false;
            if (tipo === 'INGRESO') {
                catIngresos.style.display = '';
                catGastos.style.display = 'none';
            } else if (tipo === 'GASTO') {
                catIngresos.style.display = 'none';
                catGastos.style.display = '';
            }
            // Resetear categoría si no coincide con el tipo
            const selectedOption = filtroCategoria.options[filtroCategoria.selectedIndex];
            if (selectedOption && selectedOption.getAttribute('data-tipo') && selectedOption.getAttribute('data-tipo') !== tipo) {
                filtroCategoria.value = '';
            }
        }
    }

    filtroTipo.addEventListener('change', actualizarCategorias);
    document.addEventListener('DOMContentLoaded', actualizarCategorias);

    // Validación de fechas
    const formFiltros = document.getElementById('formFiltros');
    formFiltros.addEventListener('submit', function(e) {
        const fechaDesde = document.getElementById('fechaDesde').value;
        const fechaHasta = document.getElementById('fechaHasta').value;
        if (fechaDesde && fechaHasta && fechaDesde > fechaHasta) {
            alert('La fecha "Desde" no puede ser mayor que la fecha "Hasta".');
            e.preventDefault();
        }
    });
</script>

<%-- 3. ENCABEZADO DE LA LISTA --%>
<div class="seccion-header">
    <h2>Historial de Movimientos</h2>
    <a href="${pageContext.request.contextPath}/movimientos" class="btn btn-primary">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        <span>Registrar Movimiento</span>
    </a>
</div>

<%-- 4. LOGICA DE ESTADOS VACÍOS --%>

<%-- CASO A: SI HAY FILTROS ACTIVOS PERO NO HAY RESULTADOS --%>
<c:if test="${empty movimientos && hayFiltros}">
    <div class="empty-state-modern">
        <div class="empty-icon" style="background: rgba(244, 63, 94, 0.1); color: #f43f5e;">
            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                <line x1="11" y1="8" x2="11" y2="8"></line>
            </svg>
        </div>
        <h3>No se encontraron coincidencias</h3>
        <p>No hay movimientos con los criterios seleccionados.</p>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/cuentas/detalle?id=${cuenta.id}">
            Borrar Filtros
        </a>
    </div>
</c:if>

<%-- CASO B: SI NO HAY FILTROS Y LA CUENTA ESTÁ TOTALMENTE VACÍA --%>
<c:if test="${empty movimientos && !hayFiltros}">
    <div class="empty-state-modern">
        <div class="empty-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline>
            </svg>
        </div>
        <h3>Sin movimientos</h3>
        <p>No hay movimientos registrados en esta cuenta</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/movimientos">
            Registrar primer movimiento
        </a>
    </div>
</c:if>

<%-- 5. TABLA + PAGINACIÓN --%>
<c:if test="${not empty movimientos}">

    <div class="paginacion-info">
        <span class="paginacion-texto">
            Mostrando ${mostrandoDesde} - ${mostrandoHasta}
        </span>
        <span class="paginacion-pagina">Página ${paginaActual} de ${totalPaginas}</span>
    </div>

    <div class="table-responsive">
        <table class="movimientos-table">
            <thead>
            <tr>
                <th>Fecha/Hora</th>
                <th>Tipo</th>
                <th>Categoría</th>
                <th>Descripción</th>
                <th class="text-right">Monto</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="movimiento" items="${movimientos}">
                <c:set var="esIngreso" value="${movimiento['class'].simpleName == 'Ingreso'}" />
                <tr class="mov-row ${esIngreso ? 'fila-ingreso' : 'fila-gasto'}">
                    <td class="celda-fecha">
                        <span class="fecha-texto">${movimiento.fechaFormateada}</span>
                    </td>
                    <td>
                        <div class="tipo-badge-table ${esIngreso ? 'badge-ingreso' : 'badge-gasto'}">
                            <c:choose>
                                <c:when test="${esIngreso}">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="19" x2="12" y2="5"></line><polyline points="5 12 12 5 19 12"></polyline></svg>
                                    Ingreso
                                </c:when>
                                <c:otherwise>
                                    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><polyline points="19 12 12 19 5 12"></polyline></svg>
                                    Gasto
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </td>
                    <td>
                        <span class="categoria-texto">${esIngreso ? movimiento.categoriaIngreso : movimiento.categoriaGasto}</span>
                    </td>
                    <td class="celda-descripcion" title="${movimiento.descripcion}">
                        <c:out value="${movimiento.descripcion}"/>
                    </td>
                    <td class="text-right">
                        <span class="monto-valor ${esIngreso ? 'positivo' : 'negativo'}">
                            <c:if test="${esIngreso}">+</c:if><c:if test="${!esIngreso}">-</c:if><fmt:formatNumber value="${movimiento.monto}" type="currency" currencySymbol="$" />
                        </span>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <%-- BOTONES DE PAGINACIÓN --%>
    <c:if test="${totalPaginas > 1}">
        <div class="paginacion-controles">
            <c:choose>
                <c:when test="${esPrimeraPagina}">
                    <button class="btn-paginacion" disabled>
                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"></polyline></svg>
                        Anterior
                    </button>
                </c:when>
                <c:otherwise>
                    <%-- Nota: Mantenemos los filtros en los enlaces de paginación --%>
                    <a href="${pageContext.request.contextPath}/cuentas/detalle?id=${cuenta.id}&pagina=${paginaActual - 1}&tipo=${param.tipo}&fechaDesde=${param.fechaDesde}&fechaHasta=${param.fechaHasta}&categoria=${param.categoria}" class="btn-paginacion">
                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"></polyline></svg>
                        Anterior
                    </a>
                </c:otherwise>
            </c:choose>

            <div class="numeros-pagina">
                <c:forEach begin="1" end="${totalPaginas}" var="numPagina">
                    <c:choose>
                        <c:when test="${numPagina == paginaActual}">
                            <span class="numero-pagina activo">${numPagina}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/cuentas/detalle?id=${cuenta.id}&pagina=${numPagina}&tipo=${param.tipo}&fechaDesde=${param.fechaDesde}&fechaHasta=${param.fechaHasta}&categoria=${param.categoria}" class="numero-pagina">
                                    ${numPagina}
                            </a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>

            <c:choose>
                <c:when test="${esUltimaPagina}">
                    <button class="btn-paginacion" disabled>
                        Siguiente
                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"></polyline></svg>
                    </button>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/cuentas/detalle?id=${cuenta.id}&pagina=${paginaActual + 1}&tipo=${param.tipo}&fechaDesde=${param.fechaDesde}&fechaHasta=${param.fechaHasta}&categoria=${param.categoria}" class="btn-paginacion">
                        Siguiente
                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"></polyline></svg>
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>

</c:if>

<style>
    /* 1. RESUMEN COMPACTO (Sin Scroll) */
    .resumen-compacto {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
        background: rgba(255, 255, 255, 0.05);
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 16px;
        padding: 1.5rem;
        margin-bottom: 2rem;
        gap: 1.5rem;
    }

    .resumen-item {
        display: flex;
        flex-direction: column;
        gap: 0.4rem;
        min-width: fit-content;
    }
    .resumen-item.saldo-principal { flex: 1 1 200px; }
    .resumen-label {
        font-size: 0.75rem;
        color: rgba(255, 255, 255, 0.5);
        text-transform: uppercase;
        letter-spacing: 0.5px;
        font-weight: 500;
        display: flex;
        align-items: center;
        gap: 0.3rem;
    }
    .resumen-valor { font-size: 1.25rem; font-weight: 700; color: #fff; }
    .resumen-item.saldo-principal .resumen-valor {
        font-size: 1.75rem;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
    }
    .resumen-item.ingreso .resumen-valor { color: #10b981; }
    .resumen-item.gasto .resumen-valor { color: #f43f5e; }
    .tipo-badge {
        background: rgba(102, 126, 234, 0.15);
        color: #667eea;
        padding: 0.35rem 0.75rem;
        border-radius: 8px;
        font-size: 0.85rem;
        font-weight: 600;
        width: fit-content;
    }
    .resumen-divisor {
        width: 1px;
        height: 40px;
        background: rgba(255, 255, 255, 0.1);
    }

    /* 2. NUEVOS ESTILOS DE FILTROS */
    .filters-section {
        background: rgba(255, 255, 255, 0.02);
        border: 1px solid rgba(255, 255, 255, 0.08);
        border-radius: 12px;
        padding: 1.25rem;
        margin-bottom: 2rem;
    }
    .filters-form {
        display: flex;
        align-items: flex-end;
        gap: 1.5rem;
        flex-wrap: wrap;
    }
    .filters-grid {
        display: flex;
        flex-wrap: wrap;
        gap: 1rem;
        flex: 1;
    }
    .filter-group {
        display: flex;
        flex-direction: column;
        gap: 0.4rem;
    }
    .filter-group label {
        font-size: 0.8rem;
        color: rgba(255, 255, 255, 0.6);
        font-weight: 500;
        margin-left: 2px;
    }
    .form-control-sm {
        background: rgba(0, 0, 0, 0.2);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 8px;
        padding: 8px 12px;
        color: #fff;
        font-size: 0.9rem;
        min-width: 140px;
        outline: none;
        transition: border-color 0.2s;
    }
    .form-control-sm[type="date"] { color-scheme: dark; }
    .form-control-sm:focus { border-color: #3b82f6; background: rgba(0, 0, 0, 0.4); }
    .filters-actions { display: flex; gap: 0.8rem; padding-bottom: 1px; }
    .btn-filter-apply {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        background: #3b82f6;
        color: white;
        border: none;
        padding: 9px 16px;
        border-radius: 8px;
        font-weight: 600;
        font-size: 0.9rem;
        cursor: pointer;
        transition: background 0.2s;
    }
    .btn-filter-apply:hover { background: #2563eb; }
    .btn-filter-clear {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        background: rgba(255, 255, 255, 0.05);
        color: rgba(255, 255, 255, 0.7);
        text-decoration: none;
        padding: 9px 16px;
        border-radius: 8px;
        font-weight: 500;
        font-size: 0.9rem;
        border: 1px solid rgba(255, 255, 255, 0.1);
        transition: all 0.2s;
    }
    .btn-filter-clear:hover { background: rgba(255, 255, 255, 0.1); color: white; }

    /* 3. ESTILOS DE TABLA Y FILAS */
    .seccion-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1.5rem;
        flex-wrap: wrap;
        gap: 1rem;
    }
    .seccion-header h2 { font-size: 1.5rem; font-weight: 700; margin: 0; color: #fff; }
    .table-responsive {
        width: 100%;
        overflow-x: auto;
        background: rgba(30, 30, 40, 0.6);
        border-radius: 16px;
        border: 1px solid rgba(255, 255, 255, 0.08);
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
    }
    .table-responsive::-webkit-scrollbar { height: 6px; }
    .table-responsive::-webkit-scrollbar-thumb { background: rgba(255, 255, 255, 0.1); border-radius: 3px; }
    .movimientos-table {
        width: 100%;
        border-collapse: separate;
        border-spacing: 0 4px;
        color: #e0e0e0;
        font-size: 0.95rem;
        min-width: 800px;
    }
    .movimientos-table th {
        text-align: left;
        padding: 1.2rem 1rem;
        color: rgba(255, 255, 255, 0.5);
        font-weight: 700;
        text-transform: uppercase;
        font-size: 0.7rem;
        letter-spacing: 1px;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    }
    .mov-row td {
        padding: 1rem;
        vertical-align: middle;
        background: rgba(255, 255, 255, 0.02);
        border-top: 1px solid rgba(255, 255, 255, 0.03);
        border-bottom: 1px solid rgba(255, 255, 255, 0.03);
        transition: all 0.2s ease;
    }
    .mov-row.fila-ingreso { border-left: 3px solid #10b981; }
    .mov-row.fila-gasto { border-left: 3px solid #f43f5e; }
    .mov-row:hover td { background: rgba(255, 255, 255, 0.05); }

    .tipo-badge-table {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        padding: 6px 12px;
        border-radius: 50px;
        font-size: 0.75rem;
        font-weight: 700;
        text-transform: uppercase;
    }
    .badge-ingreso { background: rgba(16, 185, 129, 0.2); color: #34d399; border: 1px solid rgba(16, 185, 129, 0.3); }
    .badge-gasto { background: rgba(244, 63, 94, 0.2); color: #fb7185; border: 1px solid rgba(244, 63, 94, 0.3); }
    .categoria-texto {
        background: rgba(255, 255, 255, 0.05);
        padding: 4px 10px;
        border-radius: 6px;
        font-size: 0.85rem;
        color: #fff;
    }
    .text-right { text-align: right; }
    .monto-valor { font-weight: 700; font-family: 'Consolas', monospace; font-size: 1.1rem; }
    .monto-valor.positivo { color: #34d399; text-shadow: 0 0 10px rgba(52, 211, 153, 0.3); }
    .monto-valor.negativo { color: #fb7185; text-shadow: 0 0 10px rgba(251, 113, 133, 0.3); }

    /* ESTILO PUNTOS SUSPENSIVOS (...) */
    .celda-descripcion {
        max-width: 35ch;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        color: rgba(255, 255, 255, 0.8);
        font-style: italic;
        cursor: help;
    }

    /* 4. PAGINACIÓN */
    .paginacion-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem 0;
        margin-bottom: 1rem;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        font-size: 0.9rem;
        color: rgba(255, 255, 255, 0.6);
        flex-wrap: wrap;
    }
    .paginacion-controles {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 1rem;
        margin-top: 2rem;
        padding: 1.5rem 0;
        flex-wrap: wrap;
    }
    .btn-paginacion {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem 1.25rem;
        background: rgba(255, 255, 255, 0.05);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 8px;
        color: #fff;
        font-size: 0.9rem;
        font-weight: 500;
        text-decoration: none;
        transition: all 0.2s ease;
        cursor: pointer;
    }
    .btn-paginacion:hover:not([disabled]) {
        background: rgba(255, 255, 255, 0.1);
        border-color: rgba(255, 255, 255, 0.2);
        transform: translateY(-2px);
    }
    .btn-paginacion[disabled] { opacity: 0.4; cursor: not-allowed; pointer-events: none; }
    .numeros-pagina { display: flex; gap: 0.5rem; }
    .numero-pagina {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        min-width: 40px;
        height: 40px;
        padding: 0 0.75rem;
        background: rgba(255, 255, 255, 0.05);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 8px;
        color: #fff;
        font-size: 0.9rem;
        font-weight: 500;
        text-decoration: none;
        transition: all 0.2s ease;
    }
    .numero-pagina:hover { background: rgba(255, 255, 255, 0.1); border-color: rgba(255, 255, 255, 0.2); }
    .numero-pagina.activo { background: #3b82f6; border-color: transparent; font-weight: 700; }

    /* Estado Vacío */
    .empty-state-modern {
        text-align: center;
        padding: 4rem 2rem;
        background: rgba(255, 255, 255, 0.03);
        border: 1px dashed rgba(255, 255, 255, 0.1);
        border-radius: 16px;
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
    .empty-state-modern h3 { font-size: 1.5rem; font-weight: 700; margin-bottom: 0.5rem; color: #fff; }
    .empty-state-modern p { color: rgba(255, 255, 255, 0.5); margin-bottom: 2rem; }

    /* Responsive */
    @media (max-width: 768px) {
        .resumen-compacto { flex-wrap: wrap; }
        .resumen-divisor { display: none; }
        .filters-form { flex-direction: column; align-items: stretch; }
        .filters-grid { flex-direction: column; }
        .form-control-sm { width: 100%; }
        .filters-actions { width: 100%; margin-top: 1rem; }
        .btn-filter-apply, .btn-filter-clear { flex: 1; justify-content: center; }
        .paginacion-controles { flex-wrap: wrap; }
        .numeros-pagina { order: 3; width: 100%; justify-content: center; margin-top: 1rem; }
        .celda-descripcion { max-width: 15ch; }
    }

    /* --- CAMBIOS SOLICITADOS (COMBOBOX ESTILIZADO) --- */
    /* Color de fondo y texto para las opciones normales */
    .form-control-sm option {
        background-color: #1e1e28;
        color: #ffffff;
        padding: 8px;
    }

    /* Color para los TÍTULOS de las categorías (Ingresos, Gastos) */
    .form-control-sm optgroup {
        background-color: #1e1e28;
        color: #3b82f6;
        font-weight: bold;
        font-style: normal;
        padding: 8px;
    }
</style>
<jsp:include page="/comun/VistaFooter.jsp" />