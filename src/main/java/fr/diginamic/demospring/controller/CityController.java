package fr.diginamic.demospring.controller;

import fr.diginamic.demospring.exception.CityException;
import fr.diginamic.demospring.model.City;
import fr.diginamic.demospring.service.CityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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

    @GetMapping("/search/name/{name}")
    public ResponseEntity<City> getCityByName(@PathVariable String name) {
        Optional<City> city = cityService.getCityByName(name);
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
    public ResponseEntity<List<City>> addCity(@Valid @RequestBody City city, BindingResult result) throws CityException {

        if (result.hasErrors()){
            throw new CityException(result.getFieldErrors().getFirst().getDefaultMessage());
        }

        List<City> cities = cityService.addCity(city);
        return ResponseEntity.ok(cities);
    }

    @PutMapping("/{id}")
    public ResponseEntity<List<City>> updateCity(@PathVariable int id, @Valid @RequestBody City city, BindingResult result) throws CityException {

        if (result.hasErrors()) {
            throw new CityException(result.getFieldErrors().getFirst().getDefaultMessage());
        }

        List<City> cities = cityService.updateCity(id, city);
        return ResponseEntity.ok(cities);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<City>> deleteCity(@PathVariable int id) throws CityException {

        List<City> cities = cityService.deleteCity(id);
        return ResponseEntity.ok(cities);
    }
}
