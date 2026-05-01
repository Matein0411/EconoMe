package com.EconoMe.comun.dashboard;

import com.EconoMe.cuentas.dao.DAOCuenta;
import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.movimientos.dao.DAOMovimiento;
import com.EconoMe.movimientos.modelos.Movimiento;

import java.util.List;
import java.util.Map;

/**
 * Servicio que orquesta la lógica del Dashboard
 * Versión sin Usuario/Cartera - muestra todas las cuentas del sistema
 */
public class ServicioDashboard {

    private final DAOCuenta daoCuenta;
    private final DAOMovimiento daoMovimiento;
    private final ServicioCompendio servicioCompendio;

    // Constructor para producción
    public ServicioDashboard() {
        this.daoCuenta = new DAOCuenta();
        this.daoMovimiento = new DAOMovimiento();
        this.servicioCompendio = new ServicioCompendio();
    }

    // Constructor para testing (inyección de dependencias)
    public ServicioDashboard(DAOCuenta daoCuenta,
                             DAOMovimiento daoMovimiento,
                             ServicioCompendio servicioCompendio) {
        this.daoCuenta = daoCuenta;
        this.daoMovimiento = daoMovimiento;
        this.servicioCompendio = servicioCompendio;
    }

    /**
     * Obtiene el resumen completo del dashboard
     * Muestra todas las cuentas y movimientos del sistema
     *
     * @return DatosDashboard con toda la información
     */
    public DatosDashboard obtenerResumen() {
        try {
            // 1. Obtener todas las cuentas del sistema
            List<Cuenta> cuentas = daoCuenta.listar();

            // 2. Verificar si hay cuentas
            if (cuentas == null || cuentas.isEmpty()) {
                return new DatosDashboard(EstatusDashboard.SIN_CUENTAS);
            }

            // 3. Obtener todos los movimientos de todas las cuentas
            List<Movimiento> todosMovimientos = obtenerTodosLosMovimientos(cuentas);

            // 4. Determinar estatus
            EstatusDashboard estatus = servicioCompendio.determinarEstatus(cuentas, todosMovimientos);

            // 5. Si no hay movimientos, retornar dashboard con solo cuentas
            if (estatus == EstatusDashboard.SIN_MOVIMIENTOS) {
                double saldoTotal = servicioCompendio.calcularSaldoTotal(cuentas);
                Map<String, Double> saldosIndividuales = servicioCompendio.obtenerSaldosIndividuales(cuentas);

                return new DatosDashboard(estatus, cuentas, saldoTotal, saldosIndividuales);
            }

            // 6. Calcular todas las métricas (delegar a ServicioCompendio)
            double saldoTotal = servicioCompendio.calcularSaldoTotal(cuentas);
            Map<String, Double> saldosIndividuales = servicioCompendio.obtenerSaldosIndividuales(cuentas);
            double ingresosTotal = servicioCompendio.calcularIngresos(todosMovimientos);
            double gastosTotal = servicioCompendio.calcularGastos(todosMovimientos);
            List<Movimiento> ultimosMovimientos = servicioCompendio.obtenerUltimosMovimientos(todosMovimientos);

            // 7. Construir objeto de respuesta
            return new DatosDashboard(
                    estatus,
                    saldoTotal,
                    saldosIndividuales,
                    ingresosTotal,
                    gastosTotal,
                    ultimosMovimientos,
                    cuentas
            );

        } catch (Exception e) {
            // Si hay error en consultas, propagar la excepción
            System.err.println("Error al obtener resumen del dashboard: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener datos del dashboard", e);
        }
    }

    /**
     * Obtiene todos los movimientos de todas las cuentas
     *
     * @param cuentas Lista de cuentas
     * @return Lista consolidada de todos los movimientos
     */
    private List<Movimiento> obtenerTodosLosMovimientos(List<Cuenta> cuentas) {
        if (cuentas == null || cuentas.isEmpty()) {
            return List.of();
        }

        return cuentas.stream()
                .flatMap(cuenta -> {
                    try {
                        List<Movimiento> movimientos = daoMovimiento.buscarPorCuenta(cuenta.getId());
                        return movimientos != null ? movimientos.stream() : List.<Movimiento>of().stream();
                    } catch (Exception e) {
                        // Log del error pero continuar con otras cuentas
                        System.err.println("Error al obtener movimientos de cuenta " + cuenta.getId() + ": " + e.getMessage());
                        return List.<Movimiento>of().stream();
                    }
                })
                .toList();
    }

    /**
     * Verifica si el sistema tiene cuentas registradas
     *
     * @return true si hay al menos una cuenta
     */
    public boolean tieneCuentas() {
        List<Cuenta> cuentas = daoCuenta.listar();
        return cuentas != null && !cuentas.isEmpty();
    }

    /**
     * Verifica si el sistema tiene movimientos registrados
     *
     * @return true si hay al menos un movimiento
     */
    public boolean tieneMovimientos() {
        List<Cuenta> cuentas = daoCuenta.listar();
        if (cuentas == null || cuentas.isEmpty()) {
            return false;
        }

        return cuentas.stream()
                .anyMatch(cuenta -> {
                    List<Movimiento> movimientos = daoMovimiento.buscarPorCuenta(cuenta.getId());
                    return movimientos != null && !movimientos.isEmpty();
                });
    }
}