package com.chrosciu.app;

import com.chrosciu.domain.Company;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Merge {

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
                //entityManager.persist(company); -> exception !

                company.setName("Cebulpol");

//                var persistedCompany = entityManager.find(Company.class, company.getId());
//                persistedCompany.setName(company.getName());

                log.info("Before merge");
                var persistedCompany = entityManager.merge(company);
                log.info("{}", persistedCompany == company);
                log.info("After merge");

                company.setName("Mirex");
                var persistedCompany2 = entityManager.merge(company);
                log.info("{}", persistedCompany == persistedCompany2);
            });
        } finally {
            if (entityManagerFactory != null) {
                entityManagerFactory.close();
            }
        }
    }

}
