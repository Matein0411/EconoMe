package com.EconoMe.movimientos.modelos;

import com.EconoMe.cuentas.modelos.Cuenta;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "Movimiento")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(
        name = "tipo",
        discriminatorType = DiscriminatorType.STRING
)
public abstract class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movimiento_id")
    private Long id;

    @Column(name = "monto", nullable = false)
    private Double monto;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;

    @Column(name = "fecha", nullable = false)
    private Instant fecha;

    public Movimiento() {}

    public Movimiento(Double monto, String descripcion, Cuenta cuenta) {
        if(monto == null || descripcion == null || descripcion.trim().isEmpty() || cuenta == null){
            throw new IllegalArgumentException("Llenar todos los campos obligatorios");
        }

        if(monto <= 0){
            throw new IllegalArgumentException("Monto inválido. Debe ser mayor a cero: "+monto);
        }
        this.monto = monto;
        this.descripcion = descripcion;
        this.cuenta = cuenta;
        this.fecha = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    @Transient
    public String getTipo() {
        if (this instanceof Ingreso) {
            return "INGRESO";
        }
        if (this instanceof Gasto) {
            return "GASTO";
        }
        return null;
    }

    @Transient
    public String getCategoria() {
        if (this instanceof Gasto g) {
            return g.getCategoriaGasto().name();
        }
        if (this instanceof Ingreso i) {
            return i.getCategoriaIngreso().name();
        }
        return "";
    }

    @Transient
    public String getFechaFormateada() {
        if (fecha == null) {
            return "";
        }
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        .withZone(ZoneId.systemDefault());

        return formatter.format(fecha);
    }
}