package eu.chrost.domain;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LazyInitializationException;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static eu.chrost.util.Utils.runAsync;
import static eu.chrost.util.Utils.runInTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class CompanyJpaTest {
    private EntityManagerFactory entityManagerFactory;
    private Statistics statistics;
    private Company company;
    private Company otherCompany;
    private Area area;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("workshop");
        statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();

        company = company();
        otherCompany = otherCompany();
        area = area();
    }

    @AfterEach
    void cleanUp() {
        if (statistics != null) {
            log.info("{}", statistics);
            statistics.clear();
            statistics = null;
        }
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

    private Company otherCompany() {
        return Company.builder()
                .name("Cebulpol S.A.")
                .build();
    }

    private Area area() {
        return Area.builder()
                .name("Poland")
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

    @Test
    void givenAnEntityWithBrokenCustomConstraints_whenFlush_thenConstraintValidationExceptionIsThrown() {
        company.setName("Mirex");
        runInTransaction(entityManagerFactory, entityManager -> {
            assertThatThrownBy(() -> {
                entityManager.persist(company);
                entityManager.flush();
            }).isInstanceOf(ConstraintViolationException.class);
        });
    }

    @Test
    void when_persisting_entity_cascaded_entities_are_also_persisted() {
        company.setArea(area);
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var persistedArea = entityManager.find(Area.class, area.getId());
            assertThat(persistedArea.getName()).isEqualTo(area.getName());
            assertThat(persistedArea.getCompanies()).extracting(Company::getName).containsExactly(company.getName());
        });
    }

    @Test
    void when_removing_entity_cascaded_entities_are_also_removed() {
        company.setArea(area);
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var persistedArea = entityManager.find(Area.class, area.getId());
            entityManager.remove(persistedArea);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var persistedCompany = entityManager.find(Company.class, company.getId());
            assertThat(persistedCompany).isNull();
        });
    }

    @Test
    void when_entity_is_orphaned_it_is_removed_with_orphan_removal_flag() {
        company.setArea(area);
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var persistedArea = entityManager.find(Area.class, area.getId());
            persistedArea.getCompanies().clear();
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var persistedCompany = entityManager.find(Company.class, company.getId());
            assertThat(persistedCompany).isNull();
        });
    }

    @Test
    void given_entities_with_relations_when_find_then_return_root_entity_without_dependent_ones() {
        company.setArea(area);
        otherCompany.setArea(area);
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
            entityManager.persist(otherCompany);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var persistedArea = entityManager.find(Area.class, area.getId());
            assertThat(statistics.getEntityLoadCount()).isEqualTo(1);
        });
    }

    @Test
    void given_entities_with_relations_when_find_then_lazy_loads_dependent_ones() {
        company.setArea(area);
        otherCompany.setArea(area);
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
            entityManager.persist(otherCompany);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var persistedArea = entityManager.find(Area.class, area.getId());
            var companies = persistedArea.getCompanies();
            assertThat(statistics.getEntityLoadCount()).isEqualTo(1);
            companies.forEach(c -> log.info("{}", c));
            assertThat(statistics.getEntityLoadCount()).isEqualTo(3);
        });
    }

    @Test
    void given_entities_with_relations_when_access_dependent_fields_and_transaction_is_closed_then_throws_an_exception() {
        company.setArea(area);
        otherCompany.setArea(area);
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
            entityManager.persist(otherCompany);
        });
        AtomicReference<List<Company>> companiesProxy = new AtomicReference<>();
        runInTransaction(entityManagerFactory, entityManager -> {
            companiesProxy.set(entityManager.find(Area.class, area.getId()).getCompanies());
        });
        assertThatThrownBy(() -> companiesProxy.get().forEach(ep -> log.info("{}", ep)))
                .isInstanceOf(LazyInitializationException.class);
    }

    @Test
    void given_entities_with_relations_when_join_fetch_dependent_entities_they_are_loaded() {
        company.setArea(area);
        otherCompany.setArea(area);
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
            entityManager.persist(otherCompany);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var persistedArea = entityManager
                    .createQuery("from Area as a join fetch a.companies c where a.id = :id", Area.class)
                    .setParameter("id", area.getId())
                    .getSingleResult();
            assertThat(statistics.getEntityLoadCount()).isEqualTo(3);
        });
    }

    @Test
    void given_entities_with_relations_when_find_with_entity_graph_then_returns_post_with_tags() {
        company.setArea(area);
        otherCompany.setArea(area);
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
            entityManager.persist(otherCompany);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var entityGraph = entityManager.createEntityGraph(Area.class);
            entityGraph.addAttributeNodes(Area_.companies);
            // All attributes specified in entity graph will be treated as Eager, and all attribute not specified will be treated as Lazy
            // Map<String, Object> properties = Map.of("jakarta.persistence.fetchgraph", entityGraph);
            // All attributes specified in entity graph will be treated as Eager, and all attribute not specified use their default/mapped value
            Map<String, Object> properties = Map.of("jakarta.persistence.loadgraph", entityGraph);
            var persistedArea = entityManager.find(Area.class, area.getId(), properties);
            assertThat(statistics.getEntityLoadCount()).isEqualTo(3);
        });
    }

    @Test
    void given_entities_with_relations_when_find_with_named_entity_graph_then_returns_post_with_tags() {
        company.setArea(area);
        otherCompany.setArea(area);
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(company);
            entityManager.persist(otherCompany);
        });
        runInTransaction(entityManagerFactory, entityManager -> {
            var entityGraph = entityManager.createEntityGraph(Area.WITH_COMPANIES);
            Map<String, Object> properties = Map.of("jakarta.persistence.loadgraph", entityGraph);
            var persistedArea = entityManager.find(Area.class, area.getId(), properties);
            assertThat(statistics.getEntityLoadCount()).isEqualTo(3);
        });
    }
}
