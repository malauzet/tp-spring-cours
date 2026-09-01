package fr.diginamic.demospring.service;

import fr.diginamic.demospring.exception.CityException;
import fr.diginamic.demospring.model.City;
import fr.diginamic.demospring.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<City> getCities() {
        return cityRepository.findAll();
    }

    public Optional<City> getCityById(int id) {
        return cityRepository.findById(id);
    }

    private void validateCity(City city) throws CityException {

        if (city.getPopulation() < 10) {
            throw new CityException("City population must be at least 10 inhabitants");
        }

        long letterCount = city.getName().chars().filter(Character::isLetter).count();
        if (letterCount < 2) {
            throw new CityException("City name must contain at least 2 letters");
        }
    }

    public boolean addCity(City city) throws CityException {

        validateCity(city);

        if (cityRepository.existsByName(city.getName())) {
            return false;
        }

        cityRepository.save(city);
        return true;
    }

    public boolean updateCity(int id, City newData) throws CityException {

        validateCity(newData);

        Optional<City> existing = cityRepository.findById(id);

        if (existing.isEmpty()) {
            return false;
        }

        City city = existing.get();
        city.setName(newData.getName());
        city.setPopulation(newData.getPopulation());
        return true;
    }

    public boolean deleteCity(int id) {
        return cityRepository.deleteById(id);
    }

    public List<City> searchByNameStartingWith(String prefix) throws CityException {
        List<City> result = cityRepository.findByNameStartingWith(prefix);

        if (result.isEmpty()) {
            throw new CityException("No city found with a name starting with " + prefix);
        }

        return result;
    }

    public List<City> searchByPopulationGreaterThan(int min) throws CityException {
        List<City> result = cityRepository.findByPopulationGreaterThan(min);

        if (result.isEmpty()) {
            throw new CityException("No city has a population greater than " + min);
        }

        return result;
    }

    public List<City> searchByPopulationBetween(int min, int max) throws CityException {
        List<City> result = cityRepository.findByPopulationBetween(min, max);

        if (result.isEmpty()) {
            throw new CityException("No city has a population between " + min + " and " + max);
        }

        return result;
    }
}
