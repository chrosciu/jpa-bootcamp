package com.chrosciu.example;

import com.chrosciu.domain.Company;
import com.chrosciu.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolationException;

@Slf4j
public class AutomaticEntityValidation {
    public static void main(String[] args) {
        Company company = Company.builder()
                .name("   ")
                .build();

        DbUtils.runInPersistence(entityManagerFactory -> {
            DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
                log.info("Before persist");
                entityManager.persist(company);
                log.info("After persist");
                try {
                    entityManager.flush();
                } catch (ConstraintViolationException e) {
                    e.printStackTrace();
                    throw e;
                }
            });
        });
    }
}
