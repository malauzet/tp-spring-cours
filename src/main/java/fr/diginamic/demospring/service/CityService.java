package fr.diginamic.demospring.service;

import fr.diginamic.demospring.model.City;
import fr.diginamic.demospring.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<City> getCities() {
        return cityRepository.findAll();
    }

    public boolean addCity(City city) {
        if (cityRepository.existsByName(city.getName())) {
            return false;
        }

        cityRepository.save(city);
        return true;
    }
}
