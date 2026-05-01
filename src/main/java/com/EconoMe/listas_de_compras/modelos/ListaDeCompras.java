package com.EconoMe.listas_de_compras.modelos;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lista_de_compras")
public class ListaDeCompras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lista_de_compras_id")
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_compra", nullable = false)
    private EstadoCompra estadoCompra;

    @Column(name = "precio_total", nullable = false)
    private double precioTotal;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @OneToMany(mappedBy = "listaDeCompras", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticuloDeCompras> articulos = new ArrayList<>();

    public ListaDeCompras() {
        this.fechaCreacion = Instant.now();
        this.estadoCompra = EstadoCompra.PENDIENTE;
        this.precioTotal = 0.0;
    }

    public ListaDeCompras(String nombre, EstadoCompra estadoCompra) {
        this.nombre = nombre;
        this.estadoCompra = estadoCompra;
        fechaCreacion = Instant.now();
        precioTotal = 0.0;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public EstadoCompra getEstadoCompra() {
        return estadoCompra;
    }

    public void setEstadoCompra(EstadoCompra estadoCompra) {
        this.estadoCompra = estadoCompra;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<ArticuloDeCompras> getArticulos() {
        return articulos;
    }

    public void setArticulos(List<ArticuloDeCompras> articulos) {
        this.articulos = articulos;
    }

    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                .withZone(ZoneId.systemDefault());
        return formatter.format(fechaCreacion);
    }

    public Long getId() {
        return id;
    }
    // Métodos helper para JSP
    public int getTotalArticulos() {
        return articulos != null ? articulos.size() : 0;
    }

    public long getArticulosComprados() {
        if (articulos == null) return 0;
        return articulos.stream()
                .filter(a -> a.getEstado() == EstadoCompra.COMPLETADA)
                .count();
    }

    public List<ArticuloDeCompras> getArticulosPendientes() {
        if (articulos == null) return new ArrayList<>();
        return articulos.stream()
                .filter(a -> a.getEstado() == EstadoCompra.PENDIENTE)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<ArticuloDeCompras> getArticulosCompletados() {
        if (articulos == null) return new ArrayList<>();
        return articulos.stream()
                .filter(a -> a.getEstado() == EstadoCompra.COMPLETADA)
                .collect(java.util.stream.Collectors.toList());
    }

    public void setId(Long id) {
        this.id = id;
    }
}
