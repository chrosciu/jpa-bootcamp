package com.chrosciu.app;

import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Utils {

    public static void runMultipleTransactions(Iterable<Consumer<EntityManager>> actions) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("bootcamp");
        try {
            for (var action: actions) {
                runInTransaction(entityManagerFactory, action);
            }
        } finally {
            if (entityManagerFactory != null) {
                entityManagerFactory.close();
            }
        }
    }

    public static void runInPersistence(Consumer<EntityManagerFactory> action) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("bootcamp");
        try {
            action.accept(entityManagerFactory);
        } finally {
            if (entityManagerFactory != null) {
                entityManagerFactory.close();
            }
        }
    }

    public static void runInTransaction(EntityManagerFactory emf, Consumer<EntityManager> action) {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        try {
            entityManager = emf.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            action.accept(entityManager);

            transaction.commit();
        } catch (Throwable t) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw t;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

}
