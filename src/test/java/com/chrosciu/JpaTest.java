package com.chrosciu;

import com.chrosciu.domain.Employee;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class JpaTest {

    private EntityManagerFactory entityManagerFactory;
    private Statistics statistics;
    private Employee employee;
    private Employee otherEmployee;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("bootcamp");
        statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        employee = employee();
        otherEmployee = otherEmployee();
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

    @Test
    void shouldBeNoEmployeesInDb() {
        runInTransaction(entityManager -> {
            var employees = entityManager.createQuery("from Employee", Employee.class).getResultList();
            Assertions.assertEquals(0, employees.size());
        });
    }

}
