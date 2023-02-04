package com.chrosciu.example;

import com.chrosciu.domain.Company;
import com.chrosciu.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Persist {
    public static void main(String[] args) {
        Company company = Company.builder()
                .name("Januszex")
                .build();

        DbUtils.runInPersistence(entityManagerFactory -> {
            DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
                log.info("Before persist");
                entityManager.persist(company);
                log.info("After persist");
            });
        });

//        EntityManagerFactory entityManagerFactory = null;
//        EntityManager entityManager = null;
//        EntityTransaction transaction = null;
//        try {
//            entityManagerFactory = Persistence.createEntityManagerFactory("bootcamp");
//            entityManager = entityManagerFactory.createEntityManager();
//            transaction = entityManager.getTransaction();
//            transaction.begin();
//            log.info("Before persist");
//            entityManager.persist(company);
//            log.info("After persist");
//            //entityManager.flush();
//            //log.info("After flush");
//            transaction.commit();
//            log.info("After commit");
//        } finally {
//            if (transaction != null && transaction.isActive()) {
//                transaction.rollback();
//            }
//            if (entityManager != null) {
//                entityManager.close();
//            }
//            if (entityManagerFactory != null) {
//                entityManagerFactory.close();
//            }
//        }


    }
}
