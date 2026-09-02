package fr.diginamic.demospring.controller;

import fr.diginamic.demospring.dto.CityDto;
import fr.diginamic.demospring.exception.CityException;
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
    public List<CityDto> getCities() {
        return cityService.getCities();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityDto> getCityById(@PathVariable int id) {

        Optional<CityDto> city = cityService.getCityById(id);

        return city.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/search/name/{name}")
    public ResponseEntity<CityDto> getCityByName(@PathVariable String name) {
        Optional<CityDto> city = cityService.getCityByName(name);
        return city.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/search/startswith/{prefix}")
    public List<CityDto> searchByNameStartingWith(@PathVariable String prefix) throws CityException {
        return cityService.searchByNameStartingWith(prefix);
    }

    @GetMapping("/search/population/greater/{min}")
    public List<CityDto> searchByPopulationGreaterThan(@PathVariable int min) throws CityException {
        return cityService.searchByPopulationGreaterThan(min);
    }

    @GetMapping("/search/population/between/{min}/{max}")
    public List<CityDto> searchByPopulationBetween(@PathVariable int min, @PathVariable int max) throws CityException {
        return cityService.searchByPopulationBetween(min, max);
    }

    @GetMapping("/search/department/{departmentId}/largest/{n}")
    public List<CityDto> getLargestCitiesOfDepartment(@PathVariable int departmentId,
                                                      @PathVariable int n) throws CityException {
        return cityService.getLargestCitiesOfDepartment(departmentId, n);
    }

    @GetMapping("/search/department/{departmentId}/population/between/{min}/{max}")
    public List<CityDto> searchByPopulationBetweenInDepartment(@PathVariable int departmentId,
                                                               @PathVariable int min,
                                                               @PathVariable int max) throws CityException {
        return cityService.searchByPopulationBetweenInDepartment(departmentId, min, max);
    }

    @PostMapping
    public ResponseEntity<List<CityDto>> addCity(@Valid @RequestBody CityDto city, BindingResult result) throws CityException {

        if (result.hasErrors()){
            throw new CityException(result.getFieldErrors().getFirst().getDefaultMessage());
        }

        List<CityDto> cities = cityService.addCity(city);
        return ResponseEntity.ok(cities);
    }

    @PutMapping("/{id}")
    public ResponseEntity<List<CityDto>> updateCity(@PathVariable int id, @Valid @RequestBody CityDto city, BindingResult result) throws CityException {

        if (result.hasErrors()) {
            throw new CityException(result.getFieldErrors().getFirst().getDefaultMessage());
        }

        List<CityDto> cities = cityService.updateCity(id, city);
        return ResponseEntity.ok(cities);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<CityDto>> deleteCity(@PathVariable int id) throws CityException {

        List<CityDto> cities = cityService.deleteCity(id);
        return ResponseEntity.ok(cities);
    }
}
