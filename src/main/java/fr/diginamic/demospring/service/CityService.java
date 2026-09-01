package fr.diginamic.demospring.service;

import fr.diginamic.demospring.exception.CityException;
import fr.diginamic.demospring.model.City;
import fr.diginamic.demospring.dao.CityDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {

    private final CityDao cityDao;

    public CityService(CityDao cityDao) {
        this.cityDao = cityDao;
    }

    public List<City> getCities() {
        return cityDao.findAll();
    }

    public Optional<City> getCityById(int id) {
        return cityDao.findById(id);
    }

    public Optional<City> getCityByName(String name) {
        return cityDao.findByName(name);
    }

    @Transactional
    public List<City> addCity(City city) throws CityException {

        if (cityDao.existsByName(city.getName())) {
            throw new CityException("The city '" + city.getName() + "' already exists.");
        }

        cityDao.save(city);
        return cityDao.findAll();
    }

    @Transactional
    public List<City> updateCity(int id, City newData) throws CityException {

        Optional<City> existing = cityDao.findById(id);

        if (existing.isEmpty()) {
            throw new CityException("City with id " + id + " not found");
        }

        City city = existing.get();
        city.setName(newData.getName());
        city.setPopulation(newData.getPopulation());
        return cityDao.findAll();
    }

    @Transactional
    public List<City> deleteCity(int id) throws CityException {

        boolean deleted = cityDao.deleteById(id);

        if (!deleted) {
            throw new CityException("City with id " + id + " not found");
        }

        return cityDao.findAll();
    }

    public List<City> searchByNameStartingWith(String prefix) throws CityException {
        List<City> result = cityDao.findByNameStartingWith(prefix);

        if (result.isEmpty()) {
            throw new CityException("No city found with a name starting with " + prefix);
        }

        return result;
    }

    public List<City> searchByPopulationGreaterThan(int min) throws CityException {
        List<City> result = cityDao.findByPopulationGreaterThan(min);

        if (result.isEmpty()) {
            throw new CityException("No city has a population greater than " + min);
        }

        return result;
    }

    public List<City> searchByPopulationBetween(int min, int max) throws CityException {
        List<City> result = cityDao.findByPopulationBetween(min, max);

        if (result.isEmpty()) {
            throw new CityException("No city has a population between " + min + " and " + max);
        }

        return result;
    }
}
