package service;

import dto.DepartmentDto;

import java.util.List;

public interface DepartmentService {

    DepartmentDto create(DepartmentDto department);

    Boolean update(DepartmentDto department);

    Boolean delete(Long id);

    List<DepartmentDto> getAll();

    List<DepartmentDto> getAllInsteadOf(List<DepartmentDto> id);

    DepartmentDto get(Long id);

    void installEmployees(DepartmentDto employee);
}
