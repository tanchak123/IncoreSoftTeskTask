package service.impl;

import com.google.inject.Inject;
import dao.DepartmentDao;
import dto.DepartmentDto;
import dto.EmployeeDto;
import service.DepartmentService;
import service.EmployeeService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DepartmentServiceImpl implements DepartmentService {

    @Inject
    private DepartmentDao departmentDao;

    @Inject
    private EmployeeService employeeService;

    @Override
    public DepartmentDto create(DepartmentDto department) {
        return departmentDao.create(department);
    }

    @Override
    public Boolean update(DepartmentDto department) {
        if (department.getId() == null) {
            return false;
        }
        return departmentDao.update(department);
    }

    @Override
    public Boolean delete(Long id) {
        if (id == null) {
            return false;
        }
        return departmentDao.delete(id);
    }

    @Override
    public List<DepartmentDto> getAll() {
        return departmentDao.getAll();
    }

    @Override
    public List<DepartmentDto> getAllInsteadOf(List<DepartmentDto> departmentList) {
        if (departmentList.isEmpty()) {
            return getAll();
        }
        List<Long> ids = departmentList.stream().map(DepartmentDto::getId).collect(Collectors.toList());
        return getAll().stream().filter(department
                -> !ids.contains(department.getId())).collect(Collectors.toList());
    }

    @Override
    public DepartmentDto get(Long id) {
        if (id == null) {
            return null;
        }
        DepartmentDto department = departmentDao.get(id);
        return department.getId() == null ? null : department;
    }

    @Override
    public void installEmployees(DepartmentDto employee) {
        if (employee.getEmployeeList().isEmpty()) {
            return;
        }
        List<Long> ids = new ArrayList<>();
        employee.getEmployeeList().forEach(emp -> ids.add(emp.getId()));
        List<EmployeeDto> allEmployees = employeeService.getAll();
        employee.setEmployeeList(allEmployees
                .stream().filter(department
                        -> ids.contains(department.getId())).collect(Collectors.toList()));
    }
}
