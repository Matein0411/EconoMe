package com.EconoMe.recordatorios.modelos;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Optional;

@Entity
@Table(name="recordatorio")
public class Recordatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recordatorio_id")
    private Long id;

    // Se eliminó la relación con Usuario

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(length = 50)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrencia")
    private Recurrencia recurrencia;

    @Column(name = "dias_de_anticipacion")
    private int diasDeAnticipacion;

    private double monto;

    public Recordatorio() {
    }

    public Recordatorio(LocalDate fechaInicio, LocalDate fechaFin, String descripcion, Recurrencia recurrencia, double monto, int diasDeAnticipacion) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.descripcion = descripcion;
        this.recurrencia = recurrencia;
        this.diasDeAnticipacion = diasDeAnticipacion;
        this.monto = monto;
    }

    public LocalDate calcularProximaFechaVencimiento(Recurrencia recurrencia, LocalDate fechaInicio, LocalDate desde) {
        switch (recurrencia) {
            case DIARIA:
                return desde;
            case SEMANAL:
                return sumarPeriodoDeTiempo(fechaInicio, desde, java.time.temporal.ChronoUnit.WEEKS);
            case MENSUAL:
                return sumarPeriodoDeTiempo(fechaInicio, desde, java.time.temporal.ChronoUnit.MONTHS);
            case ANUAL:
                return sumarPeriodoDeTiempo(fechaInicio, desde, java.time.temporal.ChronoUnit.YEARS);
            case NINGUNA:
            default:
                return fechaInicio;
        }
    }

    public LocalDate sumarPeriodoDeTiempo(LocalDate fechaInicio, LocalDate desde, java.time.temporal.ChronoUnit unidad) {
        long diferencia = unidad.between(fechaInicio, desde);
        LocalDate proximaFecha = fechaInicio.plus(diferencia, unidad);

        if (proximaFecha.isBefore(desde)) {
            proximaFecha = proximaFecha.plus(1, unidad);
        }
        return proximaFecha;
    }


    public Optional<LocalDate> obtenerFechaNotificable(LocalDate hoy) {
        LocalDate proximaFechaVencimiento = calcularProximaFechaVencimiento(recurrencia, fechaInicio, hoy);
        int diasDeAnticipacion = getDiasDeAnticipacionAUX(hoy);
        LocalDate fechaNotificacion = proximaFechaVencimiento.minusDays(diasDeAnticipacion);

        if (fechaEsNotificable(hoy, proximaFechaVencimiento, fechaNotificacion)){
            return Optional.of(proximaFechaVencimiento);
        } else {
            return Optional.empty();
        }
    }

    private boolean fechaEsNotificable(LocalDate hoy, LocalDate proximaFechaVencimiento, LocalDate fechaNotificacion) {
        boolean estaEnRangoDeNotificacion = !hoy.isBefore(fechaNotificacion);
        boolean noHaVencido = !hoy.isAfter(proximaFechaVencimiento);
        return estaEnRangoDeNotificacion && noHaVencido;
    }

    private int getDiasDeAnticipacionAUX(LocalDate hoy) {
        int diasDeAnticipacionAUX = diasDeAnticipacion;

        if (recurrencia == Recurrencia.DIARIA && !hoy.isBefore(fechaInicio)) {
            diasDeAnticipacionAUX = 0; // Notificar todos los días desde inicio
        }
        return diasDeAnticipacionAUX;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Recurrencia getRecurrencia() {
        return recurrencia;
    }

    public void setRecurrencia(Recurrencia recurrencia) {
        this.recurrencia = recurrencia;
    }

    public int getDiasDeAnticipacion() {
        return diasDeAnticipacion;
    }

    public void setDiasDeAnticipacion(int diasDeAnticipacion) {
        this.diasDeAnticipacion = diasDeAnticipacion;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}