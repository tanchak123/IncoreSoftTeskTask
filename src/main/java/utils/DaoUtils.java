package utils;

import com.google.inject.Inject;
import service.DepartmentService;
import service.EmployeeService;

public class DaoUtils {
    @Inject
    public static DepartmentService departmentService;

    @Inject
    public static EmployeeService employeeService;

}