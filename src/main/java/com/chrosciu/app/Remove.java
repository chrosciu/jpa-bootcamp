package com.chrosciu.app;

import com.chrosciu.domain.Company;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Remove {

    public static void main(String[] args) {
        Company company = Company.builder()
            .name("Januszex")
            .build();
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("bootcamp");
        try {
            Utils.runInEmf(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
            });
            Utils.runInEmf(entityManagerFactory, entityManager -> {
                //entityManager.remove(company);
                var persistedCompany = entityManager.find(Company.class, company.getId());
                entityManager.remove(persistedCompany);
            });
            Utils.runInEmf(entityManagerFactory, entityManager -> {
//                company.setId(null); NO WAY !
//                entityManager.persist(company);
            });
        } finally {
            if (entityManagerFactory != null) {
                entityManagerFactory.close();
            }
        }
    }
}
