package com.EconoMe.comun.dashboard;

import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.movimientos.modelos.Movimiento;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * DTO (Data Transfer Object) que encapsula todos los datos del dashboard
 */
public class DatosDashboard {
    private final EstatusDashboard estatus;
    private final double saldoTotal;
    private final Map<String, Double> saldosIndividuales;
    private final double ingresosTotal;
    private final double gastosTotal;
    private final List<Movimiento> ultimosMovimientos;
    private final List<Cuenta> cuentas;

    // Constructor completo (para estatus OK)
    public DatosDashboard(EstatusDashboard estatus,
                          double saldoTotal,
                          Map<String, Double> saldosIndividuales,
                          double ingresosTotal,
                          double gastosTotal,
                          List<Movimiento> ultimosMovimientos,
                          List<Cuenta> cuentas) {
        this.estatus = estatus;
        this.saldoTotal = saldoTotal;
        this.saldosIndividuales = saldosIndividuales != null ? saldosIndividuales : Collections.emptyMap();
        this.ingresosTotal = ingresosTotal;
        this.gastosTotal = gastosTotal;
        this.ultimosMovimientos = ultimosMovimientos != null ? ultimosMovimientos : Collections.emptyList();
        this.cuentas = cuentas != null ? cuentas : Collections.emptyList();
    }

    // Constructor simplificado para estados sin datos (SIN_CUENTAS, SIN_MOVIMIENTOS)
    public DatosDashboard(EstatusDashboard estatus) {
        this(estatus, 0.0, Collections.emptyMap(), 0.0, 0.0, Collections.emptyList(), Collections.emptyList());
    }

    // Constructor para SIN_MOVIMIENTOS (tiene cuentas pero sin movimientos)
    public DatosDashboard(EstatusDashboard estatus, List<Cuenta> cuentas, double saldoTotal, Map<String, Double> saldosIndividuales) {
        this(estatus, saldoTotal, saldosIndividuales, 0.0, 0.0, Collections.emptyList(), cuentas);
    }

    // Getters
    public EstatusDashboard getEstatus() {
        return estatus;
    }

    public double getSaldoTotal() {
        return saldoTotal;
    }

    public Map<String, Double> getSaldosIndividuales() {
        return saldosIndividuales;
    }

    public double getIngresosTotal() {
        return ingresosTotal;
    }

    public double getGastosTotal() {
        return gastosTotal;
    }

    public List<Movimiento> getUltimosMovimientos() {
        return ultimosMovimientos;
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }

    // Método útil para validaciones
    public boolean esExitoso() {
        return estatus == EstatusDashboard.OK;
    }

    public boolean tieneCuentas() {
        return !cuentas.isEmpty();
    }

    public boolean tieneMovimientos() {
        return !ultimosMovimientos.isEmpty();
    }
}