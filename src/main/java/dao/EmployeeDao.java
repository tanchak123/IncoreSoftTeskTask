package dao;

import com.google.inject.ImplementedBy;
import dao.impl.EmployeeDaoImpl;
import dto.EmployeeDto;

import java.util.List;

@ImplementedBy(EmployeeDaoImpl.class)
public interface EmployeeDao {

    public EmployeeDto create(EmployeeDto department);

    public Boolean update(EmployeeDto department);

    public Boolean delete(Long id);

    public List<EmployeeDto> getAll();

    public EmployeeDto get(Long id);
}
