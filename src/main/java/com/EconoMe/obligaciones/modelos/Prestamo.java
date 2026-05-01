package com.EconoMe.obligaciones.modelos;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("PRESTAMO")
public class Prestamo extends ObligacionFinanciera {

    public Prestamo() {
        // Constructor sin parámetros requerido por JPA
    }

    public Prestamo(String nombrePersona, double montoTotal, LocalDate fechaPago) {
        super(nombrePersona, montoTotal, fechaPago);
    }
}