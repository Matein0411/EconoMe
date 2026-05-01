package com.EconoMe.movimientos.modelos;

import com.EconoMe.cuentas.modelos.Cuenta;
import jakarta.persistence.*;

@Entity
@Table(name = "Ingreso")
@DiscriminatorValue("INGRESO")
public class Ingreso extends Movimiento {

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_ingreso", nullable = false)
    private CategoriaIngreso categoriaIngreso;

    public Ingreso() {}

    public Ingreso(Double monto, String descripcion, Cuenta cuenta, CategoriaIngreso categoriaIngreso) {
        super(monto, descripcion, cuenta);
        this.categoriaIngreso = categoriaIngreso;
    }

    public CategoriaIngreso getCategoriaIngreso() {
        return categoriaIngreso;
    }

    public void setCategoriaIngreso(CategoriaIngreso categoriaIngreso) {
        this.categoriaIngreso = categoriaIngreso;
    }
}