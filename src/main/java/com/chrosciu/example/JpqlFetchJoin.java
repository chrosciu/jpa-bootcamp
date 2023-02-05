package com.chrosciu.example;

import com.chrosciu.domain.Area;
import com.chrosciu.domain.Company;
import com.chrosciu.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class JpqlFetchJoin {
    static Set<Company> companiesProxy;

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
                var queryString = "select a from Area a join fetch a.companies c where a.id = :id";
                var persistedArea = entityManager.createQuery(queryString, Area.class)
                        .setParameter("id", area.getId())
                        .getSingleResult();
                var companies = persistedArea.getCompanies();
                //var size = companies.size();
                companiesProxy = companies;
            });
            DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
                var size = companiesProxy.size();
                log.info("Companies count: {}", size);
                for (Company c : companiesProxy) {
                    log.info("{}", c);
                }
            });
        });
    }
}
