package com.EconoMe.obligaciones.servicios;

import com.EconoMe.movimientos.modelos.CategoriaGasto;
import com.EconoMe.movimientos.modelos.CategoriaIngreso;
import com.EconoMe.movimientos.servicios.ServicioMovimiento;
import com.EconoMe.obligaciones.dao.DAOObligacionFinanciera;
import com.EconoMe.obligaciones.modelos.Deuda;
import com.EconoMe.obligaciones.modelos.ObligacionFinanciera;
import com.EconoMe.obligaciones.modelos.Prestamo;
import com.EconoMe.obligaciones.modelos.EstadoObligacionFinanciera;

public class ServicioObligacionFinanciera {
    private final DAOObligacionFinanciera daoObligacionFinanciera;
    private final ServicioMovimiento servicioMovimiento;

    public ServicioObligacionFinanciera() {
        this.daoObligacionFinanciera = new DAOObligacionFinanciera();
        this.servicioMovimiento = new ServicioMovimiento();
    }

    /**
     * Realiza un abono a una obligación financiera (Deuda o Préstamo)
     * @param idCuenta ID de la cuenta desde donde se realiza el pago
     * @param idObligacion ID de la obligación financiera
     * @param monto Monto a abonar
     */
    public void abonarADeuda(Long idCuenta, Long idObligacion, double monto) {
        // Validación de monto
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto del abono debe ser mayor a 0");
        }

        // Buscar obligación
        ObligacionFinanciera obligacion = daoObligacionFinanciera.buscarPorId(idObligacion);
        if (obligacion == null) {
            throw new IllegalArgumentException("Obligación financiera no encontrada");
        }

        // Validar estado
        if (obligacion.getEstado() == EstadoObligacionFinanciera.PAGADA) {
            throw new IllegalStateException("La obligación ya está pagada completamente");
        }

        // Validar que el monto no exceda el saldo pendiente
        double saldoPendiente = obligacion.calcularSaldoPendiente();
        if (monto > saldoPendiente) {
            throw new IllegalArgumentException(
                    "El monto ($" + monto + ") excede el saldo pendiente ($" + saldoPendiente + ")"
            );
        }

        // PRIMERO: Registrar movimiento según el tipo de obligación
        // Si falla por saldo insuficiente, se lanza excepción ANTES de modificar la obligación
        String descripcion;

        if (obligacion instanceof Deuda) {
            descripcion = "Abono deuda a " + obligacion.getNombrePersona();
            servicioMovimiento.registrarGasto(
                    idCuenta,
                    monto,
                    descripcion,
                    CategoriaGasto.ABONO_DEUDA
            );
        } else if (obligacion instanceof Prestamo) {
            descripcion = "Abono préstamo de " + obligacion.getNombrePersona();
            servicioMovimiento.registrarIngreso(
                    idCuenta,
                    monto,
                    descripcion,
                    CategoriaIngreso.ABONO_PRESTAMO
            );
        }

        // SEGUNDO: Solo si el movimiento fue exitoso, registrar el abono en la obligación
        obligacion.registrarAbono(monto);
        daoObligacionFinanciera.actualizar(obligacion);
    }
}