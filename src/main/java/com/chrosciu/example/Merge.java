package com.chrosciu.example;

import com.chrosciu.domain.Company;
import com.chrosciu.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Merge {
    public static void main(String[] args) {
        Company company = Company.builder()
                .name("Januszex")
                .build();

        DbUtils.runInPersistence(entityManagerFactory -> {
            DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
            });
            DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
                //entityManager.persist(company);

                log.info("Before merge");

                var persistedCompany = entityManager.merge(company);
                log.info("persistedCompany == company: {}", persistedCompany == company);

                log.info("After merge");

                var persistedCompany2 = entityManager.merge(company);
                log.info("persistedCompany2 == persistedCompany: {}", persistedCompany2 == persistedCompany);

                log.info("After second merge");

                persistedCompany.setName("Mirex");

                var persistedCompany3 = entityManager.find(Company.class, company.getId());
                log.info("persistedCompany3 == persistedCompany: {}", persistedCompany3 == persistedCompany);

                log.info("After find");
            });
        });
    }
}
