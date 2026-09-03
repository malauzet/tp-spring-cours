package fr.diginamic.demospring.controller;

import fr.diginamic.demospring.dto.DepartmentDto;
import fr.diginamic.demospring.exception.FunctionalException;
import fr.diginamic.demospring.exception.NotFoundException;
import fr.diginamic.demospring.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for departments, exposed under {@code /departments}.
 *
 * <p>All payloads are {@link DepartmentDto}. Errors are translated into
 * {@link fr.diginamic.demospring.exception.ApiError} responses by
 * {@link fr.diginamic.demospring.exception.GlobalExceptionHandler}.</p>
 */
@RestController
@RequestMapping("/departments")
@Validated
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
    public DepartmentDto getDepartmentById(@Parameter(description = "Department id") @PathVariable
            @Positive(message = "Department id must be a positive number.") int id) throws NotFoundException {
        return departmentService.getDepartmentById(id)
                .orElseThrow(() -> new NotFoundException("Department with id " + id + " not found"));
    }

    /**
     * Creates a department.
     *
     * @param department the department to create
     * @return the created department
     * @throws FunctionalException if a department with the same code already exists
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a department")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Department created"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or duplicate code")
    })
    public DepartmentDto addDepartment(@Valid @RequestBody DepartmentDto department) throws FunctionalException {
        return departmentService.addDepartment(department);
    }

    /**
     * Updates a department.
     *
     * @param id         id of the department to update
     * @param department new values
     * @return the updated department
     * @throws NotFoundException if no department has this id
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department updated"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "404", description = "No department with this id")
    })
    public DepartmentDto updateDepartment(@Parameter(description = "Department id") @PathVariable
            @Positive(message = "Department id must be a positive number.") int id,
                                          @Valid @RequestBody DepartmentDto department)
            throws NotFoundException {
        return departmentService.updateDepartment(id, department);
    }

    /**
     * Deletes a department.
     *
     * @param id id of the department to delete
     * @throws NotFoundException if no department has this id
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a department")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Department deleted"),
            @ApiResponse(responseCode = "404", description = "No department with this id")
    })
    public void deleteDepartment(@Parameter(description = "Department id") @PathVariable
            @Positive(message = "Department id must be a positive number.") int id) throws NotFoundException {
        departmentService.deleteDepartment(id);
    }
}
