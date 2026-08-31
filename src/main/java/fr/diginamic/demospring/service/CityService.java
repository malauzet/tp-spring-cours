package fr.diginamic.demospring.service;

import fr.diginamic.demospring.model.City;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CityService {

    public List<City> getCities() {
        List<City> cities = new ArrayList<>();
        cities.add(new City("Paris", 2_161_000));
        cities.add(new City("New York", 8_336_000));
        cities.add(new City("Tokyo", 13_960_000));
        cities.add(new City("London", 9_002_000));
        cities.add(new City("Berlin", 1_780_000));
        return cities;
    }
}
