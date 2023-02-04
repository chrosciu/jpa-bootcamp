package com.chrosciu.example;

import com.chrosciu.domain.Company;
import com.chrosciu.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Refresh {
    public static void main(String[] args) {
        Company company = Company.builder()
                .name("Januszex")
                .build();

        DbUtils.runInPersistence(entityManagerFactory -> {
            DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
            });
            DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
                var persistedCompany = entityManager.find(Company.class, company.getId());

                DbUtils.runInTransaction(entityManagerFactory, em -> {
                    var persistedCompany2 = em.find(Company.class, company.getId());
                    log.info("persistedCompany == persistedCompany2: {}", persistedCompany == persistedCompany2);
                    persistedCompany2.setName("Mirex");
                });

                persistedCompany.setName("Cebulpol");
                entityManager.flush(); // if called - will overwite company name
//                if (1 == 1) {
//                    throw new RuntimeException("Boom");
//                }
                log.info("persistedCompany.getName(): {}", persistedCompany.getName());
                entityManager.refresh(persistedCompany);
                log.info("persistedCompany.getName(): {}", persistedCompany.getName());
            });
        });
    }
}
