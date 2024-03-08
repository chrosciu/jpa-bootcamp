package eu.chrost.domain;

import eu.chrost.dto.AreaAndCount;
import eu.chrost.dto.CompanyNameAndType;
import eu.chrost.dto.CompanyTypeAndCount;
import eu.chrost.dto.NameAndCount;
import eu.chrost.dto.Names;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static eu.chrost.domain.CompanyType.BUDZETOWKA;
import static eu.chrost.domain.CompanyType.JANUSZEX;
import static eu.chrost.domain.CompanyType.KORPO;
import static eu.chrost.util.Utils.runInTransaction;

@Slf4j
class JpqlQueriesTest {
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("workshop");
    }

    @AfterEach
    void cleanUp() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    @Test
    void jpqlQueries() {
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

        Company mireks = Company.builder()
                .name("Mireks")
                .companyType(JANUSZEX)
                .size(3)
                .build();

        Company fiskus = Company.builder()
                .name("Urzad Skarbowy")
                .companyType(BUDZETOWKA)
                .size(100)
                .area(poland)
                .build();

        Company google = Company.builder()
                .name("Google")
                .companyType(KORPO)
                .size(10000)
                .area(global)
                .build();

        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.persist(januszpol);
            entityManager.persist(mireks);
            entityManager.persist(fiskus);
            entityManager.persist(google);
            entityManager.persist(america);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var allCompanies = entityManager
                    .createQuery("from Company", Company.class)
                    .getResultList();
            log.info("{}", allCompanies);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var allCompaniesNames = entityManager
                    .createQuery("select name from Company", String.class)
                    .getResultList();
            log.info("{}", allCompaniesNames);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "select new eu.chrost.dto.CompanyNameAndType(name, companyType) from Company";
            var allCompaniesNamesAndTypes = entityManager
                    .createQuery(query, CompanyNameAndType.class)
                    .getResultList();
            log.info("{}", allCompaniesNamesAndTypes);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "select count(c) from Company c";
            var companiesCount = entityManager
                    .createQuery(query, Long.class)
                    .getSingleResult();
            log.info("{}", companiesCount);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "select distinct(companyType) from Company";
            var distinctCompanyTypes = entityManager
                    .createQuery(query, CompanyType.class)
                    .getResultList();
            log.info("{}", distinctCompanyTypes);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "select count(distinct(companyType)) from Company";
            var distinctCompanyTypesCount = entityManager
                    .createQuery(query, Long.class)
                    .getSingleResult();
            log.info("{}", distinctCompanyTypesCount);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "select new eu.chrost.dto.CompanyTypeAndCount(c.companyType, count(c)) from Company c group by c.companyType";
            var result = entityManager
                    .createQuery(query, CompanyTypeAndCount.class)
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "from Company where name='JANUSZPOL'";
            var distinctCompanyTypesCount = entityManager
                    .createQuery(query, Company.class)
                    .getResultList();
            log.info("{}", distinctCompanyTypesCount);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var expectedName = "JANUSZPOL";
            var query = "from Company where name='" + expectedName + "'"; //BAD !!!
            var distinctCompanyTypesCount = entityManager
                    .createQuery(query, Company.class)
                    .getResultList();
            log.info("{}", distinctCompanyTypesCount);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var expectedName = "JANUSZPOL";
            var query = "from Company where name=:name";
            var distinctCompanyTypesCount = entityManager
                    .createQuery(query, Company.class)
                    .setParameter("name", expectedName)
                    .getResultList();
            log.info("{}", distinctCompanyTypesCount);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var expectedName = "JANUSZPOL";
            var query = "from Company where name=?1";
            var distinctCompanyTypesCount = entityManager
                    .createQuery(query, Company.class)
                    .setParameter(1, expectedName)
                    .getResultList();
            log.info("{}", distinctCompanyTypesCount);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "from Company where companyType = eu.chrost.domain.CompanyType.JANUSZEX";
            var distinctCompanyTypesCount = entityManager
                    .createQuery(query, Company.class)
                    .getResultList();
            log.info("{}", distinctCompanyTypesCount);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "from Company where companyType in ("
                    + "eu.chrost.domain.CompanyType.JANUSZEX, eu.chrost.domain.CompanyType.KORPO)";
            var result = entityManager
                    .createQuery(query, Company.class)
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "from Company where companyType in ?1";
            var result = entityManager
                    .createQuery(query, Company.class)
                    .setParameter(1, EnumSet.of(CompanyType.JANUSZEX, CompanyType.KORPO))
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "from Company where name like '%A%'";
            var result = entityManager
                    .createQuery(query, Company.class)
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "from Company where name like 'J_NUSZPOL'";
            var result = entityManager
                    .createQuery(query, Company.class)
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "select new eu.chrost.dto.CompanyTypeAndCount(c.companyType, count(c)) from Company c group by c.companyType";
            var result = entityManager
                    .createQuery(query, CompanyTypeAndCount.class)
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = entityManager.createNamedQuery(Company.FIND_BY_TYPE, Company.class);
            var result = query
                    .setParameter("companyType", CompanyType.JANUSZEX)
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "update Company c set c.size = c.size * 2 where c.companyType = eu.chrost.domain.CompanyType.JANUSZEX";
            var result = entityManager.createQuery(query).executeUpdate();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "from Company";
            var result = entityManager.createQuery(query, Company.class)
                    .setMaxResults(2)
                    .setFirstResult(2)
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "from Company c where c.name='JANUSZPOL'";
            var result = entityManager.createQuery(query, Company.class)
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "from Company c where c.area.name = 'Global'";
            var result = entityManager.createQuery(query, Company.class)
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "select new eu.chrost.dto.NameAndCount(c.area.name, count(c)) from Company c group by c.area.name";
            var result = entityManager.createQuery(query, NameAndCount.class)
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "select new eu.chrost.dto.AreaAndCount(c.area, count(c)) from Company c group by c.area";
            var result = entityManager.createQuery(query, AreaAndCount.class)
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "select c.name from Area a join a.companies c";
            var result = entityManager.createQuery(query, String.class)
                    .getResultList();
            log.info("{}", result);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var query = "select new eu.chrost.dto.Names(c.name, c.area.name) from Company c left join c.area";
            var result = entityManager.createQuery(query, Names.class)
                    .getResultList();
            log.info("{}", result);
        });

    }
}
