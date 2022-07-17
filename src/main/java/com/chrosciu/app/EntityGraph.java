package com.chrosciu.app;

import com.chrosciu.domain.Area;
import com.chrosciu.domain.Company;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

@Slf4j
public class EntityGraph {
    private static Statistics statistics;

    public static void main(String[] args) {
        Area area = Area.builder()
            .name("Polska")
            .build();
        Company company = Company.builder()
            .name("Januszex")
            .area(area)
            .build();
        Company otherCompany = Company.builder()
            .name("Cebulpol")
            .area(area)
            .build();
        Utils.runInPersistence(entityManagerFactory -> {
            var sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
            statistics = sessionFactory.getStatistics();
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
                entityManager.persist(otherCompany);
            });
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var entityGraph = entityManager.createEntityGraph(Area.class);
//                entityGraph.addAttributeNodes("companies");
                var entityGraph = entityManager.createEntityGraph(Area.WITH_COMPANIES);
                // All attributes specified in entity graph will be treated as Eager, and all attribute not specified will be treated as Lazy
                // Map<String, Object> properties = Map.of("jakarta.persistence.fetchgraph", entityGraph);
                // All attributes specified in entity graph will be treated as Eager, and all attribute not specified use their default/mapped value
                //Map<String, Object> properties = Map.of("jakarta.persistence.loadgraph", entityGraph);
                Map<String, Object> queryProperties = Map.of(
                    "jakarta.persistence.loadgraph",
                    entityGraph
                );
                log.info("Number of loads: {}", statistics.getEntityLoadCount());
                var persistedArea = entityManager.find(Area.class, area.getId(), queryProperties);
                log.info("Number of loads: {}", statistics.getEntityLoadCount());
            });
        });

    }
}
