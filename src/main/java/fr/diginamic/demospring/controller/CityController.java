package fr.diginamic.demospring.controller;

import fr.diginamic.demospring.dto.CityDto;
import fr.diginamic.demospring.exception.CityException;
import fr.diginamic.demospring.exception.NotFoundException;
import fr.diginamic.demospring.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for cities, exposed under {@code /cities}.
 *
 * <p>All payloads are {@link CityDto}. Validation failures and business errors
 * are turned into HTTP responses by
 * {@link fr.diginamic.demospring.exception.GlobalExceptionHandler}.</p>
 */
@RestController
@RequestMapping("/cities")
@Tag(name = "Cities", description = "Read, search and manage cities")
public class CityController {

    private final CityService cityService;

    /**
     * @param cityService city business service
     */
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    /**
     * @return every city
     */
    @GetMapping
    @Operation(summary = "List all cities")
    public List<CityDto> getCities() {
        return cityService.getCities();
    }

    /**
     * @param id city id
     * @return the matching city
     * @throws NotFoundException if no city has this id
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a city by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "City found"),
            @ApiResponse(responseCode = "404", description = "No city with this id")
    })
    public CityDto getCityById(@Parameter(description = "City id") @PathVariable int id) throws NotFoundException {
        return cityService.getCityById(id)
                .orElseThrow(() -> new NotFoundException("City with id " + id + " not found"));
    }

    /**
     * @param name exact city name (case-insensitive)
     * @return the matching city
     * @throws NotFoundException if no city has this name
     */
    @GetMapping("/search/name/{name}")
    @Operation(summary = "Get a city by exact name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "City found"),
            @ApiResponse(responseCode = "404", description = "No city with this name")
    })
    public CityDto getCityByName(@Parameter(description = "Exact city name") @PathVariable String name) throws NotFoundException {
        return cityService.getCityByName(name)
                .orElseThrow(() -> new NotFoundException("City '" + name + "' not found"));
    }

    /**
     * @param prefix case-insensitive name prefix
     * @return cities whose name starts with {@code prefix}
     * @throws CityException if no city matches
     */
    @GetMapping("/search/startswith/{prefix}")
    @Operation(summary = "List cities whose name starts with a prefix")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "At least one city matched"),
            @ApiResponse(responseCode = "400", description = "No city matched")
    })
    public List<CityDto> searchByNameStartingWith(@Parameter(description = "Name prefix") @PathVariable String prefix) throws CityException {
        return cityService.searchByNameStartingWith(prefix);
    }

    /**
     * @param min exclusive lower bound on population
     * @return cities more populated than {@code min}
     * @throws CityException if no city matches
     */
    @GetMapping("/search/population/greater/{min}")
    @Operation(summary = "List cities with a population greater than a threshold")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "At least one city matched"),
            @ApiResponse(responseCode = "400", description = "No city matched")
    })
    public List<CityDto> searchByPopulationGreaterThan(@Parameter(description = "Exclusive lower bound") @PathVariable int min) throws CityException {
        return cityService.searchByPopulationGreaterThan(min);
    }

    /**
     * @param min exclusive lower bound on population
     * @param max exclusive upper bound on population
     * @return cities whose population lies strictly between the bounds
     * @throws CityException if no city matches
     */
    @GetMapping("/search/population/between/{min}/{max}")
    @Operation(summary = "List cities with a population within a range")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "At least one city matched"),
            @ApiResponse(responseCode = "400", description = "No city matched")
    })
    public List<CityDto> searchByPopulationBetween(@Parameter(description = "Exclusive lower bound") @PathVariable int min,
                                                   @Parameter(description = "Exclusive upper bound") @PathVariable int max) throws CityException {
        return cityService.searchByPopulationBetween(min, max);
    }

    /**
     * @param departmentId department id
     * @param n            maximum number of cities to return
     * @return up to {@code n} cities of the department, most populated first
     * @throws CityException if the department has no city
     */
    @GetMapping("/search/department/{departmentId}/largest/{n}")
    @Operation(summary = "List the N most populated cities of a department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "At least one city matched"),
            @ApiResponse(responseCode = "400", description = "The department has no city")
    })
    public List<CityDto> getLargestCitiesOfDepartment(@Parameter(description = "Department id") @PathVariable int departmentId,
                                                      @Parameter(description = "Maximum number of cities") @PathVariable int n) throws CityException {
        return cityService.getLargestCitiesOfDepartment(departmentId, n);
    }

    /**
     * @param departmentId department id
     * @param min          exclusive lower bound on population
     * @param max          exclusive upper bound on population
     * @return the matching cities of the department
     * @throws CityException if no city matches
     */
    @GetMapping("/search/department/{departmentId}/population/between/{min}/{max}")
    @Operation(summary = "List cities of a department with a population within a range")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "At least one city matched"),
            @ApiResponse(responseCode = "400", description = "No city matched")
    })
    public List<CityDto> searchByPopulationBetweenInDepartment(@Parameter(description = "Department id") @PathVariable int departmentId,
                                                               @Parameter(description = "Exclusive lower bound") @PathVariable int min,
                                                               @Parameter(description = "Exclusive upper bound") @PathVariable int max) throws CityException {
        return cityService.searchByPopulationBetweenInDepartment(departmentId, min, max);
    }

    /**
     * Creates a city.
     *
     * @param city the city to create; a department id or code is mandatory
     * @return the full list of cities after insertion
     * @throws CityException if the department cannot be resolved or the city already exists
     */
    @PostMapping
    @Operation(summary = "Create a city")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "City created"),
            @ApiResponse(responseCode = "400", description = "Invalid payload, unresolved department or duplicate city")
    })
    public ResponseEntity<List<CityDto>> addCity(@Valid @RequestBody CityDto city) throws CityException {
        return ResponseEntity.ok(cityService.addCity(city));
    }

    /**
     * Updates a city.
     *
     * @param id   id of the city to update
     * @param city new values; a department id or code is mandatory
     * @return the full list of cities after the update
     * @throws CityException     if the department cannot be resolved or the name clashes
     * @throws NotFoundException if no city has this id
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a city")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "City updated"),
            @ApiResponse(responseCode = "400", description = "Invalid payload, unresolved department or duplicate city"),
            @ApiResponse(responseCode = "404", description = "No city with this id")
    })
    public ResponseEntity<List<CityDto>> updateCity(@Parameter(description = "City id") @PathVariable int id,
                                                    @Valid @RequestBody CityDto city)
            throws CityException, NotFoundException {
        return ResponseEntity.ok(cityService.updateCity(id, city));
    }

    /**
     * Deletes a city.
     *
     * @param id id of the city to delete
     * @return the full list of cities after deletion
     * @throws NotFoundException if no city has this id
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a city")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "City deleted"),
            @ApiResponse(responseCode = "404", description = "No city with this id")
    })
    public ResponseEntity<List<CityDto>> deleteCity(@Parameter(description = "City id") @PathVariable int id) throws NotFoundException {
        return ResponseEntity.ok(cityService.deleteCity(id));
    }
}
