package com.EconoMe.plantillas.modelos;

import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.movimientos.modelos.CategoriaGasto;
import com.EconoMe.movimientos.modelos.CategoriaIngreso;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "plantilla")
public class Plantilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long plantilla_id;

    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id")
    private Cuenta cuenta;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "monto", nullable = false)
    private double monto;

    @Column(name = "tipo", nullable = false)
    private String tipo; // "INGRESO" o "GASTO"

    @Column(name = "categoria", nullable = false)
    private String categoria;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public Plantilla(){

    }

    public Plantilla(String nombre, double monto) {
        this.nombre = nombre;
        this.monto = monto;
    }

    public void setCategoriaGasto(CategoriaGasto categoria) {
        this.tipo = "GASTO";
        this.categoria = categoria.name();
    }

    public void setCategoriaIngreso(CategoriaIngreso categoria) {
        this.tipo = "INGRESO";
        this.categoria = categoria.name();
    }

    // Getters y Setters completos
    public Long getId() { return plantilla_id; }

    /*
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
     */

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Cuenta getCuenta() { return cuenta; }
    public void setCuenta(Cuenta cuenta) { this.cuenta = cuenta; }

    public void setCategoria(String categoria) { this.categoria = categoria; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {this.fechaCreacion = fechaCreacion;}

    public String getCategoria() {
        return categoria;
    }

    public Object getCategoriaEnum() {
        if ("GASTO".equals(this.tipo)) {
            return CategoriaGasto.valueOf(this.categoria);
        } else {
            return CategoriaIngreso.valueOf(this.categoria);
        }
    }

    public void setId(long plantilla_id) {
        this.plantilla_id = plantilla_id;
    }
}
