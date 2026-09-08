package fr.diginamic.demospring.service;

import fr.diginamic.demospring.dto.DepartmentDto;
import fr.diginamic.demospring.exception.FunctionalException;
import fr.diginamic.demospring.exception.NotFoundException;
import fr.diginamic.demospring.model.Department;
import fr.diginamic.demospring.repository.DepartmentRepository;
import fr.diginamic.demospring.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Business logic for departments.
 *
 * <p>CRUD methods accept and return {@link DepartmentDto}. {@link #resolve} is the
 * exception: it returns the managed {@link Department} entity because
 * {@link CityService} needs it to set the foreign key on a city.</p>
 *
 * <p>The class is read-only by default; the mutating methods opt back into a
 * writable transaction with their own {@link Transactional} annotation.</p>
 */
@Service
@Transactional(readOnly = true)
public class DepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentService.class);

    private final DepartmentRepository departmentRepository;

    /**
     * @param departmentRepository department repository
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
     * @return the created department
     * @throws FunctionalException if a department with the same code already exists
     */
    @Transactional
    public DepartmentDto addDepartment(DepartmentDto department) throws FunctionalException {

        if (departmentRepository.existsByCodeIgnoreCase(department.getCode())) {
            throw new FunctionalException("The department '" + department.getCode() + "' already exists.");
        }

        Department entity = department.toEntity();
        entity.setUserUpdate(SecurityUtils.getCurrentUsername());
        entity.setDateUpdate(LocalDateTime.now());

        Department saved = departmentRepository.save(entity);

        logger.info("User '{}' created department '{}' (id={})",
                SecurityUtils.getCurrentUsername(), saved.getCode(), saved.getId());

        return DepartmentDto.fromEntity(saved);
    }

    /**
     * Updates an existing department (code and name).
     *
     * @param id      id of the department to update
     * @param newData new values
     * @return the updated department
     * @throws NotFoundException if no department has the given id
     */
    @Transactional
    public DepartmentDto updateDepartment(int id, DepartmentDto newData) throws NotFoundException {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department with id " + id + " not found"));

        department.setCode(newData.getCode());
        department.setName(newData.getName());
        department.setUserUpdate(SecurityUtils.getCurrentUsername());
        department.setDateUpdate(LocalDateTime.now());

        Department saved = departmentRepository.save(department);

        logger.info("User '{}' updated department '{}' (id={})",
                SecurityUtils.getCurrentUsername(), saved.getCode(), saved.getId());

        return DepartmentDto.fromEntity(saved);
    }

    /**
     * Deletes a department by id.
     *
     * @param id id of the department to delete
     * @throws NotFoundException if no department has the given id
     */
    @Transactional
    public void deleteDepartment(int id) throws NotFoundException {

        if (!departmentRepository.existsById(id)) {
            throw new NotFoundException("Department with id " + id + " not found");
        }

        departmentRepository.deleteById(id);

        logger.info("User '{}' deleted department (id={})", SecurityUtils.getCurrentUsername(), id);
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
     *   <li>otherwise a {@link FunctionalException} is thrown.</li>
     * </ol>
     *
     * @param departmentId   candidate department id, may be {@code null}
     * @param departmentCode candidate department code, may be {@code null} or blank
     * @return the resolved (possibly newly created) managed entity
     * @throws FunctionalException if neither a known id nor a code is available
     */
    // @Transactional: needed here — when called from a read-only context this
    // flips the transaction to read-write so step 3 (department creation) can persist.
    @Transactional
    public Department resolve(Integer departmentId, String departmentCode) throws FunctionalException {

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

            // TP requirement: an unknown code is not an error — create the
            // department on the fly and attach the city to it.
            Department department = new Department(departmentCode, null);
            department.setUserUpdate(SecurityUtils.getCurrentUsername());
            department.setDateUpdate(LocalDateTime.now());
            departmentRepository.save(department);

            logger.info("User '{}' auto-created department '{}' (id={}) while resolving a city",
                    SecurityUtils.getCurrentUsername(), department.getCode(), department.getId());

            return department;
        }

        throw new FunctionalException("Unknown department.");
    }
}
