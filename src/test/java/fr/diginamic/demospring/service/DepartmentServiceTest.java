package fr.diginamic.demospring.service;

import fr.diginamic.demospring.dto.DepartmentDto;
import fr.diginamic.demospring.exception.FunctionalException;
import fr.diginamic.demospring.exception.NotFoundException;
import fr.diginamic.demospring.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    /** GET — read a department seeded by data.sql. */
    @Test
    public void getDepartmentById() {

        Optional<DepartmentDto> result = departmentService.getDepartmentById(1);

        assertTrue(result.isPresent());
        assertEquals("69", result.get().getCode());
        assertEquals("Rhône", result.get().getName());
    }

    /** POST — create a department, and reject a duplicate code. */
    @Test
    public void addDepartment() throws FunctionalException {

        DepartmentDto dto = new DepartmentDto();
        dto.setCode("34");
        dto.setName("Hérault");

        DepartmentDto created = departmentService.addDepartment(dto);

        assertNotNull(created.getId());
        assertEquals(5, departmentService.getDepartments().size());

        // same code again -> functional error
        DepartmentDto duplicate = new DepartmentDto();
        duplicate.setCode("34");
        duplicate.setName("Doublon");
        assertThrows(FunctionalException.class, () -> departmentService.addDepartment(duplicate));
    }

    /** PUT — update code and name of an existing department. */
    @Test
    public void updateDepartment() throws NotFoundException {

        DepartmentDto newData = new DepartmentDto();
        newData.setCode("01D");
        newData.setName("Ain modifié");

        DepartmentDto updated = departmentService.updateDepartment(2, newData);

        assertEquals("01D", updated.getCode());
        assertEquals("Ain modifié", updated.getName());
        assertEquals("01D", departmentService.getDepartmentById(2).orElseThrow().getCode());

        // unknown id -> not found
        assertThrows(NotFoundException.class, () -> departmentService.updateDepartment(999, newData));
    }


    /** DELETE — remove a department, and reject an unknown id. */
    @Test
    public void deleteDepartment() throws NotFoundException {

        departmentService.deleteDepartment(3);

        assertTrue(departmentService.getDepartmentById(3).isEmpty());
        assertEquals(3, departmentService.getDepartments().size());

        assertThrows(NotFoundException.class, () -> departmentService.deleteDepartment(999));
    }
}
