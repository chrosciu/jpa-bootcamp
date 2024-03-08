package eu.chrost.domain;

import eu.chrost.util.Utils;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CompanyJpaTest {
    private EntityManagerFactory entityManagerFactory;
    //private Statistics statistics;
    private Company company;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("workshop");
        //statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();

        company = company();
    }

    @AfterEach
    void cleanUp() {
//        if (statistics != null) {
//            log.info("{}", statistics);
//            statistics.clear();
//            statistics = null;
//        }
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    private Company company() {
        return Company.builder()
                .name("Januszex sp. z o.o.")
                .build();
    }

    @Test
    void givenAnEmptyDatabase_whenLoadAllEntities_thenEmptyCollectionIsReturned() {
        Utils.runInTransaction(entityManagerFactory, entityManager -> {
            var companies = entityManager.createQuery("from Company", Company.class).getResultList();
            assertThat(companies).isEmpty();
        });
    }

    @Test
    void givenAnEntityObject_whenPersist_thenEntityStateIsSavedIntoDatabase() {
        Utils.runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
        });
        Utils.runInTransaction(entityManagerFactory, entityManager -> {
            var persistedCompany = entityManager.find(Company.class, company.getId());
            assertThat(persistedCompany.getName()).isEqualTo(company.getName());
        });
    }
}
