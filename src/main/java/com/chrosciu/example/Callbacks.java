package com.chrosciu.example;

import com.chrosciu.domain.Area;
import com.chrosciu.domain.Company;
import com.chrosciu.util.DbUtils;

public class Callbacks {
    public static void main(String[] args) {
        Area area = Area.builder()
                .name("Poland")
                .build();
        Company company = Company.builder()
                .area(area)
                .name("Mirex")
                .build();

        DbUtils.runInPersistence(entityManagerFactory -> {
            DbUtils.runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(company);
            });
        });
    }
}
