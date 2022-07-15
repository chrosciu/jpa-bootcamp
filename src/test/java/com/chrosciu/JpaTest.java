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
import org.junit.jupiter.api.BeforeEach;

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

}
