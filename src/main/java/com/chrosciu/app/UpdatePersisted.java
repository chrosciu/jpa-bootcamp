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
            Utils.runInEmf(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
                company.setName("Cebula Ltd.");
                entityManager.flush();
                company.setName("Mirekpol");
            });
            Utils.runInEmf(entityManagerFactory, entityManager -> {
                company.setName("Marcin Sp z.o.o");
            });
        } finally {
            if (entityManagerFactory != null) {
                entityManagerFactory.close();
            }
        }
    }

}
