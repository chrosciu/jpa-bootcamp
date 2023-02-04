package com.chrosciu.example;

import com.chrosciu.domain.Company;
import com.chrosciu.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpdatePersisted {
    public static void main(String[] args) {
        Company company = Company.builder()
                .name("Januszex")
                .build();

        DbUtils.runInPersistence(entityManagerFactory -> {
            DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
                company.setName("Cebulpol");
            });
            DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
                var persistedCompany = entityManager.find(Company.class, company.getId());
                persistedCompany.setName("Mirex");
            });
        });
    }
}
