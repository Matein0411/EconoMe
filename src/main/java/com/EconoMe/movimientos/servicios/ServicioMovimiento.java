package com.EconoMe.movimientos.servicios;

import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.cuentas.servicios.ServicioCuenta;
import com.EconoMe.movimientos.dao.DAOMovimiento;
import com.EconoMe.movimientos.modelos.*;

import java.time.Instant;
import java.util.List;

public class ServicioMovimiento {
    private final DAOMovimiento daoMovimiento;
    private final ServicioCuenta servicioCuenta;

    public ServicioMovimiento(DAOMovimiento daoMovimiento, ServicioCuenta servicioCuenta) {
        this.daoMovimiento = daoMovimiento;
        this.servicioCuenta = servicioCuenta;
    }

    public ServicioMovimiento() {
        this.daoMovimiento = new DAOMovimiento();
        this.servicioCuenta = new ServicioCuenta();
    }

    public void registrarIngreso(Long cuentaId, double monto, String descripcion, CategoriaIngreso categoria) {
        validarMonto(monto);
        validarCategoria(categoria, "categoría de ingreso");

        Cuenta cuenta = servicioCuenta.buscarCuenta(cuentaId);

        Ingreso ingreso = new Ingreso(monto, descripcion, cuenta, categoria);
        daoMovimiento.crear(ingreso);

        servicioCuenta.ajustarMonto(cuentaId, monto);
    }

    public void registrarGasto(Long cuentaId, double monto, String descripcion, CategoriaGasto categoria) {
        validarMonto(monto);
        validarCategoria(categoria, "categoría de gasto");

        if (!servicioCuenta.tieneSaldoSuficiente(cuentaId, monto)) {
            double saldoActual = servicioCuenta.obtenerMonto(cuentaId);
            throw new IllegalArgumentException(
                    String.format("Saldo insuficiente. Saldo actual: %.2f, monto requerido: %.2f",
                            saldoActual, monto)
            );
        }

        Cuenta cuenta = servicioCuenta.buscarCuenta(cuentaId);

        Gasto gasto = new Gasto(monto, descripcion, cuenta, categoria);
        daoMovimiento.crear(gasto);

        servicioCuenta.ajustarMonto(cuentaId, -monto);
    }

    private void validarMonto(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
    }

