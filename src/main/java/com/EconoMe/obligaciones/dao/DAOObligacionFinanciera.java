package com.EconoMe.obligaciones.dao;

import com.EconoMe.comun.DAOBase;
import com.EconoMe.obligaciones.modelos.EstadoObligacionFinanciera;
import com.EconoMe.obligaciones.modelos.ObligacionFinanciera;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DAOObligacionFinanciera extends DAOBase<ObligacionFinanciera> {

    public DAOObligacionFinanciera() {
        super(ObligacionFinanciera.class);
    }

    public List<ObligacionFinanciera> buscarConFiltros(String nombrePersona, LocalDate fechaInicio, LocalDate fechaFin){
        return executeQuery(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ObligacionFinanciera> cq = cb.createQuery(ObligacionFinanciera.class);
            Root<ObligacionFinanciera> root = cq.from(ObligacionFinanciera.class);

            List<Predicate> predicates = new ArrayList<>();

            // Filtro: solo pendientes
            predicates.add(cb.equal(root.get("estado"), EstadoObligacionFinanciera.PENDIENTE));

            // Filtro: nombre persona (si existe)
            if(nombrePersona != null && !nombrePersona.trim().isEmpty()){
                predicates.add(cb.like(
                        cb.lower(root.get("nombrePersona")),
                        "%" + nombrePersona.toLowerCase() + "%"
                ));
            }

            // Filtro: fecha Inicio (si existe)
            if ( fechaInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaPago"), fechaInicio));
            }

            // Filtro: fecha fin (si existe)
            if (fechaFin != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaPago"), fechaFin));
            }

            // aplicar todos los filtros con AND
            cq.where(cb.and(predicates.toArray(new Predicate[0])));

            return session.createQuery(cq).getResultList();
        });
    }
}
