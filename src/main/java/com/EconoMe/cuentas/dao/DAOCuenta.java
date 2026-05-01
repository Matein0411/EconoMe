package com.EconoMe.cuentas.dao;

import com.EconoMe.comun.DAOBase;
import com.EconoMe.cuentas.modelos.Cuenta;
import com.EconoMe.cuentas.modelos.TipoCuenta;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class DAOCuenta extends DAOBase<Cuenta> {

    public DAOCuenta() {super(Cuenta.class);}

    public boolean existeCuentaPorNombreYTipo(String nombre, TipoCuenta tipo) {
        return executeQuery(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Cuenta> root = cq.from(Cuenta.class);

            Expression<String> nombreNormalizado = cb.lower(
                    cb.trim(root.get("nombre"))
            );

            String nombreBuscar = nombre.trim().toLowerCase();

            cq.select(cb.count(root))
                    .where(
                            cb.and(
                                    cb.equal(nombreNormalizado, nombreBuscar),
                                    cb.equal(root.get("tipoCuenta"), tipo)
                            )
                    );

            Long count = session.createQuery(cq).getSingleResult();
            return count > 0;
        });
    }

    public double obtenerMonto(Long cuentaId) {
        return executeQuery(session -> {
            Double monto = session.createQuery(
                            "select cu.monto from Cuenta cu where cu.id = :id",
                            Double.class
                    ).setParameter("id", cuentaId)
                    .uniqueResult();
            return monto != null ? monto : 0.0;
        });
    }

    /**
     * Cuenta el total de cuentas registradas
     */
    public long contarCuentas() {
        return executeQuery(session ->
                session.createQuery("SELECT COUNT(c) FROM Cuenta c", Long.class)
                        .getSingleResult()
        );
    }
}