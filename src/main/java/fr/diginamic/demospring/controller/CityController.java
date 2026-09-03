package fr.diginamic.demospring.controller;

import fr.diginamic.demospring.dto.CityDto;
import fr.diginamic.demospring.exception.FunctionalException;
import fr.diginamic.demospring.exception.NotFoundException;
import fr.diginamic.demospring.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for cities, exposed under {@code /cities}.
 *
 * <p>All payloads are {@link CityDto}. Search endpoints return the matching
 * cities as a plain list (empty when nothing matched); only lookups by id or
 * exact name answer {@code 404} when the resource is absent. Validation
 * failures and business errors are turned into
 * {@link fr.diginamic.demospring.exception.ApiError} responses by
 * {@link fr.diginamic.demospring.exception.GlobalExceptionHandler}.</p>
 */
@RestController
@RequestMapping("/cities")
@Validated
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
     * @param page zero-based page index
     * @param size page size (at least 1)
     * @return the requested page of cities
     */
    @GetMapping
    @Operation(summary = "List all cities (paginated)")
    public Page<CityDto> getCities(@RequestParam(defaultValue = "0") @Min(0) int page,
                                   @RequestParam(defaultValue = "20") @Min(1) int size) {
        return cityService.getCities(PageRequest.of(page, size));
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
    public CityDto getCityById(@Parameter(description = "City id") @PathVariable @Positive int id) throws NotFoundException {
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
     * @return cities whose name starts with {@code prefix} (empty list if none)
     */
    @GetMapping("/search/startswith/{prefix}")
    @Operation(summary = "List cities whose name starts with a prefix")
    @ApiResponse(responseCode = "200", description = "Matching cities (empty list if none matched)")
    public List<CityDto> searchByNameStartingWith(@Parameter(description = "Name prefix") @PathVariable String prefix) {
        return cityService.searchByNameStartingWith(prefix);
    }

    /**
     * @param min exclusive lower bound on population
     * @return cities more populated than {@code min} (empty list if none)
     */
    @GetMapping("/search/population/greater/{min}")
    @Operation(summary = "List cities with a population greater than a threshold")
    @ApiResponse(responseCode = "200", description = "Matching cities (empty list if none matched)")
    public List<CityDto> searchByPopulationGreaterThan(@Parameter(description = "Exclusive lower bound") @PathVariable @PositiveOrZero int min) {
        return cityService.searchByPopulationGreaterThan(min);
    }

    /**
     * @param min exclusive lower bound on population
     * @param max exclusive upper bound on population
     * @return cities whose population lies strictly between the bounds
     *         (empty list if none)
     */
    @GetMapping("/search/population/between/{min}/{max}")
    @Operation(summary = "List cities with a population within a range")
    @ApiResponse(responseCode = "200", description = "Matching cities (empty list if none matched)")
    public List<CityDto> searchByPopulationBetween(@Parameter(description = "Exclusive lower bound") @PathVariable @PositiveOrZero int min,
                                                   @Parameter(description = "Exclusive upper bound") @PathVariable @PositiveOrZero int max) {
        return cityService.searchByPopulationBetween(min, max);
    }

    /**
     * @param departmentId department id
     * @param n            maximum number of cities to return (at least 1)
     * @return up to {@code n} cities of the department, most populated first
     *         (empty list if the department has no city)
     */
    @GetMapping("/search/department/{departmentId}/largest/{n}")
    @Operation(summary = "List the N most populated cities of a department")
    @ApiResponse(responseCode = "200", description = "Matching cities (empty list if the department has no city)")
    public List<CityDto> getLargestCitiesOfDepartment(@Parameter(description = "Department id") @PathVariable @Positive int departmentId,
                                                      @Parameter(description = "Maximum number of cities") @PathVariable @Positive int n) {
        return cityService.getLargestCitiesOfDepartment(departmentId, n);
    }

    /**
     * @param departmentId department id
     * @param min          exclusive lower bound on population
     * @param max          exclusive upper bound on population
     * @return the matching cities of the department (empty list if none)
     */
    @GetMapping("/search/department/{departmentId}/population/between/{min}/{max}")
    @Operation(summary = "List cities of a department with a population within a range")
    @ApiResponse(responseCode = "200", description = "Matching cities (empty list if none matched)")
    public List<CityDto> searchByPopulationBetweenInDepartment(@Parameter(description = "Department id") @PathVariable @Positive int departmentId,
                                                               @Parameter(description = "Exclusive lower bound") @PathVariable @PositiveOrZero int min,
                                                               @Parameter(description = "Exclusive upper bound") @PathVariable @PositiveOrZero int max) {
        return cityService.searchByPopulationBetweenInDepartment(departmentId, min, max);
    }

    /**
     * @param departmentId department id
     * @param min          exclusive lower bound on population
     * @return the cities of the department more populated than {@code min},
     *         ordered by descending population (empty list if none)
     */
    @GetMapping("/search/department/{departmentId}/population/greater/{min}")
    @Operation(summary = "List cities of a department with a population greater than a threshold")
    @ApiResponse(responseCode = "200", description = "Matching cities (empty list if none matched)")
    public List<CityDto> searchByPopulationGreaterThanInDepartment(@Parameter(description = "Department id") @PathVariable @Positive int departmentId,
                                                                   @Parameter(description = "Exclusive lower bound") @PathVariable @PositiveOrZero int min) {
        return cityService.searchByPopulationGreaterThanInDepartment(departmentId, min);
    }

    /**
     * Creates a city.
     *
     * @param city the city to create; a department id or code is mandatory
     * @return the created city
     * @throws FunctionalException if the department cannot be resolved or the city already exists
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a city")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "City created"),
            @ApiResponse(responseCode = "400", description = "Invalid payload, unresolved department or duplicate city")
    })
    public CityDto addCity(@Valid @RequestBody CityDto city) throws FunctionalException {
        return cityService.addCity(city);
    }

    /**
     * Updates a city.
     *
     * @param id   id of the city to update
     * @param city new values; a department id or code is mandatory
     * @return the updated city
     * @throws FunctionalException     if the department cannot be resolved or the name clashes
     * @throws NotFoundException if no city has this id
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a city")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "City updated"),
            @ApiResponse(responseCode = "400", description = "Invalid payload, unresolved department or duplicate city"),
            @ApiResponse(responseCode = "404", description = "No city with this id")
    })
    public CityDto updateCity(@Parameter(description = "City id") @PathVariable @Positive int id,
                              @Valid @RequestBody CityDto city)
            throws FunctionalException, NotFoundException {
        return cityService.updateCity(id, city);
    }

    /**
     * Deletes a city.
     *
     * @param id id of the city to delete
     * @throws NotFoundException if no city has this id
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a city")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "City deleted"),
            @ApiResponse(responseCode = "404", description = "No city with this id")
    })
    public void deleteCity(@Parameter(description = "City id") @PathVariable @Positive int id) throws NotFoundException {
        cityService.deleteCity(id);
    }
}
