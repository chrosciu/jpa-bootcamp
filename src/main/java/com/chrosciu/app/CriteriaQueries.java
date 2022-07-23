package com.chrosciu.app;

import static com.chrosciu.domain.CompanyType.JANUSZEX;

import com.chrosciu.domain.Area;
import com.chrosciu.domain.Company;
import com.chrosciu.domain.CompanyType;
import com.chrosciu.domain.Company_;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.Predicate;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
class CompanyQuery {
    String name;
    CompanyType companyType;
}

@Slf4j
public class CriteriaQueries {

    public static void main(String[] args) {
        Utils.runInPersistence(entityManagerFactory -> {
            prepareData(entityManagerFactory);
//            Utils.runInTransaction(entityManagerFactory, entityManager -> {
//                var companies = findCompanies(entityManager, new CompanyQuery(null, null));
//                log.info("{}", companies);
//                companies = findCompanies(entityManager, new CompanyQuery("JANUSZPOL", null));
//                log.info("{}", companies);
//                companies = findCompanies(entityManager, new CompanyQuery(null, JANUSZEX));
//                log.info("{}", companies);
//                companies = findCompanies(entityManager, new CompanyQuery("JANUSZPOL", JANUSZEX));
//                log.info("{}", companies);
//            });

            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var companies = allCompanies(entityManager);
                log.info("{}", companies);
            });

            Utils.runInTransaction(entityManagerFactory, entityManager -> {
                var companies = findCompaniesWithCriteria(entityManager, new CompanyQuery(null, null));
                log.info("{}", companies);
                companies = findCompaniesWithCriteria(entityManager, new CompanyQuery("JANUSZPOL", null));
                log.info("{}", companies);
                companies = findCompaniesWithCriteria(entityManager, new CompanyQuery(null, JANUSZEX));
                log.info("{}", companies);
                companies = findCompaniesWithCriteria(entityManager, new CompanyQuery("JANUSZPOL", JANUSZEX));
                log.info("{}", companies);
            });
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

        var name = companyQuery.getName();
        if (name != null) {
            var namePredicate = criteriaBuilder.equal(root.get(Company_.name), name);
            predicates.add(namePredicate);
        }

        var companyType = companyQuery.getCompanyType();
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
        var name = companyQuery.getName();
        var companyType = companyQuery.getCompanyType();
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
