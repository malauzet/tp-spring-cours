package fr.diginamic.demospring.controller;

import fr.diginamic.demospring.model.City;
import fr.diginamic.demospring.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cities")
public class CityController {

    private final CityService cityService;

    private List<City> cities = new ArrayList<>();

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public List<City> getCities() {
        return cityService.getCities();
    }

    @PostMapping
    public ResponseEntity<String> addCity(@RequestBody City city) {
        boolean added = cityService.addCity(city);

        if (!added) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The city '" + city.getName() + "' already exists.");
        }

        return ResponseEntity.ok("City inserted successfully");
    }
}
