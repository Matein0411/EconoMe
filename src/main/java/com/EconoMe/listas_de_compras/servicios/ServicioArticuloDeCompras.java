package com.EconoMe.listas_de_compras.servicios;

import com.EconoMe.listas_de_compras.dao.DAOArticuloDeCompra;
import com.EconoMe.listas_de_compras.dao.DAOListaDeCompra;
import com.EconoMe.listas_de_compras.modelos.ArticuloDeCompras;
import com.EconoMe.listas_de_compras.modelos.EstadoCompra;
import com.EconoMe.listas_de_compras.modelos.ListaDeCompras;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ServicioArticuloDeCompras {

    // Constantes de límites
    private static final int LIMITE_LISTAS_POR_USUARIO = 5;
    private static final int LIMITE_ARTICULOS_POR_LISTA = 20;

    private final DAOListaDeCompra daoLista;
    private final DAOArticuloDeCompra daoArticulo;

    public ServicioArticuloDeCompras() {
        this.daoLista = new DAOListaDeCompra();
        this.daoArticulo = new DAOArticuloDeCompra();
    }

    /**
     * Registra una nueva lista de compras validando el límite
     */
    public boolean registrarListaDeCompras(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la lista es requerido");
        }

        // Validar límite de listas
        long cantidadListas = daoLista.contarListas();
        if (cantidadListas >= LIMITE_LISTAS_POR_USUARIO) {
            throw new IllegalStateException(
                    String.format("Has alcanzado el límite máximo de %d listas de compras",
                            LIMITE_LISTAS_POR_USUARIO)
            );
        }

        ListaDeCompras lista = new ListaDeCompras();
        lista.setNombre(nombre.trim());
        lista.setEstadoCompra(EstadoCompra.PENDIENTE);
        lista.setPrecioTotal(0.0);

        daoLista.crear(lista);
        return true;
    }

    /**
     * Registra un nuevo artículo validando el límite
     */
    public boolean registrarArticuloDeCompras(String nombre, double precio, ListaDeCompras listaDeCompras) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del artículo es requerido");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }

        // Validar límite de artículos en la lista
        long cantidadArticulos = daoArticulo.contarArticulosPorLista(listaDeCompras.getId());
        if (cantidadArticulos >= LIMITE_ARTICULOS_POR_LISTA) {
            throw new IllegalStateException(
                    String.format("Esta lista ha alcanzado el límite máximo de %d artículos",
                            LIMITE_ARTICULOS_POR_LISTA)
            );
        }

        ArticuloDeCompras articulo = new ArticuloDeCompras(
                nombre.trim(),
                precio,
                EstadoCompra.PENDIENTE,
                listaDeCompras
        );
        daoArticulo.crear(articulo);

        // Actualizar precio total de la lista
        double nuevoTotal = listaDeCompras.getPrecioTotal() + precio;
        listaDeCompras.setPrecioTotal(nuevoTotal);
        daoLista.actualizar(listaDeCompras);

        return true;
    }

    /**
     * Busca una lista de compras por su ID
     */
    public ListaDeCompras buscarListaPorId(Long listaId) {
        if (listaId == null) {
            throw new IllegalArgumentException("El ID de la lista es requerido");
        }
        return daoLista.buscarPorId(listaId);
    }

    /**
     * Elimina un artículo de la lista
     */
    public void eliminarArticulo(Long articuloId) {
        if (articuloId == null) {
            throw new IllegalArgumentException("El ID del artículo es requerido");
        }

        ArticuloDeCompras articulo = daoArticulo.buscarPorId(articuloId);
        if (articulo == null) {
            throw new IllegalArgumentException("Artículo no encontrado");
        }

        // Actualizar total de la lista antes de eliminar
        ListaDeCompras lista = articulo.getListaDeCompras();
        double nuevoTotal = lista.getPrecioTotal() - articulo.getPrecioUnitario();
        lista.setPrecioTotal(Math.max(0, nuevoTotal)); // Evitar negativos
        daoLista.actualizar(lista);

        // Eliminar artículo
        daoArticulo.borrar(articuloId);
    }

    /**
     * Actualiza la lista completa con los estados de los artículos
     * @param idLista ID de la lista
     * @param articulosEstados Map con id del artículo y su nuevo estado
     */
    public void actualizarListaCompleta(Long idLista, List<Map<String, Object>> articulosEstados) {
        if (idLista == null) {
            throw new IllegalArgumentException("El ID de la lista es requerido");
        }

        // 1. Obtener todos los artículos de la lista
        List<ArticuloDeCompras> articulos = daoArticulo.buscarPorCampo("listaDeCompras.id", idLista);

        if (articulos.isEmpty()) {
            return; // No hay artículos que actualizar
        }

        // 2. Actualizar estados según el map recibido
        for (Map<String, Object> estadoMap : articulosEstados) {
            Long articuloId = ((Number) estadoMap.get("id")).longValue();
            String nuevoEstado = (String) estadoMap.get("estado");

            // Buscar artículo en la lista
            articulos.stream()
                    .filter(a -> a.getId().equals(articuloId))
                    .findFirst()
                    .ifPresent(articulo -> {
                        articulo.setEstado(EstadoCompra.valueOf(nuevoEstado));
                    });
        }

        // 3. Actualizar estados en lote
        daoArticulo.actualizarEstadosLote(articulos);

        // 4. Calcular precio total (solo artículos PENDIENTES)
        double precioTotal = articulos.stream()
                .filter(a -> a.getEstado() == EstadoCompra.PENDIENTE)
                .mapToDouble(ArticuloDeCompras::getPrecioUnitario)
                .sum();

        // 5. Determinar estado de la lista
        boolean todoCompletado = articulos.stream()
                .allMatch(a -> a.getEstado() == EstadoCompra.COMPLETADA);

        EstadoCompra estadoLista = todoCompletado ? EstadoCompra.COMPLETADA : EstadoCompra.PENDIENTE;

        // 6. Actualizar lista
        ListaDeCompras lista = daoLista.buscarPorId(idLista);
        if (lista != null) {
            lista.setPrecioTotal(precioTotal);
            lista.setEstadoCompra(estadoLista);
            daoLista.actualizar(lista);
        }
    }

    /**
     * Genera un gasto desde la lista de compras
     */
    public double generarGastoDesdeLista(Long listaId) {
        if (listaId == null) {
            throw new IllegalArgumentException("El ID de la lista es requerido");
        }

        ListaDeCompras lista = daoLista.buscarPorId(listaId);
        if (lista == null) {
            throw new IllegalArgumentException("Lista no encontrada con ID: " + listaId);
        }

        // Calcular total de artículos COMPLETADOS
        double gastoTotal = lista.getArticulos().stream()
                .filter(a -> a.getEstado() == EstadoCompra.COMPLETADA)
                .mapToDouble(ArticuloDeCompras::getPrecioUnitario)
                .sum();

        // Actualizar estado si todo está completado
        boolean todosCompletados = lista.getArticulos().stream()
                .allMatch(a -> a.getEstado() == EstadoCompra.COMPLETADA);

        if (todosCompletados && !lista.getArticulos().isEmpty()) {
            lista.setEstadoCompra(EstadoCompra.COMPLETADA);
            daoLista.actualizar(lista);
        }

        return gastoTotal;
    }

    // Método adicional para marcar artículo como comprado
    public boolean marcarArticuloComoComprado(Long articuloId) {
        if (articuloId == null) {
            throw new IllegalArgumentException("El ID del artículo es requerido");
        }

        daoArticulo.actualizarEstadoArticulo(articuloId, EstadoCompra.COMPLETADA);
        return true;
    }

    public double obtenerGastoAcumulado(Long listaId) {
        return generarGastoDesdeLista(listaId);
    }
    public List<ListaDeCompras> obtenerListas() {
        return daoLista.listar();
    }
    public ArticuloDeCompras buscarArticuloPorId(Long id) {
        return daoArticulo.buscarPorId(id);
    }

    public boolean actualizarArticulo(ArticuloDeCompras articulo) {
        try {
            daoArticulo.actualizar(articulo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}