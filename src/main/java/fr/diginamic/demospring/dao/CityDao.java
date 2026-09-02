package fr.diginamic.demospring.dao;

import fr.diginamic.demospring.model.City;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CityDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<City> findAll() {
        return entityManager.createQuery("SELECT c FROM City c", City.class).getResultList();
    }

    public Optional<City> findById(int id) {
        return Optional.ofNullable(entityManager.find(City.class, id));
    }

    public boolean existsByName(String name) {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(c) " +
                "FROM City c " +
                "WHERE LOWER(c.name) = LOWER(:name)", Long.class);
        query.setParameter("name", name);
        return query.getSingleResult() > 0;
    }

    public void save(City city) {
        entityManager.persist(city);
    }

    public boolean deleteById(int id) {

        Optional<City> city = findById(id);

        if (city.isEmpty()) {
            return false;
        }

        entityManager.remove(city.get());
        return true;
    }

    public Optional<City> findByName(String name) {
        TypedQuery<City> query = entityManager.createQuery("SELECT c " +
                "FROM City c " +
                "WHERE LOWER(c.name) = LOWER(:name)", City.class);
        query.setParameter("name", name);
        return query.getResultStream().findFirst();
    }

    public List<City> findByNameStartingWith(String prefix) {
        TypedQuery<City> query = entityManager.createQuery("SELECT c " +
                        "FROM City c " +
                        "WHERE LOWER(c.name) LIKE LOWER(:prefix)", City.class);
        query.setParameter("prefix", prefix + "%");
        return query.getResultList();
    }

    public List<City> findByPopulationGreaterThan(int min) {
        TypedQuery<City> query = entityManager.createQuery("SELECT c " +
                "FROM City c " +
                "WHERE c.population > :min", City.class);
        query.setParameter("min", min);
        return query.getResultList();
    }

    public List<City> findByPopulationBetween(int min, int max) {
        TypedQuery<City> query = entityManager.createQuery("SELECT c " +
                "FROM City c " +
                "WHERE c.population > :min " +
                "AND c.population < :max", City.class);
        query.setParameter("min", min);
        query.setParameter("max", max);
        return query.getResultList();
    }

    public List<City> findTopByDepartmentOrderByPopulationDesc(int departmentId, int limit) {
        TypedQuery<City> query = entityManager.createQuery("SELECT c " +
                "FROM City c " +
                "WHERE c.department.id = :departmentId " +
                "ORDER BY c.population DESC",  City.class);
        query.setParameter("departmentId", departmentId);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<City> findByDepartmentAndPopulationBetween(int departmentId, int min, int max) {
        TypedQuery<City> query = entityManager.createQuery("SELECT c " +
                "FROM City c " +
                "WHERE c.department.id = :departmentId " +
                "AND c.population > :min " +
                "AND c.population < :max", City.class);
        query.setParameter("departmentId", departmentId);
        query.setParameter("min", min);
        query.setParameter("max", max);
        return query.getResultList();
    }
}