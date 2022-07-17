package com.chrosciu.app;

import com.chrosciu.domain.Area;
import com.chrosciu.domain.Company;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CascadeRemove {

    public static void main(String[] args) {
        Area area = Area.builder()
            .name("Polska")
            .build();
        Company company = Company.builder()
            .name("Januszex")
            .area(area)
            .build();
        Utils.runInPersistence(entityManagerFactory -> {
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
            });
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var persistedArea = entityManager.find(Area.class, area.getId());
                log.info("{}", persistedArea.getCompanies());
            });
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var persistedArea = entityManager.find(Area.class, area.getId());
                entityManager.remove(persistedArea);
            });
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var persistedArea = entityManager.find(Area.class, area.getId());
                log.info("area: {}", persistedArea);
            });
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var persistedCompany = entityManager.find(Company.class, company.getId());
                log.info("company: {}", persistedCompany);
            });
        });
    }
}
