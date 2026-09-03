package fr.diginamic.demospring.service;

import fr.diginamic.demospring.dto.DepartmentDto;
import fr.diginamic.demospring.exception.CityException;
import fr.diginamic.demospring.exception.NotFoundException;
import fr.diginamic.demospring.model.Department;
import fr.diginamic.demospring.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Business logic for departments.
 *
 * <p>CRUD methods accept and return {@link DepartmentDto}. {@link #resolve} is the
 * exception: it returns the managed {@link Department} entity because
 * {@link CityService} needs it to set the foreign key on a city.</p>
 */
@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    /**
     * @param departmentRepository department data access object
     */
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    /** @return every department */
    public List<DepartmentDto> getDepartments() {
        return departmentRepository.findAll().stream().map(DepartmentDto::fromEntity).toList();
    }

    /**
     * @param id department id
     * @return the department, or {@link Optional#empty()} if not found
     */
    public Optional<DepartmentDto> getDepartmentById(int id) {
        return departmentRepository.findById(id).map(DepartmentDto::fromEntity);
    }

    /**
     * Creates a department.
     *
     * @param department the department to create
     * @return the full list of departments after insertion
     * @throws CityException if a department with the same code already exists
     */
    @Transactional
    public List<DepartmentDto> addDepartment(DepartmentDto department) throws CityException {

        if (departmentRepository.existsByCodeIgnoreCase(department.getCode())) {
            throw new CityException("The department '" + department.getCode() + "' already exists.");
        }

        departmentRepository.save(department.toEntity());
        return departmentRepository.findAll().stream().map(DepartmentDto::fromEntity).toList();
    }

    /**
     * Updates an existing department (code and name).
     *
     * @param id      id of the department to update
     * @param newData new values
     * @return the full list of departments after the update
     * @throws NotFoundException if no department has the given id
     */
    @Transactional
    public List<DepartmentDto> updateDepartment(int id, DepartmentDto newData) throws NotFoundException {

        Optional<Department> existing = departmentRepository.findById(id);

        if (existing.isEmpty()) {
            throw new NotFoundException("Department with id " + id + " not found");
        }

        Department department = existing.get();
        department.setCode(newData.getCode());
        department.setName(newData.getName());
        return departmentRepository.findAll().stream().map(DepartmentDto::fromEntity).toList();
    }

    /**
     * Deletes a department by id.
     *
     * @param id id of the department to delete
     * @return the full list of departments after deletion
     * @throws NotFoundException if no department has the given id
     */
    @Transactional
    public List<DepartmentDto> deleteDepartment(int id) throws NotFoundException {

        if (!departmentRepository.existsById(id)) {
            throw new NotFoundException("Department with id " + id + " not found");
        }

        departmentRepository.deleteById(id);
        return departmentRepository.findAll().stream().map(DepartmentDto::fromEntity).toList();
    }

    /**
     * Resolves the {@link Department} entity a city must be attached to.
     *
     * <p>Resolution order:</p>
     * <ol>
     *   <li>by {@code departmentId} when provided and known;</li>
     *   <li>by {@code departmentCode} when provided and known;</li>
     *   <li>otherwise, when only an <em>unknown</em> code is given, a new
     *       department is created with that code;</li>
     *   <li>otherwise a {@link CityException} is thrown.</li>
     * </ol>
     *
     * @param departmentId candidate department id, may be {@code null}
     * @param departmentCode candidate department code, may be {@code null} or blank
     * @return the resolved (possibly newly created) managed entity
     * @throws CityException if neither a known id nor a code is available
     */
    @Transactional
    public Department resolve(Integer departmentId, String departmentCode) throws CityException {

        if (departmentId != null) {
            Optional<Department> byId = departmentRepository.findById(departmentId);
            if (byId.isPresent()) {
                return byId.get();
            }
        }

        if (departmentCode != null && !departmentCode.isBlank()) {
            Optional<Department> byCode = departmentRepository.findByCodeIgnoreCase(departmentCode);
            if (byCode.isPresent()) {
                return byCode.get();
            }

            Department department = new Department();
            department.setCode(departmentCode);
            departmentRepository.save(department);
            return department;
        }

        throw new CityException("Unknown department.");
    }
}
