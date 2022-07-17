package com.chrosciu.app;

import com.chrosciu.domain.Company;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Refresh {

    public static void main(String[] args) {
        Company company = Company.builder()
            .name("Januszex")
            .build();
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("bootcamp");
        try {
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
            });
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var persistedCompany = entityManager.find(Company.class, company.getId());

                Utils.runInTransaction(entityManagerFactory, em -> {
                    var pc = em.find(Company.class, company.getId());
                    pc.setName("Mirex");
                });

                persistedCompany.setName("Cebulpol");
                entityManager.refresh(persistedCompany);
                log.info("{}", persistedCompany.getName());

            });
        } finally {
            if (entityManagerFactory != null) {
                entityManagerFactory.close();
            }
        }
    }
}
