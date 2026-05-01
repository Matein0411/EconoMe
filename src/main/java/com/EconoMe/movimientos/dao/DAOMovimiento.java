package com.EconoMe.movimientos.dao;

import com.EconoMe.comun.DAOBase;
import com.EconoMe.movimientos.modelos.*;

import java.time.Instant;
import java.util.List;

public class DAOMovimiento extends DAOBase<Movimiento> {

    public DAOMovimiento() {
        super(Movimiento.class);
    }

    public double sumIngresosPorCuenta(Long cuentaId) {
        return executeQuery(session -> session.createQuery(
                        "select coalesce(sum(m.monto), 0) from Movimiento m " +
                                "where m.cuenta.id = :cuentaId and type(m) = Ingreso",
                        Double.class
                ).setParameter("cuentaId", cuentaId)
                .getSingleResult());
    }


    public double sumGastosPorCuenta(Long cuentaId) {
        return executeQuery(session -> session.createQuery(
                        "select coalesce(sum(m.monto), 0) from Movimiento m " +
                                "where m.cuenta.id = :cuentaId and type(m) = Gasto",
                        Double.class
                ).setParameter("cuentaId", cuentaId)
                .getSingleResult());
    }

    public List<Ingreso> buscarIngresosPorCuenta(Long cuentaId) {
        return executeQuery(session -> session.createQuery(
                        "select i from Ingreso i " +
                                "where i.cuenta.id = :cuentaId " +
                                "order by i.fecha desc",
                        Ingreso.class
                ).setParameter("cuentaId", cuentaId)
                .getResultList());
    }


    public List<Gasto> buscarGastosPorCuenta(Long cuentaId) {
        return executeQuery(session -> session.createQuery(
                        "select g from Gasto g " +
                                "where g.cuenta.id = :cuentaId " +
                                "order by g.fecha desc",
                        Gasto.class
                ).setParameter("cuentaId", cuentaId)
                .getResultList());
    }

    public long contarMovimientos(Long cuentaId) {
        return executeQuery(session -> session.createQuery(
                        "select count(m) from Movimiento m where m.cuenta.id = :cuentaId",
                        Long.class
                ).setParameter("cuentaId", cuentaId)
                .getSingleResult());
    }
    public List<Movimiento> buscarPorCuenta(Long cuentaId) {
        return executeQuery(session -> session.createQuery(
                        "select m from Movimiento m " +
                                "where m.cuenta.id = :cuentaId " +
                                "order by m.fecha desc",
                        Movimiento.class
                ).setParameter("cuentaId", cuentaId)
                .getResultList());
    }

    public List<Movimiento> buscarConFiltros(Long cuentaId, String tipo, String categoria, Instant fechaInicio, Instant fechaFin) {
        return executeQuery(session -> {
            StringBuilder hql = new StringBuilder("select m from Movimiento m where m.cuenta.id = :cuentaId");

            // Filtro por tipo (INGRESO o GASTO)
            if(tipo != null && !tipo.isEmpty()){
                if(tipo.equals("INGRESO")){
                    hql.append(" and type(m) = Ingreso");
                }else if (tipo.equals("GASTO")){
                    hql.append(" and type(m) = Gasto");
                }
            }

            // Filtro por categoría
            if(categoria != null && !categoria.isEmpty()){
                if(tipo != null && tipo.equals("INGRESO")){
                    hql.append(" and cast(m as Ingreso).categoriaIngreso = :categoria");
                }else if (tipo != null && tipo.equals("GASTO")){
                    hql.append(" and cast(m as Gasto).categoriaGasto = :categoria");
                }
            }

            // Filtro por fecha inicio
            if(fechaInicio != null){
                hql.append(" and m.fecha >= :fechaInicio");
            }

            // Filtro por fecha fin
            if(fechaFin != null){
                hql.append(" and m.fecha <= :fechaFin");
            }

            hql.append(" order by m.fecha desc");

            // Crear Query
            var query = session.createQuery(hql.toString(), Movimiento.class);

            // Settear parámetros
            query.setParameter("cuentaId", cuentaId);

            if(categoria != null && !categoria.isEmpty()){
                if("INGRESO".equals(tipo)){
                    query.setParameter("categoria", CategoriaIngreso.valueOf(categoria));
                }else if ("GASTO".equals(tipo)){
                    query.setParameter("categoria", CategoriaGasto.valueOf(categoria));
                }
            }

            if (fechaInicio != null) {
                query.setParameter("fechaInicio", fechaInicio);
            }

            if (fechaFin != null) {
                query.setParameter("fechaFin", fechaFin);
            }

            return query.getResultList();
        });
    }

