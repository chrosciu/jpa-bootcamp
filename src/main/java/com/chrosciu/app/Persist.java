package com.chrosciu.app;

import com.chrosciu.domain.Company;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Persist {

    public static void main(String[] args) {
        Company company = Company.builder()
            .name("Januszex")
            .build();
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("bootcamp");
        try {
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
            });
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var persistedCompany = entityManager.find(Company.class, company.getId());
                log.info("{}", persistedCompany);
            });

//            EntityManager entityManager = entityManagerFactory.createEntityManager();
//            EntityTransaction transaction = entityManager.getTransaction();
//            try {
//                transaction.begin();
//                log.info("Before persist");
//                entityManager.persist(company);
//                log.info("After persist");
//                transaction.commit();
//                log.info("After commit");
//            } finally {
//                if (entityManager != null) {
//                    entityManager.close();
//                    entityManager = null;
//                }
//            }
//            entityManager = entityManagerFactory.createEntityManager();
//            try {
//                var persistedCompany = entityManager.find(Company.class, company.getId());
//                log.info("{}", persistedCompany);
//            } finally {
//                if (entityManager != null) {
//                    entityManager.close();
//                }
//            }
        } finally {
            if (entityManagerFactory != null) {
                entityManagerFactory.close();
            }
        }
    }
}
