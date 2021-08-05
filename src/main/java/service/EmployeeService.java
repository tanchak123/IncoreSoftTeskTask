package service;

import dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {
    EmployeeDto create(EmployeeDto department);

    Boolean update(EmployeeDto department);

    Boolean delete(Long id);

    List<EmployeeDto> getAll();

    List<EmployeeDto> getAllInsteadOf(List<EmployeeDto> employeeList);

    EmployeeDto get(Long id);

    void installDepartments(EmployeeDto employee);
}
