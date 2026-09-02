package fr.diginamic.demospring.dao;

import fr.diginamic.demospring.model.City;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data access object for {@link City}, built directly on the JPA
 * {@link EntityManager} (no Spring Data repository).
 *
 * <p>Write methods assume they run inside a transaction opened by the calling
 * service.</p>
 */
@Repository
public class CityDao {

    @PersistenceContext
    private EntityManager entityManager;

    /** @return every city in the database */
    public List<City> findAll() {
        return entityManager.createQuery("SELECT c FROM City c", City.class).getResultList();
    }

    /**
     * @param id primary key to look up
     * @return the city, or {@link Optional#empty()} if none has this id
     */
    public Optional<City> findById(int id) {
        return Optional.ofNullable(entityManager.find(City.class, id));
    }

    /**
     * Tests whether a city with the given name already exists in a department
     * (case-insensitive).
     *
     * @param name         city name to test
     * @param departmentId id of the department to scope the search to
     * @return {@code true} if such a city exists
     */
    public boolean existsByNameAndDepartmentId(String name, int departmentId) {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(c) " +
                "FROM City c " +
                "WHERE LOWER(c.name) = LOWER(:name) " +
                "AND c.department.id = :departmentId", Long.class);
        query.setParameter("name", name);
        query.setParameter("departmentId", departmentId);
        return query.getSingleResult() > 0;
    }

    /**
     * Same as {@link #existsByNameAndDepartmentId} but ignoring one city, used on
     * update so a city does not collide with itself.
     *
     * @param name         city name to test
     * @param departmentId id of the department to scope the search to
     * @param id           id of the city to exclude from the check
     * @return {@code true} if a <em>different</em> city with this name exists in
     *         the department
     */
    public boolean existsByNameAndDepartmentIdAndIdNot(String name, int departmentId, int id) {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(c) " +
                "FROM City c " +
                "WHERE LOWER(c.name) = LOWER(:name) " +
                "AND c.department.id = :departmentId " +
                "AND c.id <> :id", Long.class);
        query.setParameter("name", name);
        query.setParameter("departmentId", departmentId);
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }

    /**
     * Persists a new city.
     *
     * @param city transient entity to insert
     */
    public void save(City city) {
        entityManager.persist(city);
    }

    /**
     * Deletes a city by id.
     *
     * @param id primary key of the city to delete
     * @return {@code true} if a city was deleted, {@code false} if none matched
     */
    public boolean deleteById(int id) {

        Optional<City> city = findById(id);

        if (city.isEmpty()) {
            return false;
        }

        entityManager.remove(city.get());
        return true;
    }

    /**
     * @param name exact city name (case-insensitive)
     * @return the first matching city, or {@link Optional#empty()}
     */
    public Optional<City> findByName(String name) {
        TypedQuery<City> query = entityManager.createQuery("SELECT c " +
                "FROM City c " +
                "WHERE LOWER(c.name) = LOWER(:name)", City.class);
        query.setParameter("name", name);
        return query.getResultStream().findFirst();
    }

    /**
     * @param prefix case-insensitive name prefix
     * @return cities whose name starts with {@code prefix}
     */
    public List<City> findByNameStartingWith(String prefix) {
        TypedQuery<City> query = entityManager.createQuery("SELECT c " +
                        "FROM City c " +
                        "WHERE LOWER(c.name) LIKE LOWER(:prefix)", City.class);
        query.setParameter("prefix", prefix + "%");
        return query.getResultList();
    }

    /**
     * @param min exclusive lower bound
     * @return cities with a population strictly greater than {@code min}
     */
    public List<City> findByPopulationGreaterThan(int min) {
        TypedQuery<City> query = entityManager.createQuery("SELECT c " +
                "FROM City c " +
                "WHERE c.population > :min", City.class);
        query.setParameter("min", min);
        return query.getResultList();
    }

    /**
     * @param min exclusive lower bound
     * @param max exclusive upper bound
     * @return cities whose population lies strictly between {@code min} and {@code max}
     */
    public List<City> findByPopulationBetween(int min, int max) {
        TypedQuery<City> query = entityManager.createQuery("SELECT c " +
                "FROM City c " +
                "WHERE c.population > :min " +
                "AND c.population < :max", City.class);
        query.setParameter("min", min);
        query.setParameter("max", max);
        return query.getResultList();
    }

    /**
     * Returns the most populated cities of a department.
     *
     * @param departmentId id of the department
     * @param limit        maximum number of cities to return
     * @return cities of the department ordered by descending population, capped
     *         at {@code limit}
     */
    public List<City> findTopByDepartmentOrderByPopulationDesc(int departmentId, int limit) {
        TypedQuery<City> query = entityManager.createQuery("SELECT c " +
                "FROM City c " +
                "WHERE c.department.id = :departmentId " +
                "ORDER BY c.population DESC",  City.class);
        query.setParameter("departmentId", departmentId);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    /**
     * @param departmentId id of the department to scope the search to
     * @param min          exclusive lower bound
     * @param max          exclusive upper bound
     * @return cities of the department whose population lies strictly between
     *         {@code min} and {@code max}
     */
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
