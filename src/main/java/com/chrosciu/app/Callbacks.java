package com.chrosciu.app;

import static com.chrosciu.app.Utils.runInPersistence;
import static com.chrosciu.app.Utils.runInTransaction;

import com.chrosciu.domain.Company;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Callbacks {

    public static void main(String[] args) {
        Company company = Company.builder()
            .name("Januszex")
            .build();
        runInPersistence(entityManagerFactory -> {
            runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
            });
            runInTransaction(entityManagerFactory, entityManager -> {
                var persistedCompany = entityManager.find(Company.class, company.getId());
            });
        });
    }


}
