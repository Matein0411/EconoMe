package com.EconoMe.listas_de_compras.modelos;

import jakarta.persistence.*;

import java.time.Instant;
@Entity
@Table(name = "articulo_de_compras")
public class ArticuloDeCompras {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "articulo_de_compras_id")
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "precio_unitario", nullable = false)
    private double precioUnitario;

    @Column(name = "estado_compra", nullable = false)
    private EstadoCompra estado;

    @ManyToOne
    @JoinColumn(name = "lista_de_compras_id", nullable = false)
    private ListaDeCompras listaDeCompras;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    public ArticuloDeCompras(String nombre, double precioUnitario, EstadoCompra estado, ListaDeCompras listaDeCompras) {
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.estado = estado;
        this.listaDeCompras = listaDeCompras;
        fechaCreacion = Instant.now();
    }
    public ArticuloDeCompras() {
        this.fechaCreacion = Instant.now();
        this.estado = EstadoCompra.PENDIENTE;
        this.precioUnitario = 0.0;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public EstadoCompra getEstado() {
        return estado;
    }

    public ListaDeCompras getListaDeCompras() {
        return listaDeCompras;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public void setEstado(EstadoCompra estado) {
        this.estado = estado;
    }

    public void setListaDeCompras(ListaDeCompras listaDeCompras) {
        this.listaDeCompras = listaDeCompras;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
