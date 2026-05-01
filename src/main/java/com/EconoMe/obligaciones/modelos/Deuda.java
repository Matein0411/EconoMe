package com.EconoMe.obligaciones.modelos;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("DEUDA")
public class Deuda extends ObligacionFinanciera {

    public Deuda() {
        // Constructor sin parámetros requerido por JPA
    }

    public Deuda(String nombrePersona, double montoTotal, LocalDate fechaPago) {
        super(nombrePersona, montoTotal, fechaPago);
    }


}