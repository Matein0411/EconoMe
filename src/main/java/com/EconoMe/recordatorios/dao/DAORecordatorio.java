package com.EconoMe.recordatorios.dao;

import com.EconoMe.comun.DAOBase;
import com.EconoMe.recordatorios.modelos.Recordatorio;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.List;


public class DAORecordatorio extends DAOBase<Recordatorio> {
    public DAORecordatorio(){
        super(Recordatorio.class);
    }

    // Listar los recordatorios activos (fechaFin >= fecha actual)
    public List<Recordatorio> listarActivos() {
        return executeQuery(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Recordatorio> cq = cb.createQuery(Recordatorio.class);
            Root<Recordatorio> root = cq.from(Recordatorio.class);

            cq.select(root).where(
                    cb.greaterThanOrEqualTo(root.get("fechaFin"), LocalDate.now())
            );

            return session.createQuery(cq).getResultList();
        });
    }
}