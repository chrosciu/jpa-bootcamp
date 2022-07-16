package com.chrosciu;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.chrosciu.domain.Employee;
import com.chrosciu.domain.Team;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LazyInitializationException;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Slf4j
class JpaTest {

    private EntityManagerFactory entityManagerFactory;
    private Statistics statistics;
    private Employee employee;
    private Employee otherEmployee;
    private Team team;
    private List<Employee> employeesProxy;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("bootcamp");
        statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        employee = employee();
        otherEmployee = otherEmployee();
        team = team();
    }

    @AfterEach
    void cleanUp() {
        log.info("{}", statistics);
        if (statistics != null) {
            statistics.clear();
            statistics = null;
        }
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
        employee = null;
        team = null;
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

    private Employee employee() {
        return Employee.builder()
            .firstName("Janusz")
            .lastName("Bukowy")
            .build();
    }

    private Employee otherEmployee() {
        return Employee.builder()
            .firstName("Mirek")
            .lastName("Jaworek")
            .build();
    }

    private Team team() {
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
    
    private void createTeamWithEmployees() {
        runInTransaction(entityManager -> {
            employee.setTeam(team);
            otherEmployee.setTeam(team);
            entityManager.persist(employee);
            entityManager.persist(otherEmployee);
        });
    }

    @Test
    void when_persisting_entity_cascaded_entities_are_also_persisted() {
        createTeamWithEmployees();
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            assertEquals(team.getName(), persistedTeam.getName());
            assertEquals(2, persistedTeam.getEmployees().size());
            assertEquals(employee.getFirstName(), persistedTeam.getEmployees().get(0).getFirstName());
        });
    }
    @Test
    void when_removing_entity_cascaded_entities_are_also_removed() {
        createTeamWithEmployees();
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
        createTeamWithEmployees();
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
    @Disabled("need to drop all cascades in Employee class on team field")
    void given_entities_with_relations_when_persist_some_entities_then_throws_exception() {
        employee.setTeam(team);
        assertThrows(RollbackException.class, () -> runInTransaction(entityManager -> entityManager.persist(employee)));
    }

    @Test
    void given_entities_with_relations_when_find_then_return_root_entity_without_dependent_ones() {
        createTeamWithEmployees();
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            assertEquals(1, statistics.getEntityLoadCount());
        });
    }

    @Test
    void given_entities_with_relations_when_find_then_lazy_loads_dependent_ones() {
        createTeamWithEmployees();
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            var employees = persistedTeam.getEmployees();
            assertEquals(1, statistics.getEntityLoadCount());
            employees.forEach(e -> log.info("{}", e));
            assertEquals(3, statistics.getEntityLoadCount());
        });
    }

    @Test
    void given_entities_with_relations_when_access_dependent_fields_and_transaction_is_closed_then_throws_an_exception() {
        createTeamWithEmployees();
        runInTransaction(entityManager -> employeesProxy = entityManager.find(Team.class, team.getId()).getEmployees());
        assertThrows(LazyInitializationException.class, () -> employeesProxy.forEach(ep -> log.info("{}", ep)));
    }

    @Test
    void given_entities_with_relations_when_join_fetch_dependent_entities_they_are_loaded() {
        createTeamWithEmployees();
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager
                .createQuery("from Team as t join fetch t.employees e where t.id = :id", Team.class)
                .setParameter("id", team.getId())
                .getSingleResult();
            assertEquals(3, statistics.getEntityLoadCount());
        });
    }

    @Test
    void given_entities_with_relations_when_find_with_entity_graph_then_returns_post_with_tags() {
        createTeamWithEmployees();
        runInTransaction(entityManager -> {
//            var entityGraph = entityManager.createEntityGraph(Team.class);
//            entityGraph.addAttributeNodes("employees");
             var entityGraph = entityManager.createEntityGraph(Team.WITH_EMPLOYEES);
            // All attributes specified in entity graph will be treated as Eager, and all attribute not specified will be treated as Lazy
            // Map<String, Object> properties = Map.of("jakarta.persistence.fetchgraph", entityGraph);
            // All attributes specified in entity graph will be treated as Eager, and all attribute not specified use their default/mapped value
            Map<String, Object> properties = Map.of("jakarta.persistence.loadgraph", entityGraph);
            var persistedTeam = entityManager.find(Team.class, team.getId(), properties);
            assertEquals(3, statistics.getEntityLoadCount());
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

    @Test
    @Disabled("@Version field in Team must be removed")
    void given_no_locks_last_committed_transaction_wins() throws InterruptedException {
        runInTransaction(entityManager -> entityManager.persist(team));
        execute(List.of(
            new UpdateTeamNameTask(team.getId(), "Wajchowi", LockModeType.NONE, 1, 5, entityManagerFactory),
            new UpdateTeamNameTask(team.getId(), "Magicy", LockModeType.NONE, 2, 3, entityManagerFactory)
        ));
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            assertEquals("Wajchowi", persistedTeam.getName());
        });
    }

    @Test
    void given_versioned_entity_when_first_transaction_tires_to_override_changes_from_second_transaction_then_first_transaction_is_rolled_back() throws InterruptedException {
        runInTransaction(entityManager -> entityManager.persist(team));
        execute(List.of(
            new UpdateTeamNameTask(team.getId(), "Wajchowi", LockModeType.NONE, 1, 5, entityManagerFactory),
            new UpdateTeamNameTask(team.getId(), "Magicy", LockModeType.NONE, 2, 3, entityManagerFactory)
        ));
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            assertEquals("Magicy", persistedTeam.getName());
        });
    }

    @Test
    void given_two_transactions_when_first_transactions_acquired_the_lock_then_second_transaction_waits_for_first_transaction_to_release_the_lock() throws InterruptedException {
        runInTransaction(entityManager -> entityManager.persist(team));
        execute(List.of(
            new UpdateTeamNameTask(team.getId(), "Wajchowi", LockModeType.PESSIMISTIC_WRITE, 1, 5, entityManagerFactory),
            new UpdateTeamNameTask(team.getId(), "Magicy", LockModeType.PESSIMISTIC_WRITE, 2, 3, entityManagerFactory)
        ));
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            assertEquals("Magicy", persistedTeam.getName());
        });
    }

    static void execute(List<Task> tasks) throws InterruptedException {
        var countDownLatch = new CountDownLatch(tasks.size());
        var executor = Executors.newFixedThreadPool(tasks.size());
        tasks.forEach(task -> {
            task.setCountDownLatch(countDownLatch);
            executor.submit(task);
        });
        countDownLatch.await();
    }

    public interface Task extends Runnable {
        void setCountDownLatch(CountDownLatch countDownLatch);
    }



}
