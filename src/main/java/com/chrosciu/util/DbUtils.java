package com.chrosciu.util;

import lombok.experimental.UtilityClass;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.function.Consumer;

@UtilityClass
public final class DbUtils {
    public static final String PERSISTENCE_UNIT = "bootcamp";

    public static void runInPersistence(Consumer<EntityManagerFactory> action) {
        EntityManagerFactory entityManagerFactory = null;
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
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
