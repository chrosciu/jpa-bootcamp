package eu.chrost.domain;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static eu.chrost.util.Utils.runAsync;
import static eu.chrost.util.Utils.runInTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        runInTransaction(entityManagerFactory, entityManager -> {
            var companies = entityManager.createQuery("from Company", Company.class).getResultList();
            assertThat(companies).isEmpty();
        });
    }

    @Test
    void givenAnEntityObject_whenPersist_thenEntityStateIsSavedIntoDatabase() {
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var persistedCompany = entityManager.find(Company.class, company.getId());
            assertThat(persistedCompany.getName()).isEqualTo(company.getName());
        });
    }

    @Test
    void givenAnManagedEntity_whenEntityStateIsChanged_thenEntityStateIsAutomaticallySynchronizedWithDatabase() {
        var newName = "Cebulpol S.A.";
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
            company.setName(newName);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var persistedCompany = entityManager.find(Company.class, company.getId());
            assertThat(persistedCompany.getName()).isEqualTo(newName);
        });
    }

    @Test
    void givenADetachedEntity_whenMerge_thenEntityStateIsAutomaticallySynchronizedWithDatabase() {
        var newName = "Cebulpol S.A.";
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            company.setName(newName);
            var managedCompany = entityManager.merge(company);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var persistedCompany = entityManager.find(Company.class, company.getId());
            assertThat(persistedCompany.getName()).isEqualTo(newName);
        });
    }

    @Test
    void givenAManagedEntity_whenRemove_thenEntityStateIsRemovedFromDatabase() {
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var persistedCompany = entityManager.find(Company.class, company.getId());
            entityManager.remove(persistedCompany);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var persistedCompany = entityManager.find(Company.class, company.getId());
            assertThat(persistedCompany).isNull();
        });
    }

    @Test
    void givenADetachedEntity_whenRemove_thenExceptionIsThrown() {
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
            entityManager.detach(company);
            assertThatThrownBy(() -> entityManager.remove(company)).isInstanceOf(IllegalArgumentException.class);
        });
    }

    @Test
    void givenAnManagedEntity_whenRefresh_thenEntityStateIsSynchronizedWithDatabaseState() {
        var newName = "Cebulpol S.A.";
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var persistedCompany = entityManager.find(Company.class, company.getId());
            runAsync(() -> runInTransaction(entityManagerFactory, em -> {
                var pc = em.find(Company.class, company.getId());
                pc.setName(newName);
            }));
            persistedCompany.setName("Mirex");
            entityManager.refresh(persistedCompany);
            assertThat(persistedCompany.getName()).isEqualTo(newName);
        });
    }

    @Test
    void givenAnEntityWithBrokenConstraints_whenFlush_thenConstraintValidationExceptionIsThrown() {
        company.setName("JaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaanuszPol");
        runInTransaction(entityManagerFactory, entityManager -> {
            assertThatThrownBy(() -> {
                entityManager.persist(company);
                entityManager.flush();
            }).isInstanceOf(ConstraintViolationException.class);
        });
    }

    @Test
    void givenAnEntityWithBrokenConstraints_whenTriggerValidationManually_thenConstraintViolationsAreDetected() {
        company.setName("JaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaanuszPol");
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            var validator = validatorFactory.getValidator();
            var constraintViolations = validator.validate(company);
            assertThat(constraintViolations).isNotEmpty();
        }
    }

    @Test
    void givenAnValidEntity_whenTriggerValidationManually_thenConstraintViolationsAreEmpty() {
        company.setName("Cebulpol");
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            var validator = validatorFactory.getValidator();
            var constraintViolations = validator.validate(company);
            assertThat(constraintViolations).isEmpty();
        }
    }
}
