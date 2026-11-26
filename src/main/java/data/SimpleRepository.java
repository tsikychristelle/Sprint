package data;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class SimpleRepository<T, ID> implements CrudRepository<T, ID> {
    private final EntityManagerFactory emf;
    private final Class<T> entityClass;

    public SimpleRepository(EntityManagerFactory emf, Class<T> entityClass) {
        this.emf = emf;
        this.entityClass = entityClass;
    }

    private <R> R inTransaction(Function<EntityManager, R> work) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            R result = work.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public T save(T entity) {
        return inTransaction(em -> em.merge(entity));
    }

    @Override
    public Optional<T> findById(ID id) {
        try (EntityManager em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(entityClass, id));
        }
    }

    @Override
    public List<T> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("select e from " + entityClass.getSimpleName() + " e", entityClass)
                     .getResultList();
        }
    }

    @Override
    public void deleteById(ID id) {
        inTransaction(em -> {
            T ref = em.find(entityClass, id);
            if (ref != null) em.remove(ref);
            return null;
        });
    }
}