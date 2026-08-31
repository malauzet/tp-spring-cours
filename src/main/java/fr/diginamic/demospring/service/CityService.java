package fr.diginamic.demospring.service;

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

    public boolean addCity(City city) {

        if (cityRepository.existsByName(city.getName())) {
            return false;
        }

        cityRepository.save(city);
        return true;
    }

    public boolean updateCity(int id, City newData) {

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
}
