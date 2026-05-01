package com.EconoMe.plantillas.dao;

import com.EconoMe.comun.DAOBase;
import com.EconoMe.plantillas.modelos.Plantilla;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class DAOPlantilla extends DAOBase<Plantilla> {

    public DAOPlantilla() {
        super(Plantilla.class);
    }

    public List<Plantilla> buscarPorFiltros(String nombre, String tipo, String categoria) {
        return executeQuery(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Plantilla> cq = cb.createQuery(Plantilla.class);
            Root<Plantilla> root = cq.from(Plantilla.class);

            // Lista dinámica de predicados (condiciones WHERE)
            List<Predicate> predicates = new ArrayList<>();

            // Filtro opcional: nombre (búsqueda parcial, case-insensitive)
            if (nombre != null && !nombre.trim().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("nombre")),
                        "%" + nombre.trim().toLowerCase() + "%"
                ));
            }

            // Filtro opcional: tipo
            if (tipo != null && !tipo.trim().isEmpty() && !tipo.equals("TODOS")) {
                predicates.add(cb.equal(root.get("tipo"), tipo.toUpperCase()));
            }

            // Filtro opcional: categoría
            if (categoria != null && !categoria.trim().isEmpty() && !categoria.equals("TODAS")) {
                predicates.add(cb.equal(root.get("categoria"), categoria.toUpperCase()));
            }

            // Aplicar todos los predicados
            cq.select(root).where(predicates.toArray(new Predicate[0]));

            // Ordenar por fecha de creación (más recientes primero)
            cq.orderBy(cb.desc(root.get("fechaCreacion")));

            return session.createQuery(cq).getResultList();
        });
    }

    public boolean existePlantillaPorNombre(String nombre) {
        return executeQuery(session -> {
            String hql = "SELECT COUNT(p) FROM Plantilla p " +
                    "WHERE LOWER(TRIM(p.nombre)) = LOWER(TRIM(:nombre))";

            Long count = session.createQuery(hql, Long.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();

            return count > 0;
        });
    }

}