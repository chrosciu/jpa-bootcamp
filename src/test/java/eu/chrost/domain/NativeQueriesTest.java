package eu.chrost.domain;

import eu.chrost.dto.CompanyNameAndType;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static eu.chrost.domain.CompanyType.BUDZETOWKA;
import static eu.chrost.domain.CompanyType.JANUSZEX;
import static eu.chrost.domain.CompanyType.KORPO;
import static eu.chrost.util.Utils.runInTransaction;

@Slf4j
class NativeQueriesTest {
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("workshop");
    }

    @AfterEach
    void cleanUp() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    @Test
    void nativeQueries() {
        Area poland = Area.builder()
                .name("Polska")
                .build();

        Area global = Area.builder()
                .name("Global")
                .build();

        Area america = Area.builder()
                .name("America")
                .build();

        Company januszpol = Company.builder()
                .name("Januszpol")
                .companyType(JANUSZEX)
                .size(5)
                .area(poland)
                .build();

        Company mireks = Company.builder()
                .name("Mireks")
                .companyType(JANUSZEX)
                .size(3)
                .build();

        Company fiskus = Company.builder()
                .name("Urzad Skarbowy")
                .companyType(BUDZETOWKA)
                .size(100)
                .area(poland)
                .build();

        Company google = Company.builder()
                .name("Google")
                .companyType(KORPO)
                .size(10000)
                .area(global)
                .build();

        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(januszpol);
            entityManager.persist(mireks);
            entityManager.persist(fiskus);
            entityManager.persist(google);
            entityManager.persist(america);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "select * from companies";
            var result = entityManager.createNativeQuery(query, Company.class)
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "select name, companyType from companies";
            List<CompanyNameAndType> result = entityManager.createNativeQuery(query, Company.COMPANY_NAME_AND_TYPE)
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "select name from companies";
            List<String> result = entityManager.createNativeQuery(query)
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "select * from companies where name = :name";
            List<String> result = entityManager.createNativeQuery(query, Company.class)
                    .setParameter("name", "JANUSZPOL")
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            List<Company> result = entityManager.createNamedQuery(Company.FIND_BY_NAME, Company.class)
                    .setParameter("name", "JANUSZPOL")
                    .getResultList();
            log.info("{}", result);
        });

    }
}
