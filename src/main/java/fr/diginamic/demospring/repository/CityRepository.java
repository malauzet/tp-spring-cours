package fr.diginamic.demospring.repository;

import fr.diginamic.demospring.model.City;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CityRepository {

    private final List<City> cities = new ArrayList<>();
    private int incrementId = 1;

    @PostConstruct
    public void initData() {
        cities.add(new City(incrementId++, "Paris", 2_161_000));
        cities.add(new City(incrementId++, "New York", 8_336_000));
        cities.add(new City(incrementId++, "Tokyo", 13_960_000));
        cities.add(new City(incrementId++, "London", 9_002_000));
        cities.add(new City(incrementId++, "Berlin", 1_780_000));
    }

    public List<City> findAll() {
        return cities;
    }

    public Optional<City> findById(int id) {
        return cities.stream().filter(c -> c.getId() == id).findFirst();
    }

    public boolean existsByName(String name) {
        return cities.stream().anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }

    public void save(City city) {
        city.setId(incrementId++);
        cities.add(city);
    }

    public boolean deleteById(int id) {
        return cities.removeIf(c -> c.getId() == id);
    }
}