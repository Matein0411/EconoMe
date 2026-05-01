package com.EconoMe.listas_de_compras.dao;

import com.EconoMe.comun.DAOBase;
import com.EconoMe.listas_de_compras.modelos.ListaDeCompras;

import java.util.List;

public class DAOListaDeCompra extends DAOBase<ListaDeCompras> {
    public DAOListaDeCompra(){
        super(ListaDeCompras.class);
    }
    @Override
    public List<ListaDeCompras> listar() {
        return executeQuery(session -> {
            String hql = "SELECT DISTINCT l FROM ListaDeCompras l LEFT JOIN FETCH l.articulos ORDER BY l.fechaCreacion DESC";
            org.hibernate.query.Query<ListaDeCompras> query = session.createQuery(hql, ListaDeCompras.class);
            return query.getResultList();
        });
    }
    @Override
    public ListaDeCompras buscarPorId(Long id) {
        return executeQuery(session -> {
            String hql = "SELECT l FROM ListaDeCompras l LEFT JOIN FETCH l.articulos WHERE l.id = :id";
            org.hibernate.query.Query<ListaDeCompras> query = session.createQuery(hql, ListaDeCompras.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        });
    }

    /**
     * Cuenta el total de listas de compras
     */
    public long contarListas() {
        return executeQuery(session ->
                session.createQuery("SELECT COUNT(l) FROM ListaDeCompras l", Long.class)
                        .getSingleResult()
        );
    }
}
