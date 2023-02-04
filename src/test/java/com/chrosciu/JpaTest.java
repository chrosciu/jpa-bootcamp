package com.chrosciu;

import com.chrosciu.domain.Employee;
import com.chrosciu.util.DbUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.assertj.core.api.Assertions.assertThat;

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
        DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
            var employees = entityManager.createQuery("select e from Employee e", Employee.class).getResultList();
            assertThat(employees).isEmpty();

        });
    }

}
