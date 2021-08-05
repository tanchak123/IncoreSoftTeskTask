package dao;

import com.google.inject.ImplementedBy;
import dao.impl.DepartmentDaoImpl;
import dto.DepartmentDto;

import java.util.List;

@ImplementedBy(DepartmentDaoImpl.class)
public interface DepartmentDao {

    public DepartmentDto create(DepartmentDto department);

    public Boolean update(DepartmentDto department);

    public Boolean delete(Long id);

    public List<DepartmentDto> getAll();

    public DepartmentDto get(Long id);

}
