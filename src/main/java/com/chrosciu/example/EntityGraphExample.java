package com.chrosciu.example;

import com.chrosciu.domain.Area;
import com.chrosciu.domain.Company;
import com.chrosciu.util.DbUtils;
import com.chrosciu.util.JpaProperties;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityGraph;
import java.util.Map;
import java.util.Set;

@Slf4j
public class EntityGraphExample {
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
                EntityGraph entityGraph = entityManager.createEntityGraph(Area.WITH_COMPANIES);
                Map<String, Object> queryProperties = Map.of(
                        JpaProperties.LOAD_GRAPH,
                        entityGraph
                );
                Area persistedArea = entityManager.find(Area.class, area.getId(), queryProperties);
                companiesProxy = persistedArea.getCompanies();
            });
            DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
                for (Company c : companiesProxy) {
                    log.info("{}", c);
                }
            });
        });

    }
}
