package com.chrosciu.app;

import com.chrosciu.domain.Company;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class AutomaticValidation {

    public static void main(String[] args) {
        Company company = Company.builder()
            .name("   ")
            .build();
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("bootcamp");
        try {
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
                entityManager.flush();
            });
        } finally {
            if (entityManagerFactory != null) {
                entityManagerFactory.close();
            }
        }
    }
}
