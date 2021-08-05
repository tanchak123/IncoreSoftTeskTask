package services;

import dto.DepartmentDto;
import dto.EmployeeDto;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class EmployeeServiceTest extends AbstractClass{

    @Test
    public void employeeServiceGetAllTest() {
        List<EmployeeDto> allEmployees = employeeService.getAll();
        EmployeeDto employee = allEmployees.get(0);
        Assert.assertNotNull(employee.getId());
        Assert.assertNotNull(employee.getCreateDate());
        Assert.assertNotNull(employee.getName());
        Assert.assertEquals(employee.getDepartmentList().size(), 0);
        Assert.assertNotEquals(allEmployees.size(), 0);
    }

    @Test
    public void employeeServiceCreateTest() {
        EmployeeDto employee = new EmployeeDto();
        employee.setName("employee create test:");
        employee.setEmail("testEmployee@gamil.com");
        employee.setExperienceYears(23);
        employeeService.create(employee);
        Assert.assertNotNull(employee.getId());
        Assert.assertNotNull(employee.getCreateDate());
        employeeService.delete(employee.getId());
    }

    @Test
    public void employeeServiceGetTest() {
        EmployeeDto employee = employeeService.get(employeeId);
        Assert.assertTrue(employee.getDepartmentList().size() > 0);
        Assert.assertNotNull(employee.getDepartmentList().get(0).getId());
        Assert.assertNull(employee.getDepartmentList().get(0).getName());
    }

    @Test
    public void employeeServiceUpdateAndDeleteTest() {
        EmployeeDto employee = employeeService.get(employeeId);
        employee.setName("update");
        employee.setEmail("testUpdateEm@gmail.com");
        employee.setExperienceYears(23);
        DepartmentDto department = new DepartmentDto();
        department.setName("update");
        departmentService.create(department);
        Assert.assertNotNull(department.getId());
        employee.getDepartmentList().add(department);
        employeeService.update(employee);
        EmployeeDto employeeAfterUpdate = employeeService.get(employee.getId());
        Assert.assertEquals(employeeAfterUpdate.getName(), "update");
        Assert.assertNotNull(employeeAfterUpdate.getDepartmentList()
                .stream()
                .filter(dep -> dep.getId().equals(department.getId())).findAny().get());
        Assert.assertTrue(departmentService.delete(department.getId()));
    }
}
