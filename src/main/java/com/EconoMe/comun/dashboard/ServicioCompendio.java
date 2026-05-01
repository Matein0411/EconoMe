package com.EconoMe.comun.dashboard;

import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.movimientos.modelos.Gasto;
import com.EconoMe.movimientos.modelos.Ingreso;
import com.EconoMe.movimientos.modelos.Movimiento;

import java.util.*;
import java.util.stream.Collectors;

public class ServicioCompendio {

    public double calcularSaldoTotal(List<Cuenta> cuentas) {
        return cuentas.stream().mapToDouble(Cuenta::getMonto).sum();
    }

    /**
     * Obtiene saldos individuales con clave única: "Nombre (Tipo)"
     * Evita problemas si hay cuentas con el mismo nombre
     */
    public Map<String, Double> obtenerSaldosIndividuales(List<Cuenta> cuentas) {
        if (cuentas == null || cuentas.isEmpty()) {
            return Collections.emptyMap();
        }

        return cuentas.stream()
                .collect(Collectors.toMap(
                        cuenta -> cuenta.getNombre() + " (" + cuenta.getTipoCuenta() + ")",
                        Cuenta::getMonto,
                        (existing, replacement) -> existing, // En caso de duplicados, mantener el primero
                        LinkedHashMap::new // Mantener orden de inserción
                ));
    }

    public double calcularIngresos(List<Movimiento> movimientos) {
        return movimientos.stream()
                .filter(m -> m instanceof Ingreso)
                .mapToDouble(Movimiento::getMonto)
                .sum();

    }

    public double calcularGastos(List<Movimiento> movimientos) {
        return movimientos.stream()
                .filter(m -> m instanceof Gasto)
                .mapToDouble(Movimiento::getMonto)
                .sum();
    }

    public List<Movimiento> obtenerUltimosMovimientos(List<Movimiento> movimientos) {
        return movimientos.stream()
                .sorted((m1, m2) -> m2.getFecha().compareTo(m1.getFecha()))
                .limit(5)
                .collect(Collectors.toList());
    }
    public EstatusDashboard determinarEstatus(List<Cuenta> cuentas, List<Movimiento> movimientos) {
        if (cuentas.isEmpty()) return EstatusDashboard.SIN_CUENTAS;
        if (movimientos.isEmpty()) return EstatusDashboard.SIN_MOVIMIENTOS;
        return EstatusDashboard.OK;
    }





}