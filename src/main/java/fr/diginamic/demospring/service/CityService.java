package fr.diginamic.demospring.service;

import fr.diginamic.demospring.dto.CityDto;
import fr.diginamic.demospring.exception.CityException;
import fr.diginamic.demospring.exception.NotFoundException;
import fr.diginamic.demospring.model.City;
import fr.diginamic.demospring.dao.CityDao;
import fr.diginamic.demospring.model.Department;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Business logic for cities.
 *
 * <p>All public methods accept and return {@link CityDto}; entities never leave
 * this layer. Department resolution (find by id/code, or create from an unknown
 * code) is delegated to {@link DepartmentService#resolve}.</p>
 */
@Service
public class CityService {

    private final CityDao cityDao;
    private final DepartmentService departmentService;

    /**
     * @param cityDao           city data access object
     * @param departmentService used to resolve the department a city belongs to
     */
    public CityService(CityDao cityDao, DepartmentService departmentService) {
        this.cityDao = cityDao;
        this.departmentService = departmentService;
    }

    /** @return every city */
    public List<CityDto> getCities() {
        return cityDao.findAll().stream()
                .map(CityDto::fromEntity)
                .toList();
    }

    /**
     * @param id city id
     * @return the city, or {@link Optional#empty()} if not found
     */
    public Optional<CityDto> getCityById(int id) {
        return cityDao.findById(id).map(CityDto::fromEntity);
    }

    /**
     * @param name exact city name (case-insensitive)
     * @return the first matching city, or {@link Optional#empty()}
     */
    public Optional<CityDto> getCityByName(String name) {
        return cityDao.findByName(name).map(CityDto::fromEntity);
    }

    /**
     * Creates a city and attaches it to its department.
     *
     * @param city the city to create; must carry a department id or code
     * @return the full list of cities after insertion
     * @throws CityException if no department can be resolved, or a city with the
     *                       same name already exists in that department
     */
    @Transactional
    public List<CityDto> addCity(CityDto city) throws CityException {

        Department department = departmentService.resolve(city.getDepartmentId(), city.getDepartmentCode());

        if (cityDao.existsByNameAndDepartmentId(city.getName(), department.getId())) {
            throw new CityException("The city '" + city.getName() + "' already exists in this department.");
        }

        City entity = city.toEntity();
        entity.setDepartment(department);

        cityDao.save(entity);
        return cityDao.findAll().stream().map(CityDto::fromEntity).toList();
    }

    /**
     * Updates an existing city (name, population and department).
     *
     * @param id      id of the city to update
     * @param newData new values; must carry a department id or code
     * @return the full list of cities after the update
     * @throws CityException     if no department can be resolved, or another city
     *                           with the same name already exists in that department
     * @throws NotFoundException if no city has the given id
     */
    @Transactional
    public List<CityDto> updateCity(int id, CityDto newData) throws CityException, NotFoundException {

        Optional<City> existing = cityDao.findById(id);

        if (existing.isEmpty()) {
            throw new NotFoundException("City with id " + id + " not found");
        }

        Department department = departmentService.resolve(newData.getDepartmentId(), newData.getDepartmentCode());

        if (cityDao.existsByNameAndDepartmentIdAndIdNot(newData.getName(), department.getId(), id)) {
            throw new CityException("The city '" + newData.getName() + "' already exists in this department.");
        }

        City city = existing.get();
        city.setName(newData.getName());
        city.setPopulation(newData.getPopulation());
        city.setDepartment(department);
        return cityDao.findAll().stream().map(CityDto::fromEntity).toList();
    }

    /**
     * Deletes a city by id.
     *
     * @param id id of the city to delete
     * @return the full list of cities after deletion
     * @throws NotFoundException if no city has the given id
     */
    @Transactional
    public List<CityDto> deleteCity(int id) throws NotFoundException {

        boolean deleted = cityDao.deleteById(id);

        if (!deleted) {
            throw new NotFoundException("City with id " + id + " not found");
        }

        return cityDao.findAll().stream().map(CityDto::fromEntity).toList();
    }

    /**
     * @param prefix case-insensitive name prefix
     * @return cities whose name starts with {@code prefix}
     * @throws CityException if no city matches
     */
    public List<CityDto> searchByNameStartingWith(String prefix) throws CityException {
        List<City> result = cityDao.findByNameStartingWith(prefix);

        if (result.isEmpty()) {
            throw new CityException("No city found with a name starting with " + prefix);
        }

        return result.stream().map(CityDto::fromEntity).toList();
    }

    /**
     * @param min exclusive lower bound on population
     * @return cities with a population strictly greater than {@code min}
     * @throws CityException if no city matches
     */
    public List<CityDto> searchByPopulationGreaterThan(int min) throws CityException {
        List<City> result = cityDao.findByPopulationGreaterThan(min);

        if (result.isEmpty()) {
            throw new CityException("No city has a population greater than " + min);
        }

        return result.stream().map(CityDto::fromEntity).toList();
    }

    /**
     * @param min exclusive lower bound on population
     * @param max exclusive upper bound on population
     * @return cities whose population lies strictly between {@code min} and {@code max}
     * @throws CityException if no city matches
     */
    public List<CityDto> searchByPopulationBetween(int min, int max) throws CityException {
        List<City> result = cityDao.findByPopulationBetween(min, max);

        if (result.isEmpty()) {
            throw new CityException("No city has a population between " + min + " and " + max);
        }

        return result.stream().map(CityDto::fromEntity).toList();
    }

    /**
     * Lists the most populated cities of a department.
     *
     * @param departmentId id of the department
     * @param limit        maximum number of cities to return
     * @return up to {@code limit} cities ordered by descending population
     * @throws CityException if the department has no city
     */
    public List<CityDto> getLargestCitiesOfDepartment(int departmentId, int limit) throws CityException {
        List<City> result = cityDao.findTopByDepartmentOrderByPopulationDesc(departmentId, limit);

        if (result.isEmpty()) {
            throw new CityException("No city found with a department with id " + departmentId);
        }

        return result.stream().map(CityDto::fromEntity).toList();
    }

    /**
     * Lists the cities of a department whose population lies within a range.
     *
     * @param departmentId id of the department
     * @param min          exclusive lower bound on population
     * @param max          exclusive upper bound on population
     * @return the matching cities
     * @throws CityException if no city matches
     */
    public List<CityDto> searchByPopulationBetweenInDepartment(int departmentId, int min, int max) throws CityException {
        List<City> result = cityDao.findByDepartmentAndPopulationBetween(departmentId, min, max);

        if (result.isEmpty()) {
            throw new CityException("No city in department " + departmentId + " has a population between " + min + " and " + max);
        }

        return result.stream().map(CityDto::fromEntity).toList();
    }
}
