package com.chrosciu.app;

import com.chrosciu.domain.Area;
import com.chrosciu.domain.Company;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CascadePersist {

    public static void main(String[] args) {
        Area area = Area.builder()
            .name("Polska")
            .build();
        Company company = Company.builder()
            .name("Januszex")
            //.area(area)
            .build();
        company.assignArea(area);
        Utils.runInPersistence(entityManagerFactory -> {
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                //entityManager.persist(area);
                entityManager.persist(company);
                //entityManager.persist(area);
            });
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var persistedArea = entityManager.find(Area.class, area.getId());
                log.info("{}", persistedArea.getCompanies());
            });
        });
    }
}
