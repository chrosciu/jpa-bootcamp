package com.chrosciu.example;

import com.chrosciu.domain.Area;
import com.chrosciu.domain.Company;
import com.chrosciu.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EagerProperty {
    static Area areaProxy;

    public static void main(String[] args) {
        Area area = Area.builder()
                .name("Polska")
                .build();
        Company company = Company.builder()
                .name("Mireks")
                .area(area)
                .build();
        Company otherCompany = Company.builder()
                .name("Cebulpol")
                .area(area)
                .build();
        DbUtils.runInPersistence(entityManagerFactory -> {
            DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
                entityManager.persist(otherCompany);
            });
            DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
                var persistedCompany = entityManager.find(Company.class, company.getId());
                log.info("After company fetch");
                var persistedArea = persistedCompany.getArea();
                areaProxy = persistedArea;
                log.info("After area fetch");
            });
            DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
                log.info("Area proxy: {}", areaProxy);
            });
        });
    }
}
