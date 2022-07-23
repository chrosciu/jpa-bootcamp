package com.chrosciu;

import static javax.persistence.LockModeType.NONE;
import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;
import static org.assertj.core.api.Assertions.assertThat;

import com.chrosciu.domain.Employee;
import com.chrosciu.domain.EmployeeType;
import com.chrosciu.domain.Employee_;
import com.chrosciu.domain.Team;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Slf4j
class JpaTest {

    private EntityManagerFactory entityManagerFactory;
    private Statistics statistics;
    private Employee employee;
    private Employee otherEmployee;
    private Employee aloneEmployee;
    private Team team;
    private Team otherTeam;
    private Team aloneTeam;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("bootcamp");
        statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        employee = employee();
        otherEmployee = otherEmployee();
        aloneEmployee = aloneEmployee();
        team = team();
        otherTeam = otherTeam();
        aloneTeam = aloneTeam();

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
        employee = null;
        otherEmployee = null;
        aloneEmployee = null;
        team = null;
        otherTeam = null;
        aloneTeam = null;
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
            .employeeType(EmployeeType.ONSITE)
            .age(50)
            .build();
    }

    private Employee otherEmployee() {
        return Employee.builder()
            .firstName("Mirek")
            .lastName("Jaworek")
            .employeeType(EmployeeType.REMOTE)
            .age(45)
            .build();
    }

    private Employee aloneEmployee() {
        return Employee.builder()
            .firstName("Boleslaw")
            .lastName("Wstydliwy")
            .employeeType(EmployeeType.REMOTE)
            .age(40)
            .build();
    }

    private Team team() {
        return Team.builder()
            .name("Druciarze")
            .build();
    }

    private Team otherTeam() {
        return Team.builder()
            .name("Spawacze")
            .build();
    }

    private Team aloneTeam() {
        return Team.builder()
            .name("Zwyciezcy")
            .build();
    }

    @Test
    void shouldBeNoEmployeesInDb() {
        runInTransaction(entityManager -> {
            var employees = entityManager.createQuery("from Employee", Employee.class).getResultList();
            Assertions.assertEquals(0, employees.size());
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
    void teamShouldBeSavedWithEmployees() {
        Assertions.assertDoesNotThrow(() -> createTeamWithEmployees());
    }

    @SneakyThrows
    static void executeInParallel(List<Runnable> tasks) {
        var count = tasks.size();
        var latch = new CountDownLatch(count);
        var executor = Executors.newFixedThreadPool(count);
        tasks.forEach(task -> {
            executor.submit(() -> {
                try {
                    task.run();
                } finally {
                    latch.countDown();
                }
            });
        });
        latch.await();
    }

    @Test
    void shouldRunTasksInParallel() {
        Runnable task1 = new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                log.info("[1] started");
                Thread.sleep(1000);
                log.info("[1] after 1 sleep");
                Thread.sleep(5000);
                log.info("[1] finished");
            }
        };
        Runnable task2 = new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                log.info("[2] started");
                Thread.sleep(2000);
                log.info("[2] after 1 sleep");
                Thread.sleep(3000);
                log.info("[2] finished");
            }
        };
        log.info("before execute");
        executeInParallel(List.of(task1, task2));
        log.info("after execute");
    }

    @RequiredArgsConstructor
    class UpdateTeamNameTask implements Runnable {
        private final long teamId;
        private final String newName;
        private final long sleepBeforeLoad;
        private final long sleepAfterLoad;
        private final LockModeType lockModeType;

        private void log(String message) {
            log.info("[{}] " + message, newName);
        }

        private void logException(String message, Throwable t) {
            log.warn("[{}] " + message, newName, t);
        }

        @Override
        public void run() {
            EntityManager entityManager = null;
            EntityTransaction transaction = null;
            try {
                entityManager = entityManagerFactory.createEntityManager();
                transaction = entityManager.getTransaction();

                log("started");

                transaction.begin();

                Thread.sleep(1000 * sleepBeforeLoad);

                log("before load");
                var team = entityManager.find(Team.class, teamId, lockModeType);
                log("after load");

                Thread.sleep(1000 * sleepAfterLoad);

                team.setName(newName);

                log("before commit");
                transaction.commit();
                log("after commit");

            } catch (Throwable t) {
                logException("Exception in task: ", t);
            } finally {
                if (transaction != null && transaction.isActive()) {
                    log("before rollback");
                    transaction.rollback();
                    log("after rollback");
                }
                if (entityManager != null) {
                    entityManager.close();
                }
            }
        }
    }

    @Test
    void teamNameShouldBeChangedIfSingleTaskIsExecuted() {
        runInTransaction(entityManager -> entityManager.persist(team));
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            assertThat(persistedTeam.getName()).isEqualTo("Druciarze");
        });
        executeInParallel(List.of(new UpdateTeamNameTask(team.getId(), "Wajchowi", 1, 2, NONE)));
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            assertThat(persistedTeam.getName()).isEqualTo("Wajchowi");
        });
    }

    @Test
    @Disabled("Does not work with @Version field in Team - see test below")
    void teamNameIsChangedToLastCommittedOneInCaseOfParallelChanges() {
        runInTransaction(entityManager -> entityManager.persist(team));
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            assertThat(persistedTeam.getName()).isEqualTo("Druciarze");
        });
        executeInParallel(List.of(
                new UpdateTeamNameTask(team.getId(), "Wajchowi", 1, 5, NONE),
                new UpdateTeamNameTask(team.getId(), "Magicy", 2, 3, NONE)
            )
        );
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            assertThat(persistedTeam.getName()).isEqualTo("Wajchowi");
        });
    }

    @Test
    void teamNameIsChangedToFirstCommittedWithOptimisticLock() {
        runInTransaction(entityManager -> entityManager.persist(team));
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            assertThat(persistedTeam.getName()).isEqualTo("Druciarze");
        });
        executeInParallel(List.of(
                new UpdateTeamNameTask(team.getId(), "Wajchowi", 1, 5, NONE),
                new UpdateTeamNameTask(team.getId(), "Magicy", 2, 3, NONE)
            )
        );
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            assertThat(persistedTeam.getName()).isEqualTo("Magicy");
        });
    }

    @Test
    void teamNameIsChangedToXWithPessimisticLock() {
        runInTransaction(entityManager -> entityManager.persist(team));
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            assertThat(persistedTeam.getName()).isEqualTo("Druciarze");
        });
        executeInParallel(List.of(
                new UpdateTeamNameTask(team.getId(), "Wajchowi", 1, 5, PESSIMISTIC_WRITE),
                new UpdateTeamNameTask(team.getId(), "Magicy", 2, 3, PESSIMISTIC_WRITE)
            )
        );
        runInTransaction(entityManager -> {
            var persistedTeam = entityManager.find(Team.class, team.getId());
            assertThat(persistedTeam.getName()).isEqualTo("Magicy");
        });
    }

    private void createMultipleTeamsWithEmployees() {
        runInTransaction(entityManager -> {
            employee.setTeam(team);
            otherEmployee.setTeam(otherTeam);
            entityManager.persist(employee);
            entityManager.persist(otherEmployee);
            entityManager.persist(aloneEmployee);
            entityManager.persist(aloneTeam);
        });
    }

    @Test
    void multipleTeamsShouldBeSavedWithEmployees() {
        Assertions.assertDoesNotThrow(() -> createMultipleTeamsWithEmployees());
    }

    @Value
    class EmployeeQuery {
        EmployeeType employeeType;
        Integer minAge;
        Integer maxAge;
        List<String> firstNames;
    }

    private static List<Employee> findEmployeesWithCriteria(EntityManager entityManager, EmployeeQuery employeeQuery) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var criteriaQuery = criteriaBuilder.createQuery(Employee.class);
        var root = criteriaQuery.from(Employee.class);
        criteriaQuery.select(root);

        var predicates = new ArrayList<Predicate>();

        if (employeeQuery.getEmployeeType() != null) {
            var namePredicate = criteriaBuilder.equal(root.get(Employee_.employeeType), employeeQuery.employeeType);
            predicates.add(namePredicate);
        }

        if (employeeQuery.getMinAge() != null) {
            var namePredicate = criteriaBuilder.greaterThan(root.get(Employee_.age), employeeQuery.getMinAge());
            predicates.add(namePredicate);
        }

        if (employeeQuery.getMaxAge() != null) {
            var namePredicate = criteriaBuilder.lessThan(root.get(Employee_.age), employeeQuery.getMaxAge());
            predicates.add(namePredicate);
        }

        if (employeeQuery.getFirstNames() != null) {
            var firstNamePredicates = new ArrayList<Predicate>();
            for (var firstName: employeeQuery.getFirstNames()) {
                firstNamePredicates.add(criteriaBuilder.equal(root.get(Employee_.firstName), firstName));
            }
            var firstNamePredicatesArray = firstNamePredicates.toArray(new Predicate[0]);
            var firstNamePredicate = criteriaBuilder.or(firstNamePredicatesArray);
            predicates.add(firstNamePredicate);
        }

        var predicatesArray = predicates.toArray(new Predicate[0]);
        criteriaQuery.where(predicatesArray);

        var typedQuery = entityManager.createQuery(criteriaQuery);
        var result = typedQuery.getResultList();
        return result;
    }

}
