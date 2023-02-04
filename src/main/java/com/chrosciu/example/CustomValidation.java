package com.chrosciu.example;

import com.chrosciu.domain.Company;
import com.chrosciu.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomValidation {
    public static void main(String[] args) {
        Company company = Company.builder()
                .name("Januszex")
                //.name("Mirex")
                .build();

        DbUtils.runInPersistence(entityManagerFactory -> {
            DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
                log.info("Before persist");
                entityManager.persist(company);
                log.info("After persist");
            });
        });
    }
}
