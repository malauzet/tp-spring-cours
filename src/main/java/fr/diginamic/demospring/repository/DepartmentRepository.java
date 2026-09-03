package fr.diginamic.demospring.repository;

import fr.diginamic.demospring.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data repository for {@link Department}.
 *
 * <p>Inherits the standard CRUD methods from {@link JpaRepository}; the two
 * derived queries declared here let
 * {@link fr.diginamic.demospring.service.DepartmentService} look a department up
 * by its INSEE code.</p>
 */
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    /**
     * @param code INSEE department code (case-insensitive)
     * @return the matching department, or {@link Optional#empty()}
     */
    Optional<Department> findByCodeIgnoreCase(String code);

    /**
     * @param code INSEE department code (case-insensitive)
     * @return {@code true} if a department with this code already exists
     */
    boolean existsByCodeIgnoreCase(String code);
}
