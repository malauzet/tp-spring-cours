package fr.diginamic.demospring.controller;

import fr.diginamic.demospring.exception.CityException;
import fr.diginamic.demospring.model.City;
import fr.diginamic.demospring.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public List<City> getCities() {
        return cityService.getCities();
    }

    @GetMapping("/{id}")
    public ResponseEntity<City> getCityById(@PathVariable int id) {

        Optional<City> city = cityService.getCityById(id);

        return city.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/search/startswith/{prefix}")
    public List<City> searchByNameStartingWith(@PathVariable String prefix) throws CityException {
        return cityService.searchByNameStartingWith(prefix);
    }

    @GetMapping("/search/population/greater/{min}")
    public List<City> searchByPopulationGreaterThan(@PathVariable int min) throws CityException {
        return cityService.searchByPopulationGreaterThan(min);
    }

    @GetMapping("/search/population/between/{min}/{max}")
    public List<City> searchByPopulationBetween(@PathVariable int min, @PathVariable int max) throws CityException {
        return cityService.searchByPopulationBetween(min, max);
    }

    @PostMapping
    public ResponseEntity<String> addCity(@RequestBody City city) throws CityException {

        boolean added = cityService.addCity(city);

        if (!added) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The city '" + city.getName() + "' already exists.");
        }

        return ResponseEntity.ok("City inserted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCity(@PathVariable int id, @RequestBody City city) throws CityException {

        boolean updated = cityService.updateCity(id, city);

        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("City with id " + id + " not found");
        }

        return ResponseEntity.ok("City updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCity(@PathVariable int id) {

        boolean deleted = cityService.deleteCity(id);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("City with id " + id + " not found");
        }

        return ResponseEntity.ok("City deleted successfully");
    }
}
