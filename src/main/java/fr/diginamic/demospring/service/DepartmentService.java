package fr.diginamic.demospring.service;

import fr.diginamic.demospring.dao.DepartmentDao;
import fr.diginamic.demospring.exception.CityException;
import fr.diginamic.demospring.model.Department;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    private final DepartmentDao departmentDao;

    public DepartmentService(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    public List<Department> getDepartments() {
        return departmentDao.findAll();
    }

    public Optional<Department> getDepartmentById(int id) {
        return departmentDao.findById(id);
    }

    @Transactional
    public List<Department> addDepartment(Department department) throws CityException {

        if (departmentDao.existsByCode(department.getCode())) {
            throw new CityException("The department '" + department.getCode() + "' already exists.");
        }

        departmentDao.save(department);
        return departmentDao.findAll();
    }

    @Transactional
    public List<Department> updateDepartment(int id, Department newData) throws CityException {

        Optional<Department> existing = departmentDao.findById(id);

        if (existing.isEmpty()) {
            throw new CityException("Department with id " + id + " not found");
        }

        Department department = existing.get();
        department.setCode(newData.getCode());
        department.setName(newData.getName());
        return departmentDao.findAll();
    }

    @Transactional
    public List<Department> deleteDepartment(int id) throws CityException {

        boolean deleted = departmentDao.deleteById(id);

        if (!deleted) {
            throw new CityException("Department with id " + id + " not found");
        }

        return departmentDao.findAll();
    }

    @Transactional
    public Department resolve(Integer departmentId, String departmentCode) throws CityException {

        if (departmentId != null) {
            Optional<Department> byId = departmentDao.findById(departmentId);
            if (byId.isPresent()) {
                return byId.get();
            }
        }

        if (departmentCode != null && !departmentCode.isBlank()) {
            Optional<Department> byCode = departmentDao.findByCode(departmentCode);
            if (byCode.isPresent()) {
                return byCode.get();
            }

            Department department = new Department();
            department.setCode(departmentCode);
            departmentDao.save(department);
            return department;
        }

        throw new CityException("Unknown department.");
    }
}
