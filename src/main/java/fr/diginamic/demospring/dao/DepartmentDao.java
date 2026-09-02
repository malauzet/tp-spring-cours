package fr.diginamic.demospring.dao;

import fr.diginamic.demospring.model.Department;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data access object for {@link Department}, built directly on the JPA
 * {@link EntityManager}.
 *
 * <p>Write methods assume they run inside a transaction opened by the calling
 * service.</p>
 */
@Repository
public class DepartmentDao {

    @PersistenceContext
    private EntityManager entityManager;

    /** @return every department in the database */
    public List<Department> findAll() {
        return entityManager.createQuery("SELECT d FROM Department d", Department.class).getResultList();
    }

    /**
     * @param id primary key to look up
     * @return the department, or {@link Optional#empty()} if none has this id
     */
    public Optional<Department> findById(int id) {
        return Optional.ofNullable(entityManager.find(Department.class, id));
    }

    /**
     * @param code INSEE department code (case-insensitive)
     * @return the matching department, or {@link Optional#empty()}
     */
    public Optional<Department> findByCode(String code) {
        TypedQuery<Department> query = entityManager.createQuery("SELECT d " +
                "FROM Department d " +
                "WHERE LOWER(d.code) = LOWER(:code)", Department.class);
        query.setParameter("code", code);
        return query.getResultStream().findFirst();
    }

    /**
     * @param code INSEE department code (case-insensitive)
     * @return {@code true} if a department with this code exists
     */
    public boolean existsByCode(String code) {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(d) " +
                "FROM Department d " +
                "WHERE LOWER(d.code) = LOWER(:code)", Long.class);
        query.setParameter("code", code);
        return query.getSingleResult() > 0;
    }

    /**
     * Persists a new department.
     *
     * @param department transient entity to insert
     */
    public void save(Department department) {
        entityManager.persist(department);
    }

    /**
     * Deletes a department by id.
     *
     * @param id primary key of the department to delete
     * @return {@code true} if a department was deleted, {@code false} if none matched
     */
    public boolean deleteById(int id) {

        Optional<Department> department = findById(id);

        if (department.isEmpty()) {
            return false;
        }

        entityManager.remove(department.get());
        return true;
    }
}
