package service.impl;

import com.google.inject.Inject;
import dao.EmployeeDao;
import dto.DepartmentDto;
import dto.EmployeeDto;
import service.DepartmentService;
import service.EmployeeService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeServiceImpl implements EmployeeService {

    @Inject
    private EmployeeDao employeeDao;

    @Inject
    private DepartmentService departmentService;

    @Override
    public EmployeeDto create(EmployeeDto department) {
        return employeeDao.create(department);
    }

    @Override
    public Boolean update(EmployeeDto department) {
        return employeeDao.update(department);
    }

    @Override
    public Boolean delete(Long id) {
        return employeeDao.delete(id);
    }

    @Override
    public List<EmployeeDto> getAll() {
        return employeeDao.getAll();
    }

    @Override
    public List<EmployeeDto> getAllInsteadOf(List<EmployeeDto> employeeList) {
        if (employeeList.isEmpty()) {
            return getAll();
        }
        List<Long> ids = employeeList.stream().map(EmployeeDto::getId).collect(Collectors.toList());
        return getAll().stream().filter(department
                -> !ids.contains(department.getId())).collect(Collectors.toList());

    }

    @Override
    public EmployeeDto get(Long id) {
        if (id == null) {
            return null;
        }
        return employeeDao.get(id);
    }

    @Override
    public void installDepartments(EmployeeDto employee) {
        if (employee.getDepartmentList().isEmpty()) {
            return;
        }
        List<Long> ids = new ArrayList<>();
        employee.getDepartmentList().forEach(department -> ids.add(department.getId()));
        List<DepartmentDto> allDepartments = departmentService.getAll();
        employee.setDepartmentList(allDepartments
                .stream().filter(department -> ids.contains(
                        department.getId())).collect(Collectors.toList()));
    }
}
