package com.chrosciu.app;

import static com.chrosciu.domain.CompanyType.JANUSZEX;

import com.chrosciu.domain.Area;
import com.chrosciu.domain.Company;
import com.chrosciu.domain.CompanyType;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NativeQueries {

    public static void main(String[] args) {
        Utils.runInPersistence(entityManagerFactory -> {
            prepareData(entityManagerFactory);

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "select * from company";
//                var result = entityManager.createNativeQuery(query, Company.class)
//                    .getResultList();
//                log.info("{}", result);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "select name, companyType from company";
//                List<CompanyNameAndType> result = entityManager.createNativeQuery(query, Company.COMPANY_NAME_AND_TYPE)
//                    .getResultList();
//                log.info("{}", result);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "select name from company";
//                List<String> result = entityManager.createNativeQuery(query)
//                    .getResultList();
//                log.info("{}", result);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "select * from company where name = :name";
//                List<String> result = entityManager.createNativeQuery(query, Company.class)
//                    .setParameter("name", "JANUSZPOL")
//                    .getResultList();
//                log.info("{}", result);
//            });

            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                List<Company> result = entityManager.createNamedQuery(Company.FIND_BY_NAME, Company.class)
                    .setParameter("name", "JANUSZPOL")
                    .getResultList();
                log.info("{}", result);
            });
        });
    }

    private static void prepareData(EntityManagerFactory entityManagerFactory) {
        Area poland = Area.builder()
            .name("Polska")
            .build();

        Area global = Area.builder()
            .name("Global")
            .build();

        Area america = Area.builder()
            .name("America")
            .build();

        Company januszpol = Company.builder()
            .name("Januszpol")
            .companyType(JANUSZEX)
            .size(5)
            .area(poland)
            .build();

        Company mirex = Company.builder()
            .name("Mirex")
            .companyType(JANUSZEX)
            .size(3)
            .build();

        Company fiskus = Company.builder()
            .name("Urzad Skarbowy")
            .companyType(CompanyType.BUDZETOWKA)
            .size(100)
            .area(poland)
            .build();

        Company google = Company.builder()
            .name("Google")
            .companyType(CompanyType.KORPO)
            .size(10000)
            .area(global)
            .build();

        Utils.runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(januszpol);
            entityManager.persist(mirex);
            entityManager.persist(fiskus);
            entityManager.persist(google);
            entityManager.persist(america);
        });
    }

}
