package com.EconoMe.cuentas.servicios;

import com.EconoMe.cuentas.dao.DAOCuenta;
import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.cuentas.modelos.TipoCuenta;

import java.util.List;

public class ServicioCuenta {
    private static final double SALDO_MINIMO = 0.01;
    private static final int LIMITE_CUENTAS_POR_USUARIO = 5;
    private final DAOCuenta daoCuenta;

    public ServicioCuenta(DAOCuenta daoCuenta) {
        this.daoCuenta = daoCuenta;
    }

    public ServicioCuenta() {
        this.daoCuenta = new DAOCuenta();
    }

    /**
     * Crea una nueva cuenta validando el límite
     */
    public void crearCuenta(Cuenta cuenta) {
        // Validar límite de cuentas
        long cantidadCuentas = daoCuenta.contarCuentas();
        if (cantidadCuentas >= LIMITE_CUENTAS_POR_USUARIO) {
            throw new IllegalStateException(
                    String.format("Has alcanzado el límite máximo de %d cuentas",
                            LIMITE_CUENTAS_POR_USUARIO)
            );
        }

        validarCuentaParaCreacion(cuenta);
        daoCuenta.crear(cuenta);
    }

    private void validarCuentaParaCreacion(Cuenta cuenta) {
        validarCamposObligatorios(cuenta);
        validarSaldoPositivo(cuenta.getMonto());
        validarCuentaUnica(cuenta.getNombre().trim(), cuenta.getTipoCuenta());
    }

    private void validarCamposObligatorios(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula");
        }
        if (cuenta.getNombre() == null || cuenta.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la cuenta es obligatorio");
        }
        if (cuenta.getTipoCuenta() == null) {
            throw new IllegalArgumentException("El tipo de cuenta es obligatorio");
        }
    }

    private void validarSaldoPositivo(double saldo) {
        if (saldo < SALDO_MINIMO) {
            throw new IllegalArgumentException(
                    String.format("El saldo debe ser mayor a %.2f", SALDO_MINIMO)
            );
        }
    }

    private void validarCuentaUnica(String nombre, TipoCuenta tipo) {
        if (daoCuenta.existeCuentaPorNombreYTipo(nombre, tipo)) {
            throw new IllegalStateException(
                    "Ya existe una cuenta con el nombre '" + nombre + "' y tipo " + tipo
            );
        }
    }

    public Cuenta buscarCuenta(Long id) {
        Cuenta cuenta = daoCuenta.buscarPorId(id);
        if (cuenta == null) {
            throw new IllegalArgumentException("No se encontró la cuenta con ID " + id);
        }
        return cuenta;
    }

    public void ajustarMonto(Long cuentaId, double cambio) {
        Cuenta cuenta = buscarCuenta(cuentaId);
        double nuevoMonto = calcularNuevoSaldo(cuenta.getMonto(), cambio);

        cuenta.setMonto(nuevoMonto);
        daoCuenta.actualizar(cuenta);
    }

    public double calcularNuevoSaldo(double saldoActual, double cambio) {
        double nuevoSaldo = redondearMonto(saldoActual + cambio);

        if (nuevoSaldo < 0) {
            throw new IllegalArgumentException(
                    String.format("Saldo insuficiente. Saldo actual: %.2f, cambio: %.2f, resultado: %.2f",
                            saldoActual, cambio, nuevoSaldo)
            );
        }
        return nuevoSaldo;
    }

    public boolean tieneSaldoSuficiente(Long cuentaId, double monto) {
        double saldoActual = obtenerMonto(cuentaId);
        return saldoActual >= monto;
    }

    public double obtenerMonto(Long cuentaId) {
        return daoCuenta.obtenerMonto(cuentaId);
    }

    public double redondearMonto(double monto) {
        return Math.round(monto * 100.0) / 100.0;
    }

    public List<Cuenta> listarTodas() {
        return daoCuenta.listar();
    }
}