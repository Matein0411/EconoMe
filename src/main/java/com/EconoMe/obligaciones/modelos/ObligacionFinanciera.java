package com.EconoMe.obligaciones.modelos;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "obligacion_financiera")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_obligacion", discriminatorType = DiscriminatorType.STRING)
public abstract class ObligacionFinanciera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "obligacion_financiera_id")
    private Long id;

    @Column(name = "nombre_persona", nullable = false)
    private String nombrePersona;

    @Column(name = "monto_total", nullable = false)
    private double montoTotal;

    @Column(name = "monto_pagado", nullable = false)
    private double montoPagado;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoObligacionFinanciera estado;

    // ========== CONSTRUCTORES ==========

    public ObligacionFinanciera() {
        // Constructor sin parámetros requerido por JPA
    }

    public ObligacionFinanciera(String nombrePersona, double montoTotal, LocalDate fechaPago) {
        this.nombrePersona = nombrePersona;
        this.montoTotal = montoTotal;
        this.montoPagado = 0.0;
        this.fechaPago = fechaPago;
        this.estado = EstadoObligacionFinanciera.PENDIENTE;
    }

    // ========== GETTERS ==========

    public Long getId() {
        return id;
    }

    public String getNombrePersona() {
        return nombrePersona;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public EstadoObligacionFinanciera getEstado() {
        return estado;
    }

    // ========== SETTERS ==========

    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public void setMontoPagado(double montoPagado) {
        this.montoPagado = montoPagado;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public void setEstado(EstadoObligacionFinanciera estado) {
        this.estado = estado;
    }

    // ========== MÉTODOS DE NEGOCIO ==========

    /**
     * Calcula el saldo pendiente de pago
     * @return La diferencia entre el monto total y lo pagado
     */
    public double calcularSaldoPendiente() {
        return montoTotal - montoPagado;
    }

    /**
     * Registra un abono a la obligación financiera
     * @param monto El monto a abonar
     */
    public void registrarAbono(double monto) {
        if (monto <= 0) {
            return;
        }

        // Evitar que se exceda el monto total
        montoPagado = Math.min(montoPagado + monto, montoTotal);

        // Actualizar estado si está completamente pagada
        if (montoPagado >= montoTotal) {
            estado = EstadoObligacionFinanciera.PAGADA;
        }
    }

    /**
     * Verifica si la obligación está completamente pagada
     * @return true si el monto pagado es mayor o igual al total
     */
    public boolean estaPagadaCompletamente() {
        return montoPagado >= montoTotal;
    }

    /**
     * Verifica si puede recibir un abono/cobro
     * @param monto El monto a validar
     * @return true si el monto es válido y aún hay saldo pendiente
     */
    public boolean puedeRecibirPago(double monto) {
        return monto > 0 && calcularSaldoPendiente() > 0;
    }

    /**
     * Verifica si la obligación está vencida
     * @return true si la fecha de pago es anterior a hoy y aún está pendiente
     */
    public boolean estaVencida() {
        return estado == EstadoObligacionFinanciera.PENDIENTE
                && fechaPago.isBefore(LocalDate.now());
    }
    public void setId(long l) {
        this.id = l;
    }
}