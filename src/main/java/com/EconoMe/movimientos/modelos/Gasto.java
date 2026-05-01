package com.EconoMe.movimientos.modelos;

import com.EconoMe.cuentas.modelos.Cuenta;
import jakarta.persistence.*;

@Entity
@Table(name = "Gasto")
@DiscriminatorValue("GASTO")
public class Gasto extends Movimiento {

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_gasto", nullable = false)
    private CategoriaGasto categoriaGasto;

    public Gasto() {}

    public Gasto(Double monto, String descripcion, Cuenta cuenta, CategoriaGasto categoriaGasto) {
        super(monto, descripcion, cuenta);
        this.categoriaGasto = categoriaGasto;
    }

    public CategoriaGasto getCategoriaGasto() {
        return categoriaGasto;
    }

    public void setCategoriaGasto(CategoriaGasto categoriaGasto) {
        this.categoriaGasto = categoriaGasto;
    }
}