    public List<Movimiento> buscarConFiltros(Long cuentaId, String tipo, String categoria, Instant fechaInicio, Instant fechaFin, int limit, int offset) {
        return executeQuery(session -> {
            StringBuilder hql = new StringBuilder();
            boolean esIngreso = "INGRESO".equals(tipo);
            boolean esGasto = "GASTO".equals(tipo);
            if (esIngreso) {
                hql.append("select m from Ingreso m where m.cuenta.id = :cuentaId");
            } else if (esGasto) {
                hql.append("select m from Gasto m where m.cuenta.id = :cuentaId");
            } else {
                hql.append("select m from Movimiento m where m.cuenta.id = :cuentaId");
            }
            if (categoria != null && !categoria.isEmpty()) {
                if (esIngreso) {
                    hql.append(" and m.categoriaIngreso = :categoria");
                } else if (esGasto) {
                    hql.append(" and m.categoriaGasto = :categoria");
                }
            }
            if (fechaInicio != null) {
                hql.append(" and m.fecha >= :fechaInicio");
            }
            if (fechaFin != null) {
                hql.append(" and m.fecha <= :fechaFin");
            }
            hql.append(" order by m.fecha desc");
            var query = session.createQuery(hql.toString(), Movimiento.class)
                .setParameter("cuentaId", cuentaId);
            if (categoria != null && !categoria.isEmpty()) {
                if (esIngreso) {
                    query.setParameter("categoria", CategoriaIngreso.valueOf(categoria));
                } else if (esGasto) {
                    query.setParameter("categoria", CategoriaGasto.valueOf(categoria));
                }
            }
            if (fechaInicio != null) {
                query.setParameter("fechaInicio", fechaInicio);
            }
            if (fechaFin != null) {
                query.setParameter("fechaFin", fechaFin);
            }
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }

    public long contarConFiltros(Long cuentaId, String tipo, String categoria, Instant fechaInicio, Instant fechaFin) {
        return executeQuery(session -> {
            StringBuilder hql = new StringBuilder();
            boolean esIngreso = "INGRESO".equals(tipo);
            boolean esGasto = "GASTO".equals(tipo);

            // 1. Definir la tabla base según el tipo
            if (esIngreso) {
                hql.append("select count(m) from Ingreso m where m.cuenta.id = :cuentaId");
            } else if (esGasto) {
                hql.append("select count(m) from Gasto m where m.cuenta.id = :cuentaId");
            } else {
                hql.append("select count(m) from Movimiento m where m.cuenta.id = :cuentaId");
            }

            // 2. Construir condiciones
            if (categoria != null && !categoria.isEmpty()) {
                if (esIngreso) {
                    hql.append(" and m.categoriaIngreso = :categoria");
                } else if (esGasto) {
                    hql.append(" and m.categoriaGasto = :categoria");
                }
            }

            if (fechaInicio != null) {
                hql.append(" and m.fecha >= :fechaInicio");
            }

            if (fechaFin != null) {
                hql.append(" and m.fecha <= :fechaFin");
            }

            // 3. Crear Query
            var query = session.createQuery(hql.toString(), Long.class)
                    .setParameter("cuentaId", cuentaId);

            // 4. Asignar parámetros (AQUÍ ESTABA EL ERROR)
            if (categoria != null && !categoria.isEmpty()) {
                // CORRECCIÓN: Convertir String a Enum tal como hiciste en buscarConFiltros
                if (esIngreso) {
                    query.setParameter("categoria", CategoriaIngreso.valueOf(categoria));
                } else if (esGasto) {
                    query.setParameter("categoria", CategoriaGasto.valueOf(categoria));
                }
            }

            if (fechaInicio != null) {
                query.setParameter("fechaInicio", fechaInicio);
            }

            if (fechaFin != null) {
                query.setParameter("fechaFin", fechaFin);
            }

            return query.getSingleResult();
        });
    }
    // ========================================
    // NUEVOS MÉTODOS PARA PAGINACIÓN
    // ========================================

    /**
     * Busca movimientos de una cuenta con paginación
     * @param cuentaId ID de la cuenta
     * @param pagina Número de página (1-indexed)
     * @param tamaño Cantidad de movimientos por página
     * @return Lista de movimientos paginados
     */
    public List<Movimiento> buscarPorCuentaPaginado(Long cuentaId, int pagina, int tamaño) {
        return executeQuery(session -> {
            int offset = (pagina - 1) * tamaño;

            return session.createQuery(
                            "select m from Movimiento m " +
                                    "where m.cuenta.id = :cuentaId " +
                                    "order by m.fecha desc",
                            Movimiento.class
                    ).setParameter("cuentaId", cuentaId)
                    .setFirstResult(offset)
                    .setMaxResults(tamaño)
                    .getResultList();
        });
    }

    /**
     * Cuenta el total de movimientos de una cuenta
     * @param cuentaId ID de la cuenta
     * @return Total de movimientos
     */
    public long contarMovimientosPorCuenta(Long cuentaId) {
        return executeQuery(session -> session.createQuery(
                        "select count(m) from Movimiento m where m.cuenta.id = :cuentaId",
                        Long.class
                ).setParameter("cuentaId", cuentaId)
                .getSingleResult());
    }
}
