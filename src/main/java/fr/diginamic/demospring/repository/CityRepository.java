package fr.diginamic.demospring.repository;

import fr.diginamic.demospring.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for {@link City}.
 *
 * <p>Inherits the standard CRUD and pagination methods from
 * {@link JpaRepository}; the methods declared here are derived queries used by
 * {@link fr.diginamic.demospring.service.CityService}. Population bounds are
 * always <em>exclusive</em> and result lists are ordered by descending
 * population.</p>
 */
public interface CityRepository extends JpaRepository<City, Integer> {

    /**
     * @param name exact city name (case-insensitive)
     * @return the first matching city, or {@link Optional#empty()}
     */
    Optional<City> findByNameIgnoreCase(String name);

    /**
     * @param name         city name to test (case-insensitive)
     * @param departmentId id of the department to scope the check to
     * @return {@code true} if a city with this name already exists in the department
     */
    boolean existsByNameIgnoreCaseAndDepartmentId(String name, int departmentId);

    /**
     * Same as {@link #existsByNameIgnoreCaseAndDepartmentId} but excluding one
     * city, so an updated city does not collide with itself.
     *
     * @param name         city name to test (case-insensitive)
     * @param departmentId id of the department to scope the check to
     * @param id           id of the city to exclude from the check
     * @return {@code true} if a <em>different</em> city with this name exists in
     *         the department
     */
    boolean existsByNameIgnoreCaseAndDepartmentIdAndIdNot(String name, int departmentId, int id);

    /**
     * @param prefix case-insensitive name prefix
     * @return cities whose name starts with {@code prefix}
     */
    List<City> findByNameStartingWithIgnoreCase(String prefix);

    /**
     * @param min exclusive lower bound on population
     * @return cities more populated than {@code min}, most populated first
     */
    List<City> findByPopulationGreaterThanOrderByPopulationDesc(int min);

    /**
     * @param min exclusive lower bound on population
     * @param max exclusive upper bound on population
     * @return cities whose population lies strictly between the bounds, most
     *         populated first
     */
    List<City> findByPopulationBetweenOrderByPopulationDesc(int min, int max);

    /**
     * @param departmentId id of the department to scope the search to
     * @param min          exclusive lower bound on population
     * @return the department's cities more populated than {@code min}, most
     *         populated first
     */
    List<City> findByDepartmentIdAndPopulationGreaterThanOrderByPopulationDesc(int departmentId, int min);

    /**
     * @param departmentId id of the department to scope the search to
     * @param min          exclusive lower bound on population
     * @param max          exclusive upper bound on population
     * @return the department's cities whose population lies strictly between the
     *         bounds, most populated first
     */
    List<City> findByDepartmentIdAndPopulationBetweenOrderByPopulationDesc(int departmentId, int min, int max);

    /**
     * @param departmentId id of the department
     * @param pageable     paging information, used to cap the result size
     * @return a page of the department's cities ordered by descending population
     */
    Page<City> findByDepartmentIdOrderByPopulationDesc(int departmentId, Pageable pageable);
}
