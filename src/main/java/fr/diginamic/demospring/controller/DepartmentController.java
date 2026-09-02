package fr.diginamic.demospring.controller;

import fr.diginamic.demospring.exception.CityException;
import fr.diginamic.demospring.model.Department;
import fr.diginamic.demospring.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public List<Department> getDepartments() {
        return departmentService.getDepartments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable int id) {

        Optional<Department> department = departmentService.getDepartmentById(id);

        return department.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<List<Department>> addDepartment(@Valid @RequestBody Department department, BindingResult result) throws CityException {

        if (result.hasErrors()) {
            throw new CityException(result.getFieldErrors().getFirst().getDefaultMessage());
        }

        List<Department> departments = departmentService.addDepartment(department);
        return ResponseEntity.ok(departments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<List<Department>> updateDepartment(@PathVariable int id, @Valid @RequestBody Department department, BindingResult result) throws CityException {

        if (result.hasErrors()) {
            throw new CityException(result.getFieldErrors().getFirst().getDefaultMessage());
        }

        List<Department> departments = departmentService.updateDepartment(id, department);
        return ResponseEntity.ok(departments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<Department>> deleteDepartment(@PathVariable int id) throws CityException {

        List<Department> departments = departmentService.deleteDepartment(id);
        return ResponseEntity.ok(departments);
    }
}
