package com.chrosciu.example;

import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Slf4j
class DbConnection {
    public DbConnection() {
        log.info("DbConnection.new");
    }

    public void open() {
        log.info("DbConnection.open");
    }

    public void close() {
        log.info("DbConnection.close");
    }
}

@Slf4j
public class DbConnectionTest {

    private DbConnection dbConnection;

    @BeforeEach
    public void setUp() {
        dbConnection = new DbConnection();
        dbConnection.open();
    }

    @AfterEach
    public void cleanUp() {
        dbConnection.close();
    }

    @Test
    public void testOne() {
        log.info("testOne");
    }

    @Test
    public void testTwo() {
        log.info("testTwo");
    }

    @Test
    @Disabled("Fails by default")
    public void testThree() {
        log.info("testThree");
        assertTrue(false);
    }

    @Test
    @Disabled("Crashes by default")
    public void testFour() {
        log.info("testFour");
        throw new RuntimeException("Bleh!");
    }

}
