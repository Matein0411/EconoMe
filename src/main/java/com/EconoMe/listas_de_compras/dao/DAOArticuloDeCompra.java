package com.EconoMe.listas_de_compras.dao;

import com.EconoMe.comun.DAOBase;
import com.EconoMe.listas_de_compras.modelos.ArticuloDeCompras;
import com.EconoMe.listas_de_compras.modelos.EstadoCompra;

import java.util.List;

public class DAOArticuloDeCompra  extends DAOBase<ArticuloDeCompras> {
    public DAOArticuloDeCompra(){
        super(ArticuloDeCompras.class);
    }

    public void actualizarEstadoArticulos(List<Long> idsArticulos, EstadoCompra nuevoEstado) {
        if (idsArticulos == null || idsArticulos.isEmpty()) {
            return;
        }

        executeInTransaction(session -> {
            String hql = "UPDATE ArticuloDeCompras a SET a.estado = :nuevoEstado " +
                    "WHERE a.id IN :ids";

            session.createMutationQuery(hql)
                    .setParameter("nuevoEstado", nuevoEstado)
                    .setParameter("ids", idsArticulos)
                    .executeUpdate();
        });
    }

    public void actualizarEstadoArticulo(Long idArticulo, EstadoCompra nuevoEstado) {
        executeInTransaction(session -> {
            ArticuloDeCompras articulo = session.get(ArticuloDeCompras.class, idArticulo);
            if (articulo != null) {
                articulo.setEstado(nuevoEstado);
                session.merge(articulo);
            }
        });
    }

    /**
     * Cuenta artículos de una lista específica
     */
    public long contarArticulosPorLista(Long idLista) {
        return executeQuery(session ->
                session.createQuery(
                                "SELECT COUNT(a) FROM ArticuloDeCompras a WHERE a.listaDeCompras.id = :idLista",
                                Long.class
                        )
                        .setParameter("idLista", idLista)
                        .getSingleResult()
        );
    }

    /**
     * Actualiza estados de múltiples artículos en lote
     */
    public void actualizarEstadosLote(List<ArticuloDeCompras> articulos) {
        executeInTransaction(session -> {
            for (ArticuloDeCompras articulo : articulos) {
                session.merge(articulo);
            }
        });
    }
}
