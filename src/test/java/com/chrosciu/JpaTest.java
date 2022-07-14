package com.chrosciu;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.chrosciu.domain.Employee;
import com.chrosciu.domain.Team;
import java.util.Set;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class JpaTest {

    private EntityManagerFactory entityManagerFactory;
    private Employee employee;
    private Team team;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("bootcamp");
        employee = testEmployee();
        team = testTeam();
    }

    @AfterEach
    void cleanUp() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
        employee = null;
    }

    private void runInTransaction(Consumer<EntityManager> action) {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            action.accept(entityManager);
            transaction.commit();
        } catch (Throwable t) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw t;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private Employee testEmployee() {
        return Employee.builder()
            .firstName("Janusz")
            .lastName("Bukowy")
            .build();
    }

    private Team testTeam() {
        return Team.builder()
            .name("Druciarze")
            .build();
    }

    @Test
    void given_an_entity_object_when_persist_then_entity_state_is_saved_into_database() {
        runInTransaction(entityManager -> {
            entityManager.persist(employee);
        });
        runInTransaction(entityManager -> {
            var persistedEmployee = entityManager.find(Employee.class, employee.getId());
            assertEquals(employee.getFirstName(), persistedEmployee.getFirstName());
            assertEquals(employee.getLastName(), persistedEmployee.getLastName());
        });
    }

    @Test
    void given_an_attached_entity_when_entity_state_is_changed_then_entity_state_is_automatically_synchronized_with_the_database() {
        var newFirstName = "Stefan";
        runInTransaction(entityManager -> {
            entityManager.persist(employee);
            // entityManager.flush();
            employee.setFirstName(newFirstName);
        });
        runInTransaction(entityManager -> {
            var persistedEmployee = entityManager.find(Employee.class, employee.getId());
            assertEquals(newFirstName, persistedEmployee.getFirstName());
        });
    }

    @Test
    void given_a_detached_entity_when_merge_then_entity_state_is_automatically_synchronized_with_the_database_and_managed_entity_is_returned() {
        var newFirstName = "Stefan";
        runInTransaction(entityManager -> {
            entityManager.persist(employee);
        });
        runInTransaction(entityManager -> {
            employee.setFirstName(newFirstName);
            var managedEmployee = entityManager.merge(employee);
        });
        runInTransaction(entityManager -> {
            var persistedEmployee = entityManager.find(Employee.class, employee.getId());
            assertEquals(newFirstName, persistedEmployee.getFirstName());
        });
    }

    @Test
    void given_an_attached_entity_when_remove_then_entity_state_is_removed_from_database() {
        runInTransaction(entityManager -> entityManager.persist(employee));
        runInTransaction(entityManager -> {
            var persistedEmployee = entityManager.find(Employee.class, employee.getId());
            entityManager.remove(persistedEmployee);
        });
        runInTransaction(entityManager -> assertNull(entityManager.find(Employee.class, employee.getId())));
    }

    @Test
    void given_a_detached_entity_when_remove_then_throws_an_exception() {
        runInTransaction(entityManager -> {
            entityManager.persist(employee);
            entityManager.detach(employee);
            //entityManager.clear();
            assertThrows(IllegalArgumentException.class, () -> entityManager.remove(employee));
        });
    }

    @Test
    void given_an_entity_when_refresh_then_entity_state_is_synchronized_with_database_state() {
        var newFirstName = "Stefan";
        runInTransaction(entityManager -> entityManager.persist(employee));
        runInTransaction(entityManager -> {
            var persistedEmployee = entityManager.find(Employee.class, employee.getId());
            runAsync(() -> runInTransaction(em -> {
                var pe = em.find(Employee.class, employee.getId());
                pe.setFirstName(newFirstName);
            }));
            persistedEmployee.setFirstName("Test name");
            entityManager.refresh(persistedEmployee);
            assertEquals(newFirstName, persistedEmployee.getFirstName());
        });
    }

    @SneakyThrows
    private void runAsync(Runnable runnable) {
        var thread = new Thread(runnable);
        thread.start();
        thread.join();
    }

    @Test
    void broken_constraints_will_throw_an_exception_during_save() {
        employee.setLastName("A veeeeeeeeeeeeeeeeeeeeeeeeeryyyyyyyy looooooooong naaaaaameeeeeeee!");
        runInTransaction(entityManager -> {
            assertThrows(ConstraintViolationException.class, () -> {
                entityManager.persist(employee);
                entityManager.flush();
            });
        });
    }

    @Test
    @Disabled("need to set NONE as validation.mode in persistence.xml")
    void constraint_are_not_checked_if_validation_is_turned_off() {
        employee.setLastName("A veeeeeeeeeeeeeeeeeeeeeeeeeryyyyyyyy looooooooong naaaaaameeeeeeee!");
        runInTransaction(entityManager -> {
            assertDoesNotThrow(() -> {
                entityManager.persist(employee);
                entityManager.flush();
            });
        });
    }

    @Test
    void programmatic_validation_will_detect_all_constraint_violations() {
        employee.setLastName("A veeeeeeeeeeeeeeeeeeeeeeeeeryyyyyyyy looooooooong naaaaaameeeeeeee!");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Employee>> constraintViolations = validator.validate(employee);
        assertEquals(1, constraintViolations.size());
        assertEquals("size must be between 0 and 20", constraintViolations.iterator().next().getMessage());
    }

    @Test
    void broken_custom_constraints_will_throw_an_exception_during_save() {
        employee.setFirstName("Jan");
        employee.setLastName("Kowalski");
        runInTransaction(entityManager -> {
            assertThrows(ConstraintViolationException.class, () -> {
                entityManager.persist(employee);
                entityManager.flush();
            });
        });
    }

    @Test
    void when_persisting_entity_cascaded_entities_are_also_persisted() {
        employee.setTeam(team);
        runInTransaction(entityManager -> {
            entityManager.persist(employee);
        });
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            assertEquals(team.getName(), persistedTeam.getName());
            assertEquals(1, persistedTeam.getEmployees().size());
            assertEquals(employee.getFirstName(), persistedTeam.getEmployees().get(0).getFirstName());
        });
    }
    @Test
    void when_removing_entity_cascaded_entities_are_also_removed() {
        employee.setTeam(team);
        runInTransaction(entityManager -> {
            entityManager.persist(employee);
        });
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            entityManager.remove(persistedTeam);
        });
        runInTransaction(entityManager -> {
            var persistedEmployee = entityManager.find(Employee.class, employee.getId());
            assertNull(persistedEmployee);
        });
    }

    @Test
    void when_entity_is_orphaned_it_is_removed_with_orphan_removal_flag() {
        employee.setTeam(team);
        runInTransaction(entityManager -> {
            entityManager.persist(employee);
        });
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            persistedTeam.getEmployees().clear();
        });
        runInTransaction(entityManager -> {
            var persistedEmployee = entityManager.find(Employee.class, employee.getId());
            assertNull(persistedEmployee);
        });
    }

    @Test
    void pre_persist_callback_can_change_entity_fields_to_be_saved() {
        team.setName("Wajchowi");
        runInTransaction(entityManager -> {
            entityManager.persist(team);
        });
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            assertEquals("Magicy", persistedTeam.getName());
        });
    }

    @Test
    void post_persist_callback_can_rollback_transaction() {
        team.setName("Czarodzieje");
        var rollbackException = assertThrows(RollbackException.class, () -> {
            runInTransaction(entityManager ->  entityManager.persist(team));
        });
        assertEquals("Takich tu nie chcemy!", rollbackException.getCause().getMessage());
    }

}
