package fr.diginamic.demospring.controller;

import fr.diginamic.demospring.dto.DepartmentDto;
import fr.diginamic.demospring.exception.CityException;
import fr.diginamic.demospring.exception.NotFoundException;
import fr.diginamic.demospring.service.DepartmentService;
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
 * REST endpoints for departments, exposed under {@code /departments}.
 *
 * <p>All payloads are {@link DepartmentDto}. Errors are translated by
 * {@link fr.diginamic.demospring.exception.GlobalExceptionHandler}.</p>
 */
@RestController
@RequestMapping("/departments")
@Tag(name = "Departments", description = "Read and manage departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * @param departmentService department business service
     */
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * @return every department
     */
    @GetMapping
    @Operation(summary = "List all departments")
    public List<DepartmentDto> getDepartments() {
        return departmentService.getDepartments();
    }

    /**
     * @param id department id
     * @return the matching department
     * @throws NotFoundException if no department has this id
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a department by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department found"),
            @ApiResponse(responseCode = "404", description = "No department with this id")
    })
    public DepartmentDto getDepartmentById(@Parameter(description = "Department id") @PathVariable int id) throws NotFoundException {
        return departmentService.getDepartmentById(id)
                .orElseThrow(() -> new NotFoundException("Department with id " + id + " not found"));
    }

    /**
     * Creates a department.
     *
     * @param department the department to create
     * @return the full list of departments after insertion
     * @throws CityException if a department with the same code already exists
     */
    @PostMapping
    @Operation(summary = "Create a department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department created"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or duplicate code")
    })
    public ResponseEntity<List<DepartmentDto>> addDepartment(@Valid @RequestBody DepartmentDto department) throws CityException {
        return ResponseEntity.ok(departmentService.addDepartment(department));
    }

    /**
     * Updates a department.
     *
     * @param id         id of the department to update
     * @param department new values
     * @return the full list of departments after the update
     * @throws NotFoundException if no department has this id
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department updated"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "404", description = "No department with this id")
    })
    public ResponseEntity<List<DepartmentDto>> updateDepartment(@Parameter(description = "Department id") @PathVariable int id,
                                                               @Valid @RequestBody DepartmentDto department)
            throws NotFoundException {
        return ResponseEntity.ok(departmentService.updateDepartment(id, department));
    }

    /**
     * Deletes a department.
     *
     * @param id id of the department to delete
     * @return the full list of departments after deletion
     * @throws NotFoundException if no department has this id
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department deleted"),
            @ApiResponse(responseCode = "404", description = "No department with this id")
    })
    public ResponseEntity<List<DepartmentDto>> deleteDepartment(@Parameter(description = "Department id") @PathVariable int id) throws NotFoundException {
        return ResponseEntity.ok(departmentService.deleteDepartment(id));
    }
}
