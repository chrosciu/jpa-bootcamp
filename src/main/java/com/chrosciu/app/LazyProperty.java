package com.chrosciu.app;

import com.chrosciu.domain.Area;
import com.chrosciu.domain.Company;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LazyProperty {

    static List<Company> companiesProxy = null;

    public static void main(String[] args) {
        Area area = Area.builder()
            .name("Polska")
            .build();
        Company company = Company.builder()
            .name("Januszex")
            .area(area)
            .build();
        Company otherCompany = Company.builder()
            .name("Januszex")
            .area(area)
            .build();
        Utils.runInPersistence(entityManagerFactory -> {
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
                entityManager.persist(otherCompany);
            });
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var persistedArea = entityManager.find(Area.class, area.getId());
                var companies = persistedArea.getCompanies();
                companiesProxy = companies;
                log.info("Before real fetch");
//                var c = companies.size();
//                for (Company c: companies) {
//                    log.info("{}", c);
//                }
                log.info("After real fetch");
            });
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                for (Company c: companiesProxy) {
                    log.info("{}", c);
                }
            });

        });
    }
}
