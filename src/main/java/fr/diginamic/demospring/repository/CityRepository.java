package fr.diginamic.demospring.repository;

import fr.diginamic.demospring.model.City;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CityRepository {

    private List<City> cities = new ArrayList<>();

    @PostConstruct
    public void initData() {
        cities.add(new City("Paris", 2_161_000));
        cities.add(new City("New York", 8_336_000));
        cities.add(new City("Tokyo", 13_960_000));
        cities.add(new City("London", 9_002_000));
        cities.add(new City("Berlin", 1_780_000));
    }

    public List<City> findAll() {
        return cities;
    }

    public boolean existsByName(String name) {
        return cities.stream().anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }

    public void save(City city) {
        cities.add(city);
    }
}