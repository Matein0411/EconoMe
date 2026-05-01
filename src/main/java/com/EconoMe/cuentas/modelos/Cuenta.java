package com.EconoMe.cuentas.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "Cuenta")
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuenta_id")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "monto", nullable = false)
    private Double monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta", nullable = false)
    private TipoCuenta tipoCuenta;

    public Cuenta(){}

    public Cuenta(String nombre, TipoCuenta tipo, double monto) {
        this.nombre = nombre;
        this.tipoCuenta = tipo;
        this.monto = monto;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getMonto() {
        return monto;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setMonto(Double monto) {
        this.monto = monto;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
