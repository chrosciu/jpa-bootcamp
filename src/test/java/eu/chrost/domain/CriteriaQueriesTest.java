package eu.chrost.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static eu.chrost.domain.CompanyType.BUDZETOWKA;
import static eu.chrost.domain.CompanyType.JANUSZEX;
import static eu.chrost.domain.CompanyType.KORPO;
import static eu.chrost.util.Utils.runInTransaction;

record CompanyQuery(String name, CompanyType companyType) {}

@Slf4j
class CriteriaQueriesTest {
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
    void criteriaQueries() {
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
            var companies = findCompanies(entityManager, new CompanyQuery(null, null));
            log.info("{}", companies);
            companies = findCompanies(entityManager, new CompanyQuery("JANUSZPOL", null));
            log.info("{}", companies);
            companies = findCompanies(entityManager, new CompanyQuery(null, JANUSZEX));
            log.info("{}", companies);
            companies = findCompanies(entityManager, new CompanyQuery("JANUSZPOL", JANUSZEX));
            log.info("{}", companies);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var companies = allCompanies(entityManager);
            log.info("{}", companies);
        });

        runInTransaction(entityManagerFactory, entityManager -> {
            var companies = findCompaniesWithCriteria(entityManager, new CompanyQuery(null, null));
            log.info("{}", companies);
            companies = findCompaniesWithCriteria(entityManager, new CompanyQuery("JANUSZPOL", null));
            log.info("{}", companies);
            companies = findCompaniesWithCriteria(entityManager, new CompanyQuery(null, JANUSZEX));
            log.info("{}", companies);
            companies = findCompaniesWithCriteria(entityManager, new CompanyQuery("JANUSZPOL", JANUSZEX));
            log.info("{}", companies);
        });
    }

    private static List<Company> allCompanies(EntityManager entityManager) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var criteriaQuery = criteriaBuilder.createQuery(Company.class);
        var root = criteriaQuery.from(Company.class); //FROM Companies c
        criteriaQuery.select(root); //SELECT c
        var typedQuery = entityManager.createQuery(criteriaQuery);
        var result = typedQuery.getResultList();
        return result;
    }

    private static List<Company> findCompaniesWithCriteria(EntityManager entityManager, CompanyQuery companyQuery) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var criteriaQuery = criteriaBuilder.createQuery(Company.class);
        var root = criteriaQuery.from(Company.class); //FROM Companies c
        criteriaQuery.select(root); //SELECT c

        var predicates = new ArrayList<Predicate>();

        var name = companyQuery.name();
        if (name != null) {
            var namePredicate = criteriaBuilder.equal(root.get(Company_.name), name);
            predicates.add(namePredicate);
        }

        var companyType = companyQuery.companyType();
        if (companyType != null) {
            var companyTypePredicate = criteriaBuilder.equal(root.get(Company_.companyType), companyType);
            predicates.add(companyTypePredicate);
        }

        var predicatesArray = predicates.toArray(new Predicate[0]);
        criteriaQuery.where(predicatesArray); //WHERE predicate1 AND predicate2

        var typedQuery = entityManager.createQuery(criteriaQuery);
        var result = typedQuery.getResultList();
        return result;
    }


    private static List<Company> findCompanies(EntityManager entityManager, CompanyQuery companyQuery) {
        var query = "from Company";
        var name = companyQuery.name();
        var companyType = companyQuery.companyType();
        if (name != null && companyType != null) {
            query += " where name = :name and companyType = :companyType";
        } else if (name != null) {
            query += " where name = :name";
        } else if (companyType != null) {
            query += " where companyType = :companyType";
        }
        var typedQuery = entityManager.createQuery(query, Company.class);
        if (name != null) {
            typedQuery.setParameter("name", name);
        }
        if (companyType != null) {
            typedQuery.setParameter("companyType", companyType);
        }
        var result = typedQuery.getResultList();
        return result;
    }



}

