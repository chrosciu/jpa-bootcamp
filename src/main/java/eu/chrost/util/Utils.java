package eu.chrost.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.experimental.UtilityClass;

import java.util.function.Consumer;

@UtilityClass
public class Utils {
    public static void runInTransaction(EntityManagerFactory entityManagerFactory, Consumer<EntityManager> action) {
        EntityTransaction transaction = null;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            transaction = entityManager.getTransaction();
            transaction.begin();
            action.accept(entityManager);
            transaction.commit();
        } catch (Throwable t) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw t;
        }
    }
}
