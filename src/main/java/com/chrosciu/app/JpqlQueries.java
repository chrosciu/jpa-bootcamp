package com.chrosciu.app;

import com.chrosciu.domain.Company;
import com.chrosciu.domain.CompanyType;
import com.chrosciu.dto.CompanyNameAndType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JpqlQueries {

    public static void main(String[] args) {
        Company januszpol = Company.builder()
            .name("Januszpol")
            .companyType(CompanyType.JANUSZEX)
            .size(5)
            .build();

        Company fiskus = Company.builder()
            .name("Urzad Skarbowy")
            .companyType(CompanyType.BUDZETOWKA)
            .size(100)
            .build();

        Company google = Company.builder()
            .name("Google")
            .companyType(CompanyType.KORPO)
            .size(10000)
            .build();

        Utils.runInPersistence(entityManagerFactory -> {
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(januszpol);
                entityManager.persist(fiskus);
                entityManager.persist(google);
            });

            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var allCompanies = entityManager
                    .createQuery("from Company", Company.class)
                    .getResultList();
                log.info("{}", allCompanies);
            });

            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var allCompaniesNames = entityManager
                    .createQuery("select name from Company", String.class)
                    .getResultList();
                log.info("{}", allCompaniesNames);
            });

            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var query = "select new com.chrosciu.dto.CompanyNameAndType(name, companyType) from Company";
                var allCompaniesNamesAndTypes = entityManager
                    .createQuery(query, CompanyNameAndType.class)
                    .getResultList();
                log.info("{}", allCompaniesNamesAndTypes);
            });

            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var query = "select count(c) from Company c";
                var companiesCount = entityManager
                    .createQuery(query, Long.class)
                    .getSingleResult();
                log.info("{}", companiesCount);
            });

            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var query = "select distinct(companyType) from Company";
                var distinctCompanyTypes = entityManager
                    .createQuery(query, CompanyType.class)
                    .getResultList();
                log.info("{}", distinctCompanyTypes);
            });

            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var query = "select count(distinct(companyType)) from Company";
                var distinctCompanyTypesCount = entityManager
                    .createQuery(query, Long.class)
                    .getSingleResult();
                log.info("{}", distinctCompanyTypesCount);
            });







        });
    }
}
