package eu.chrost.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@UtilityClass
@Slf4j
public class Utils {
    public static void runInTransaction(EntityManagerFactory entityManagerFactory, Consumer<EntityManager> action) {
        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            action.accept(entityManager);
            transaction.commit();
        } catch (Throwable t) {
            if (transaction != null && transaction.isActive()) {
                transaction.setRollbackOnly();
            }
            throw t;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @SneakyThrows
    public static void runAsync(Runnable runnable) {
        Thread.ofVirtual().start(runnable).join();
    }
}