    private void validarCategoria(Object categoria, String nombreCategoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La " + nombreCategoria + " es obligatoria");
        }
    }

    public long contarMovimientos(Long cuentaId) {
        return daoMovimiento.contarMovimientos(cuentaId);
    }

    public double sumarIngresosPorCuenta(Long cuentaId) {
        return daoMovimiento.sumIngresosPorCuenta(cuentaId);
    }

    public double sumarGastosPorCuenta(Long cuentaId) {
        return daoMovimiento.sumGastosPorCuenta(cuentaId);
    }

    public List<Ingreso> obtenerIngresosPorCuenta(Long cuentaId) {
        return daoMovimiento.buscarIngresosPorCuenta(cuentaId);
    }

    public List<Gasto> obtenerGastosPorCuenta(Long cuentaId) {
        return daoMovimiento.buscarGastosPorCuenta(cuentaId);
    }

    public List<Movimiento> obtenerMovimientosPorCuenta(Long cuentaId) {
        return daoMovimiento.buscarPorCuenta(cuentaId);
    }

    public double calcularBalancePorCuenta(Long cuentaId) {
        double ingresos = sumarIngresosPorCuenta(cuentaId);
        double gastos = sumarGastosPorCuenta(cuentaId);
        return servicioCuenta.redondearMonto(ingresos - gastos);
    }

    public List<Movimiento> buscarConFiltros(Long cuentaId, String tipo, String categoria, Instant fechaInicio, Instant fechaFin) {

        // Validar cuenta
        if(cuentaId == null){
            throw new IllegalArgumentException("El ID de cuenta no existe");
        }

        // Validar fechas
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha final");
        }

        if((tipo==null || tipo.isEmpty()) && (categoria==null
                || categoria.isEmpty()) && fechaInicio==null && fechaFin==null){
            return daoMovimiento.buscarPorCuenta(cuentaId);
        }

        // Llamada al DAO
        return daoMovimiento.buscarConFiltros(cuentaId, tipo, categoria, fechaInicio, fechaFin);
    }

    // ========================================
    // NUEVOS MÉTODOS PARA PAGINACIÓN
    // ========================================

    /**
     * Obtiene movimientos paginados de una cuenta
     * @param cuentaId ID de la cuenta
     * @param pagina Número de página (1-indexed)
     * @param tamañoPagina Cantidad de movimientos por página
     * @return Lista de movimientos paginados
     */
    public List<Movimiento> obtenerMovimientosPaginados(Long cuentaId, int pagina, int tamañoPagina) {
        validarParametrosPaginacion(cuentaId, pagina, tamañoPagina);

        long totalMovimientos = daoMovimiento.contarMovimientosPorCuenta(cuentaId);
        int totalPaginas = calcularTotalPaginas(totalMovimientos, tamañoPagina);

        // Si la página solicitada es mayor al total, devolver la última página
        if (pagina > totalPaginas && totalPaginas > 0) {
            pagina = totalPaginas;
        }

        // Si no hay movimientos, devolver lista vacía
        if (totalMovimientos == 0) {
            return List.of();
        }

        return daoMovimiento.buscarPorCuentaPaginado(cuentaId, pagina, tamañoPagina);
    }

    /**
     * Calcula el número total de páginas
     * @param cuentaId ID de la cuenta
     * @param tamañoPagina Cantidad de movimientos por página
     * @return Total de páginas
     */
    public int calcularTotalPaginas(Long cuentaId, int tamañoPagina) {
        long totalMovimientos = daoMovimiento.contarMovimientosPorCuenta(cuentaId);
        return calcularTotalPaginas(totalMovimientos, tamañoPagina);
    }

    /**
     * Calcula el número total de páginas dado un total de elementos
     * @param totalElementos Total de elementos
     * @param tamañoPagina Cantidad de elementos por página
     * @return Total de páginas
     */
    private int calcularTotalPaginas(long totalElementos, int tamañoPagina) {
        if (totalElementos == 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalElementos / tamañoPagina);
    }

    /**
     * Valida que los parámetros de paginación sean válidos
     * @param cuentaId ID de la cuenta
     * @param pagina Número de página
     * @param tamañoPagina Tamaño de página
     */
    private void validarParametrosPaginacion(Long cuentaId, int pagina, int tamañoPagina) {
        if (cuentaId == null || cuentaId <= 0) {
            throw new IllegalArgumentException("El ID de cuenta debe ser válido");
        }

        if (pagina < 1) {
            throw new IllegalArgumentException("El número de página debe ser mayor o igual a 1");
        }

        if (tamañoPagina < 1) {
            throw new IllegalArgumentException("El tamaño de página debe ser mayor o igual a 1");
        }

        if (tamañoPagina > 100) {
            throw new IllegalArgumentException("El tamaño de página no puede ser mayor a 100");
        }
    }

    /**
     * Obtiene el total de movimientos de una cuenta
     * @param cuentaId ID de la cuenta
     * @return Total de movimientos
     */
    public long obtenerTotalMovimientos(Long cuentaId) {
        return daoMovimiento.contarMovimientosPorCuenta(cuentaId);
    }

    public List<Movimiento> listarMovimientosConFiltros(Long cuentaId, String tipo, String categoria, String fechaDesde, String fechaHasta, int pagina, int tamanio) {
        Instant fechaInicio = null;
        Instant fechaFin = null;
        if (fechaDesde != null && !fechaDesde.isEmpty()) {
            fechaInicio = Instant.parse(fechaDesde + "T00:00:00Z");
        }
        if (fechaHasta != null && !fechaHasta.isEmpty()) {
            fechaFin = Instant.parse(fechaHasta + "T23:59:59Z");
        }
        int offset = (pagina - 1) * tamanio;
        return daoMovimiento.buscarConFiltros(cuentaId, tipo, categoria, fechaInicio, fechaFin, tamanio, offset);
    }

    public int obtenerTotalPaginasConFiltros(Long cuentaId, String tipo, String categoria, String fechaDesde, String fechaHasta, int tamanio) {
        Instant fechaInicio = null;
        Instant fechaFin = null;
        if (fechaDesde != null && !fechaDesde.isEmpty()) {
            fechaInicio = Instant.parse(fechaDesde + "T00:00:00Z");
        }
        if (fechaHasta != null && !fechaHasta.isEmpty()) {
            fechaFin = Instant.parse(fechaHasta + "T23:59:59Z");
        }
        long total = daoMovimiento.contarConFiltros(cuentaId, tipo, categoria, fechaInicio, fechaFin);
        return (int) Math.ceil((double) total / tamanio);
    }
}