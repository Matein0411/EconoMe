package com.EconoMe.comun;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class DAOBase<T> {

    private static SessionFactory factory;
    private final Class<T> entityClass;

    protected DAOBase(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected static SessionFactory getSessionFactory() {
        if (factory == null) {
            try {
                Configuration configuration = new Configuration().configure();

                // Lógica de detección automática de entorno (Azure vs Local)
                String dbUrl = System.getenv("DB_URL");
                String dbUser = System.getenv("DB_USER");
                String dbPassword = System.getenv("DB_PASSWORD");

                if (dbUrl != null && !dbUrl.isEmpty()) {
                    // Configuración para Producción (Azure/Docker)
                    configuration.setProperty("hibernate.hikari.jdbcUrl", dbUrl);
                    configuration.setProperty("hibernate.hikari.username", dbUser);
                    configuration.setProperty("hibernate.hikari.password", dbPassword);

                    // Respaldo para driver estándar
                    configuration.setProperty("hibernate.connection.url", dbUrl);
                    configuration.setProperty("hibernate.connection.username", dbUser);
                    configuration.setProperty("hibernate.connection.password", dbPassword);
                }

                factory = configuration.buildSessionFactory();

            } catch (Exception e) {
                System.err.println("Fallo al iniciar SessionFactory: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return factory;
    }

    protected void executeInTransaction(Consumer<Session> operation) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                operation.accept(session);
                transaction.commit();
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    protected <R> R executeQuery(Function<Session, R> query) {
        try (Session session = getSessionFactory().openSession()) {
            return query.apply(session);
        }
    }

    // --- Métodos CRUD ---

    public void crear(T entity) {
        executeInTransaction(session -> session.persist(entity));
    }

    public void borrar(Long id) {
        executeInTransaction(session -> {
            T entity = session.get(entityClass, id);
            if (entity != null) {
                session.remove(entity);
            }
        });
    }

    public void actualizar(T entity) {
        executeInTransaction(session -> session.merge(entity));
    }

    public T buscarPorId(Long id) {
        return executeQuery(session -> session.get(entityClass, id));
    }

    public static void cerrarFactory() {
        if (factory != null && !factory.isClosed()) {
            factory.close();
        }
    }

    public List<T> listar() {
        return listar(true, -1, -1);
    }

    public List<T> listar(int maxResults, int firstResult) {
        return listar(false, maxResults, firstResult);
    }

    private List<T> listar(boolean all, int maxResults, int firstResult) {
        return executeQuery(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            cq.select(root);

            org.hibernate.query.Query<T> query = session.createQuery(cq);

            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }

            return query.getResultList();
        });
    }

    public List<T> buscarPorCampo(String fieldName, Object value) {
        return executeQuery(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);

            String[] campos = fieldName.split("\\.");
            jakarta.persistence.criteria.Path<?> path = root;
            for (String campo : campos) {
                path = path.get(campo);
            }

            cq.select(root).where(cb.equal(path, value));

            return session.createQuery(cq).getResultList();
        });
    }
}