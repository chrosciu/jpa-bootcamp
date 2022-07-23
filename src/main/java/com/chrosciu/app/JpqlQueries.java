package com.chrosciu.app;

import static com.chrosciu.domain.CompanyType.JANUSZEX;

import com.chrosciu.domain.Area;
import com.chrosciu.domain.Company;
import com.chrosciu.domain.CompanyType;
import com.chrosciu.dto.Names;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JpqlQueries {

    public static void main(String[] args) {
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

        Utils.runInPersistence(entityManagerFactory -> {
            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                entityManager.persist(januszpol);
                entityManager.persist(mirex);
                entityManager.persist(fiskus);
                entityManager.persist(google);
                entityManager.persist(america);
            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var allCompanies = entityManager
//                    .createQuery("from Company", Company.class)
//                    .getResultList();
//                log.info("{}", allCompanies);
//            });
//
//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var allCompaniesNames = entityManager
//                    .createQuery("select name from Company", String.class)
//                    .getResultList();
//                log.info("{}", allCompaniesNames);
//            });
//
//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "select new com.chrosciu.dto.CompanyNameAndType(name, companyType) from Company";
//                var allCompaniesNamesAndTypes = entityManager
//                    .createQuery(query, CompanyNameAndType.class)
//                    .getResultList();
//                log.info("{}", allCompaniesNamesAndTypes);
//            });
//
//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "select count(c) from Company c";
//                var companiesCount = entityManager
//                    .createQuery(query, Long.class)
//                    .getSingleResult();
//                log.info("{}", companiesCount);
//            });
//
//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "select distinct(companyType) from Company";
//                var distinctCompanyTypes = entityManager
//                    .createQuery(query, CompanyType.class)
//                    .getResultList();
//                log.info("{}", distinctCompanyTypes);
//            });
//
//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "select count(distinct(companyType)) from Company";
//                var distinctCompanyTypesCount = entityManager
//                    .createQuery(query, Long.class)
//                    .getSingleResult();
//                log.info("{}", distinctCompanyTypesCount);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "from Company where name='JANUSZPOL'";
//                var distinctCompanyTypesCount = entityManager
//                    .createQuery(query, Company.class)
//                    .getResultList();
//                log.info("{}", distinctCompanyTypesCount);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var expectedName = "JANUSZPOL";
//                var query = "from Company where name='" + expectedName + "'"; BAD !!!
//                var distinctCompanyTypesCount = entityManager
//                    .createQuery(query, Company.class)
//                    .getResultList();
//                log.info("{}", distinctCompanyTypesCount);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var expectedName = "JANUSZPOL";
//                var query = "from Company where name=:name";
//                var distinctCompanyTypesCount = entityManager
//                    .createQuery(query, Company.class)
//                    .setParameter("name", expectedName)
//                    .getResultList();
//                log.info("{}", distinctCompanyTypesCount);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var expectedName = "JANUSZPOL";
//                var query = "from Company where name=?1";
//                var distinctCompanyTypesCount = entityManager
//                    .createQuery(query, Company.class)
//                    .setParameter(1, expectedName)
//                    .getResultList();
//                log.info("{}", distinctCompanyTypesCount);
//            });

//             Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "from Company where companyType = com.chrosciu.domain.CompanyType.JANUSZEX";
//                var distinctCompanyTypesCount = entityManager
//                    .createQuery(query, Company.class)
//                    .getResultList();
//                log.info("{}", distinctCompanyTypesCount);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "from Company where companyType in ("
//                    + "com.chrosciu.domain.CompanyType.JANUSZEX, com.chrosciu.domain.CompanyType.KORPO)";
//                var result = entityManager
//                    .createQuery(query, Company.class)
//                    .getResultList();
//                log.info("{}", result);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "from Company where companyType in ?1";
//                var result = entityManager
//                    .createQuery(query, Company.class)
//                    .setParameter(1, EnumSet.of(CompanyType.JANUSZEX, CompanyType.KORPO))
//                    .getResultList();
//                log.info("{}", result);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "from Company where name like '%A%'";
//                var result = entityManager
//                    .createQuery(query, Company.class)
//                    .getResultList();
//                log.info("{}", result);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "from Company where name like 'J_NUSZPOL'";
//                var result = entityManager
//                    .createQuery(query, Company.class)
//                    .getResultList();
//                log.info("{}", result);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "select new com.chrosciu.dto.CompanyTypeAndCount(c.companyType, count(c)) from Company c group by c.companyType";
//                var result = entityManager
//                    .createQuery(query, CompanyTypeAndCount.class)
//                    .getResultList();
//                log.info("{}", result);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = entityManager.createNamedQuery(Company.FIND_BY_TYPE, Company.class);
//                var result = query
//                    .setParameter("companyType", CompanyType.JANUSZEX)
//                    .getResultList();
//                log.info("{}", result);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "update Company c set c.size = c.size * 2 where c.companyType = com.chrosciu.domain.CompanyType.JANUSZEX";
//                var result = entityManager.createQuery(query).executeUpdate();
//                log.info("{}", result);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "from Company";
//                var result = entityManager.createQuery(query, Company.class)
//                    .setMaxResults(2)
//                    .setFirstResult(2)
//                    .getResultList();
//                log.info("{}", result);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "from Company c where c.name='JANUSZPOL'";
//                var result = entityManager.createQuery(query, Company.class)
//                    .getResultList();
//                log.info("{}", result);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "from Company c where c.area.name = 'Global'";
//                var result = entityManager.createQuery(query, Company.class)
//                    .getResultList();
//                log.info("{}", result);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "select new com.chrosciu.dto.NameAndCount(c.area.name, count(c)) from Company c group by c.area.name";
//                var result = entityManager.createQuery(query, NameAndCount.class)
//                    .getResultList();
//                log.info("{}", result);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "select new com.chrosciu.dto.AreaAndCount(c.area, count(c)) from Company c group by c.area";
//                var result = entityManager.createQuery(query, AreaAndCount.class)
//                    .getResultList();
//                log.info("{}", result);
//            });

//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var query = "select c.name from Area a join a.companies c";
//                var result = entityManager.createQuery(query, String.class)
//                    .getResultList();
//                log.info("{}", result);
//            });

            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var query = "select new com.chrosciu.dto.Names(c.name, c.area.name) from Company c left join c.area";
                var result = entityManager.createQuery(query, Names.class)
                    .getResultList();
                log.info("{}", result);
            });







        });
    }
}
