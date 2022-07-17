package com.chrosciu.app;

import com.chrosciu.domain.Area;
import com.chrosciu.domain.Company;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EagerProperty {

    private static Area areaProxy;

    public static void main(String[] args) {
        Area area = Area.builder()
            .name("Polska")
            .build();
        Area otherArea = Area.builder()
            .name("Slask")
            .build();
        Company company = Company.builder()
            .name("Januszex")
            .area(area)
            .build();
        Company otherCompany = Company.builder()
            .name("Cebulpol")
            .area(otherArea)
            .build();
        Utils.runInPersistence(entityManagerFactory -> {
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
                entityManager.persist(otherCompany);
            });
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                log.info("Before fetch");
                var persistedCompany = entityManager.find(Company.class, company.getId());
                log.info("After fetch");
                log.info("Before fetch area");
                var persistedArea = persistedCompany.getArea();
                areaProxy = persistedArea;
                log.info("After fetch area");
            });
            log.info("dummy");
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                log.info("Before fetch");
//                var persistedCompanies = entityManager.createQuery("from Company c join fetch c.area", Company.class).getResultList();
//                log.info("After fetch");
            });
        });
    }
}
