import dto.DepartmentDto;
import dto.EmployeeDto;
import io.javalin.http.Context;
import utils.DaoUtils;

import java.util.Arrays;
import java.util.List;

public class MainController {

    private MainController() {}

    public static void main(Context context) {
        List<EmployeeDto> all = DaoUtils.employeeService.getAll();
        if (all.stream().noneMatch(employee -> employee.getName().startsWith("newEmp"))) {
            for (int i = 0; i < 20; i++) {
                DepartmentDto department = new DepartmentDto();
                EmployeeDto employee = new EmployeeDto();
                department.setName("newDep" + i);
                employee.setName("newEmp" + i);
                employee.setEmail("new" + i + "@gmail.com");
                employee.setExperienceYears(12);
                department.getEmployeeList().add(employee);
                DaoUtils.departmentService.create(department);
            }
        }
        context.render("main.html");
    }


}
