package fr.diginamic.demospring.service;

import fr.diginamic.demospring.dto.CityDto;
import fr.diginamic.demospring.exception.CityException;
import fr.diginamic.demospring.model.City;
import fr.diginamic.demospring.dao.CityDao;
import fr.diginamic.demospring.model.Department;
import jakarta.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {

    private final CityDao cityDao;
    private final DepartmentService departmentService;

    public CityService(CityDao cityDao, DepartmentService departmentService) {
        this.cityDao = cityDao;
        this.departmentService = departmentService;
    }

    public List<CityDto> getCities() {
        return cityDao.findAll().stream()
                .map(CityDto::fromEntity)
                .toList();
    }

    public Optional<CityDto> getCityById(int id) {
        return cityDao.findById(id).map(CityDto::fromEntity);
    }

    public Optional<CityDto> getCityByName(String name) {
        return cityDao.findByName(name).map(CityDto::fromEntity);
    }

    @Transactional
    public List<CityDto> addCity(CityDto city) throws CityException {

        if (cityDao.existsByName(city.getName())) {
            throw new CityException("The city '" + city.getName() + "' already exists.");
        }

        Department department = departmentService.resolve(city.getDepartmentId(), city.getDepartmentCode());

        City entity = city.toEntity();
        entity.setDepartment(department);

        cityDao.save(entity);
        return cityDao.findAll().stream().map(CityDto::fromEntity).toList();
    }

    @Transactional
    public List<CityDto> updateCity(int id, CityDto newData) throws CityException {

        Optional<City> existing = cityDao.findById(id);

        if (existing.isEmpty()) {
            throw new CityException("City with id " + id + " not found");
        }

        Department department = departmentService.resolve(newData.getDepartmentId(), newData.getDepartmentCode());

        City city = existing.get();
        city.setName(newData.getName());
        city.setPopulation(newData.getPopulation());
        city.setDepartment(department);
        return cityDao.findAll().stream().map(CityDto::fromEntity).toList();
    }

    @Transactional
    public List<CityDto> deleteCity(int id) throws CityException {

        boolean deleted = cityDao.deleteById(id);

        if (!deleted) {
            throw new CityException("City with id " + id + " not found");
        }

        return cityDao.findAll().stream().map(CityDto::fromEntity).toList();
    }

    public List<CityDto> searchByNameStartingWith(String prefix) throws CityException {
        List<City> result = cityDao.findByNameStartingWith(prefix);

        if (result.isEmpty()) {
            throw new CityException("No city found with a name starting with " + prefix);
        }

        return result.stream().map(CityDto::fromEntity).toList();
    }

    public List<CityDto> searchByPopulationGreaterThan(int min) throws CityException {
        List<City> result = cityDao.findByPopulationGreaterThan(min);

        if (result.isEmpty()) {
            throw new CityException("No city has a population greater than " + min);
        }

        return result.stream().map(CityDto::fromEntity).toList();
    }

    public List<CityDto> searchByPopulationBetween(int min, int max) throws CityException {
        List<City> result = cityDao.findByPopulationBetween(min, max);

        if (result.isEmpty()) {
            throw new CityException("No city has a population between " + min + " and " + max);
        }

        return result.stream().map(CityDto::fromEntity).toList();
    }

    public List<CityDto> getLargestCitiesOfDepartment(int departmentId, int limit) throws CityException {
        List<City> result = cityDao.findTopByDepartmentOrderByPopulationDesc(departmentId, limit);

        if (result.isEmpty()) {
            throw new CityException("No city found with a department with id " + departmentId);
        }

        return result.stream().map(CityDto::fromEntity).toList();
    }

    public List<CityDto> searchByPopulationBetweenInDepartment(int departmentId, int min, int max) throws CityException {
        List<City> result = cityDao.findByDepartmentAndPopulationBetween(departmentId, min, max);

        if (result.isEmpty()) {
            throw new CityException("No city in department " + departmentId + " has a population between " + min + " and " + max);
        }

        return result.stream().map(CityDto::fromEntity).toList();
    }
}
