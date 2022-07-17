package com.chrosciu.app;

import com.chrosciu.domain.Area;
import com.chrosciu.domain.Company;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

@Slf4j
public class JpqlFetchJoin {
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
                //var queryString = "from Area where id = " + area.getId(); --> VERY BAD !!!
                var queryString = "from Area a join fetch a.companies where a.id = :id";
                log.info("Number of loads: {}", statistics.getEntityLoadCount());
                var persistedArea = entityManager
                    .createQuery(queryString, Area.class)
                    .setParameter("id", area.getId())
                    .getSingleResult();
                log.info("Number of loads: {}", statistics.getEntityLoadCount());
                var persistedCompanies = persistedArea.getCompanies();
                log.info("Number of loads: {}", statistics.getEntityLoadCount());
                var size = persistedCompanies.size();
                log.info("Number of loads: {}", statistics.getEntityLoadCount());
            });
        });

    }
}
