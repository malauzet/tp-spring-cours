package fr.diginamic.demospring.dao;

import fr.diginamic.demospring.model.Department;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DepartmentDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Department> findAll() {
        return entityManager.createQuery("SELECT d FROM Department d", Department.class).getResultList();
    }

    public Optional<Department> findById(int id) {
        return Optional.ofNullable(entityManager.find(Department.class, id));
    }

    public Optional<Department> findByCode(String code) {
        TypedQuery<Department> query = entityManager.createQuery("SELECT d " +
                "FROM Department d " +
                "WHERE LOWER(d.code) = LOWER(:code)", Department.class);
        query.setParameter("code", code);
        return query.getResultStream().findFirst();
    }

    public boolean existsByCode(String code) {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(d) " +
                "FROM Department d " +
                "WHERE LOWER(d.code) = LOWER(:code)", Long.class);
        query.setParameter("code", code);
        return query.getSingleResult() > 0;
    }

    public void save(Department department) {
        entityManager.persist(department);
    }

    public boolean deleteById(int id) {

        Optional<Department> department = findById(id);

        if (department.isEmpty()) {
            return false;
        }

        entityManager.remove(department.get());
        return true;
    }
}
