package services;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dto.DepartmentDto;
import dto.EmployeeDto;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import service.DepartmentService;
import service.EmployeeService;
import utils.Binder;

public class AbstractClass {
    private static Injector injector = Guice.createInjector(new Binder());
    protected static EmployeeService employeeService = injector.getInstance(EmployeeService.class);
    protected static DepartmentService departmentService = injector.getInstance(DepartmentService.class);
    protected static long employeeId;
    protected static long departmentId;

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
}
