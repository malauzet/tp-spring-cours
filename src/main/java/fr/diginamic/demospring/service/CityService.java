package fr.diginamic.demospring.service;

import fr.diginamic.demospring.dto.CityDto;
import fr.diginamic.demospring.exception.CityException;
import fr.diginamic.demospring.exception.NotFoundException;
import fr.diginamic.demospring.model.City;
import fr.diginamic.demospring.model.Department;
import fr.diginamic.demospring.repository.CityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
 *
 * <p>Search methods return whatever matched, including an empty list; "no
 * result" is not an error. Only single-resource lookups signal absence, and they
 * do so with an empty {@link Optional} that the controller turns into a
 * {@code 404}.</p>
 */
@Service
public class CityService {

    private final CityRepository cityRepository;
    private final DepartmentService departmentService;

    /**
     * @param cityRepository    city repository
     * @param departmentService used to resolve the department a city belongs to
     */
    public CityService(CityRepository cityRepository, DepartmentService departmentService) {
        this.cityRepository = cityRepository;
        this.departmentService = departmentService;
    }

    /**
     * @param pageable paging information
     * @return the requested page of cities
     */
    public Page<CityDto> getCities(Pageable pageable) {
        return cityRepository.findAll(pageable).map(CityDto::fromEntity);
    }

    /**
     * @param id city id
     * @return the city, or {@link Optional#empty()} if not found
     */
    public Optional<CityDto> getCityById(int id) {
        return cityRepository.findById(id).map(CityDto::fromEntity);
    }

    /**
     * @param name exact city name (case-insensitive)
     * @return the first matching city, or {@link Optional#empty()}
     */
    public Optional<CityDto> getCityByName(String name) {
        return cityRepository.findByNameIgnoreCase(name).map(CityDto::fromEntity);
    }

    /**
     * Creates a city and attaches it to its department.
     *
     * @param city the city to create; must carry a department id or code
     * @return the created city
     * @throws CityException if no department can be resolved, or a city with the
     *                       same name already exists in that department
     */
    @Transactional
    public CityDto addCity(CityDto city) throws CityException {

        Department department = departmentService.resolve(city.getDepartmentId(), city.getDepartmentCode());

        if (cityRepository.existsByNameIgnoreCaseAndDepartmentId(city.getName(), department.getId())) {
            throw new CityException("The city '" + city.getName() + "' already exists in this department.");
        }

        City entity = city.toEntity();
        entity.setDepartment(department);

        return CityDto.fromEntity(cityRepository.save(entity));
    }

    /**
     * Updates an existing city (name, population and department).
     *
     * @param id      id of the city to update
     * @param newData new values; must carry a department id or code
     * @return the updated city
     * @throws CityException     if no department can be resolved, or another city
     *                           with the same name already exists in that department
     * @throws NotFoundException if no city has the given id
     */
    @Transactional
    public CityDto updateCity(int id, CityDto newData) throws CityException, NotFoundException {

        City city = cityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("City with id " + id + " not found"));

        Department department = departmentService.resolve(newData.getDepartmentId(), newData.getDepartmentCode());

        if (cityRepository.existsByNameIgnoreCaseAndDepartmentIdAndIdNot(newData.getName(), department.getId(), id)) {
            throw new CityException("The city '" + newData.getName() + "' already exists in this department.");
        }

        city.setName(newData.getName());
        city.setPopulation(newData.getPopulation());
        city.setDepartment(department);

        return CityDto.fromEntity(cityRepository.save(city));
    }

    /**
     * Deletes a city by id.
     *
     * @param id id of the city to delete
     * @throws NotFoundException if no city has the given id
     */
    @Transactional
    public void deleteCity(int id) throws NotFoundException {

        if (!cityRepository.existsById(id)) {
            throw new NotFoundException("City with id " + id + " not found");
        }

        cityRepository.deleteById(id);
    }

    /**
     * @param prefix case-insensitive name prefix
     * @return cities whose name starts with {@code prefix} (empty if none)
     */
    public List<CityDto> searchByNameStartingWith(String prefix) {
        return cityRepository.findByNameStartingWithIgnoreCase(prefix).stream()
                .map(CityDto::fromEntity)
                .toList();
    }

    /**
     * @param min exclusive lower bound on population
     * @return cities more populated than {@code min}, most populated first
     *         (empty if none)
     */
    public List<CityDto> searchByPopulationGreaterThan(int min) {
        return cityRepository.findByPopulationGreaterThanOrderByPopulationDesc(min).stream()
                .map(CityDto::fromEntity)
                .toList();
    }

    /**
     * @param min exclusive lower bound on population
     * @param max exclusive upper bound on population
     * @return cities whose population lies strictly between the bounds, most
     *         populated first (empty if none)
     */
    public List<CityDto> searchByPopulationBetween(int min, int max) {
        return cityRepository.findByPopulationBetweenOrderByPopulationDesc(min, max).stream()
                .map(CityDto::fromEntity)
                .toList();
    }

    /**
     * Lists the most populated cities of a department.
     *
     * @param departmentId id of the department
     * @param limit        maximum number of cities to return (at least 1)
     * @return up to {@code limit} cities ordered by descending population
     *         (empty if the department has no city)
     */
    public List<CityDto> getLargestCitiesOfDepartment(int departmentId, int limit) {

        Pageable topN = PageRequest.of(0, limit);
        return cityRepository.findByDepartmentIdOrderByPopulationDesc(departmentId, topN).getContent().stream()
                .map(CityDto::fromEntity)
                .toList();
    }

    /**
     * Lists the cities of a department whose population lies within a range.
     *
     * @param departmentId id of the department
     * @param min          exclusive lower bound on population
     * @param max          exclusive upper bound on population
     * @return the matching cities, most populated first (empty if none)
     */
    public List<CityDto> searchByPopulationBetweenInDepartment(int departmentId, int min, int max) {
        return cityRepository.findByDepartmentIdAndPopulationBetweenOrderByPopulationDesc(departmentId, min, max).stream()
                .map(CityDto::fromEntity)
                .toList();
    }

    /**
     * Lists the cities of a department whose population is above a threshold.
     *
     * @param departmentId id of the department
     * @param min          exclusive lower bound on population
     * @return the matching cities, most populated first (empty if none)
     */
    public List<CityDto> searchByPopulationGreaterThanInDepartment(int departmentId, int min) {
        return cityRepository.findByDepartmentIdAndPopulationGreaterThanOrderByPopulationDesc(departmentId, min).stream()
                .map(CityDto::fromEntity)
                .toList();
    }
}
