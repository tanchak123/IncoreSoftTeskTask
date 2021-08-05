package services;

import dto.DepartmentDto;
import dto.EmployeeDto;
import org.junit.*;
import java.util.List;


public class DepartmentServiceTest extends AbstractClass {

    @BeforeClass
    public static void insertData() {
        DepartmentDto department = new DepartmentDto();
        department.setName("department test:");
        EmployeeDto employee = new EmployeeDto();
        employee.setName("employee test:");
        employee.setEmail("employee");
        employee.setExperienceYears(5);
        employeeId = employeeService.create(employee).getId();
        department.getEmployeeList().add(employee);
        departmentId = departmentService.create(department).getId();
    }

    @AfterClass
    public static void deleteData() {
        departmentService.delete(departmentId);
        employeeService.delete(employeeId);
    }

    @Test
    public void departmentServiceGetAllTest() {
        List<DepartmentDto> allDepartments = departmentService.getAll();
        DepartmentDto department = allDepartments.get(0);
        Assert.assertNotNull(department.getId());
        Assert.assertNotNull(department.getCreateDate());
        Assert.assertNotNull(department.getName());
        Assert.assertEquals(department.getEmployeeList().size(), 0);
        Assert.assertNotEquals(allDepartments.size(), 0);
    }

    @Test
    public void departmentServiceCreateTest() {
        DepartmentDto department = new DepartmentDto();
        department.setName("department create test:");
        departmentService.create(department);
        Assert.assertNotNull(department.getId());
        Assert.assertNotNull(department.getCreateDate());
        departmentService.delete(department.getId());
    }

    @Test
    public void departmentServiceGetTest() {
        DepartmentDto departmentDto = departmentService.get(departmentId);
        Assert.assertTrue(departmentDto.getEmployeeList().size() > 0);
        Assert.assertNotNull(departmentDto.getEmployeeList().get(0).getId());
        Assert.assertNull(departmentDto.getEmployeeList().get(0).getExperienceYears());
    }

    @Test
    public void departmentServiceUpdateAndDeleteTest() {
        DepartmentDto departmentDto = departmentService.get(departmentId);
        departmentDto.setName("update");
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setEmail("update@gamil.com");
        employeeDto.setName("update");
        employeeDto.setExperienceYears(14);
        employeeService.create(employeeDto);
        Assert.assertNotNull(employeeDto.getId());
        departmentDto.getEmployeeList().add(employeeDto);
        departmentService.update(departmentDto);
        DepartmentDto departmentAfterUpdate = departmentService.get(departmentDto.getId());
        Assert.assertEquals(departmentAfterUpdate.getName(), "update");
        Assert.assertNotNull(departmentAfterUpdate.getEmployeeList()
                .stream()
                .filter(employee -> employee.getId().equals(employeeDto.getId())).findAny().get());
        Assert.assertTrue(employeeService.delete(employeeDto.getId()));
    }
}
