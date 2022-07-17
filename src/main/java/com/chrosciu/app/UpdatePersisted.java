package com.chrosciu.app;

import com.chrosciu.domain.Company;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class UpdatePersisted {

    public static void main(String[] args) {
        Company company = Company.builder()
            .name("Januszex")
            .build();
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("bootcamp");
        try {
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
                company.setName("Cebula Ltd.");
                entityManager.flush();
                company.setName("Mirekpol");
            });
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                company.setName("Marcin Sp z.o.o");
            });
        } finally {
            if (entityManagerFactory != null) {
                entityManagerFactory.close();
            }
        }
    }

}
