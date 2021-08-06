package controllers.employees;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import dto.DepartmentDto;
import dto.EmployeeDto;
import io.javalin.http.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static utils.DaoUtils.departmentService;
import static utils.DaoUtils.employeeService;

public class EmployeeController {

    private static List<EmployeeDto> employees = new ArrayList<>();
    private static final String ALL_OTHERS_DEPARTMENTS = "allOthersDepartments";
    private static final String EMPLOYEE = "employeeId";

    public static void getAll(Context context) {
        if (employees.isEmpty()) {
            employees = employeeService.getAll();
            Thread thread =  new Thread(() -> {
                try {
                    while (true) {
                        System.out.println("Update employees every minute");
                        Thread.sleep(60000);
                        employees = employeeService.getAll();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
        context.result(JSON.toJSONString(employees));
    }
    public static void get(Context context) {
        String id = context.pathParam("id");
        EmployeeDto employee = employeeService.get(Long.parseLong(id));
        if (employee == null) {
            context.result("No employee with id: " + context.pathParam("id"));
            return;
        }
        context.result(JSON.toJSONString(employee));
    }

    public static void getById(Context context) {
        String id = context.queryParam("id");
        String isUpdate = context.queryParam("isUpdate");
        EmployeeDto employeeDto;
        if (id != null) {
            employeeDto = employeeService.get(Long.parseLong(id));
            if (employeeDto != null) {
                Map<String, Object> model = new HashMap<>();
                model.put("employee", employeeDto);
                model.put("message", context.queryParam("message"));
                if (isUpdate == null) {
                    context.render("employees/get.html", model);
                } else {
                    context.render("employees/update.html", model);
                }
                return;
            }
        }
        getAllEmployees(context);
    }

    public static void getAllEmployees(Context context) {
        Map<String, Object> model = new HashMap<>();
        String page = context.queryParam("page");
        if (page == null) {
            page = "1";
        }
        model.put("page", Long.parseLong(page));
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL("http://localhost:7777/employees");
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (IOException malformedURLException) {
            malformedURLException.printStackTrace();
        }
        List<EmployeeDto> value = JSONObject.parseArray(content.toString(), EmployeeDto.class);
        value = value.stream().skip((Long.parseLong(page) * 10) - 10).limit(10).collect(Collectors.toList());
        model.put("employees", value);
        context.render("employees/getAll.html", model);
    }

    public static void update(Context context) {
        EmployeeDto employeeDto = new EmployeeDto();
        Map<String, Object> model = new HashMap<>();
        employeeDto.setId(Long.valueOf(context.formParam("id")));
        employeeDto.setEmail(context.formParam("email"));
        employeeDto.setName(context.formParam("name"));
        employeeDto.setCreateDate(LocalDate.parse(context.formParam("createDate")));
        String experienceYears = context.formParam("experienceYears");
        model.put("employee", employeeDto);
        if (experienceYears == null) {
            model.put("message", "Experience is null!");
            context.render("employees/update.html", model);
            return;
        }
        employeeDto.setExperienceYears(Integer.valueOf(experienceYears));
        if (Integer.parseInt(experienceYears) < 0 || Integer.parseInt(experienceYears) > 50) {
            model.put("message", "Experience must not be less than 0 or more than 50");
            context.render("employees/update.html", model);
            return;
        }
        employeeService.update(employeeDto);
        context.redirect("/employees/get?id=" + context.formParam("id"));
    }

    public static void delete(Context context) {
        Long id = Long.valueOf(context.formParam("id"));
        employeeService.delete(id);
        employees.removeIf(employeeDto -> employeeDto.getId().equals(id));
        getAllEmployees(context);
    }
    public static void create(Context context) {
        HashMap<String, Object> model = new HashMap<>();
        String name = context.formParam("name");
        String email = context.formParam("email");
        String exp = context.formParam("experienceYears");
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setName(name);
        employeeDto.setEmail(email);
        if (exp != null) {
            employeeDto.setExperienceYears(Integer.parseInt(exp));
            if (employeeDto.getExperienceYears() > 50 || employeeDto.getExperienceYears() < 0) {
                model.put("employee", employeeDto);
                model.put("message", "Experience must not be less than 0 or more than 50");
                context.render("/employees/create.html", model);
                return;
            }

        }
        if (context.method().equals("GET")) {
            model.put("employee", employeeDto);
            context.render("/employees/create.html", model);
            return;
        }
        employeeService.create(employeeDto);
        context.redirect("/employees/get?id=" + employeeDto.getId());
    }


    public static void getDepartments(Context context) {
        Map<String, Object> model = new HashMap<>();
        String page = context.queryParam("page");
        if (page == null || Long.parseLong(page) < 0) {
            page = "1";
        }
        model.put("page", Long.parseLong(page));
        EmployeeDto employeeDto = employeeService.get(Long.valueOf(context.queryParam("id")));
        employeeService.installDepartments(employeeDto);
        employeeDto.setDepartmentList(employeeDto.getDepartmentList()
                .stream().skip((Long.parseLong(page) * 10) - 10).limit(10).collect(Collectors.toList()));
        model.put("employee", employeeDto);
        context.render("/employees/getDepartments.html", model);
    }

    public static void remove(Context context) {
        Long employeeId = Long.valueOf(Objects.requireNonNull(context.formParam("employeeId"), "id of employee IS NULL"));
        Long departmentId = Long.valueOf(Objects.requireNonNull(context.formParam("departmentId"), "id of department is null"));
        EmployeeDto employee = employeeService.get(employeeId);
        employeeService.installDepartments(employee);
        employee.getDepartmentList().removeIf(departmentDto -> departmentDto.getId().equals(employeeId));
        employeeService.update(employee);
        Map<String, Object> model = new HashMap<>();
        model.put("employee", employee);
        model.put("page", 0);
        context.render("/departments/getEmployees.html", model);
    }

    public static void add(Context context) {
        Map<String, Object> model = new HashMap<>();
        if (context.method().equals("GET")) {
            Long departmentId = Long.valueOf(context.queryParam("employeeId"));
            EmployeeDto employeeDto = employeeService.get(departmentId);
            employeeService.installDepartments(employeeDto);
            List<DepartmentDto> allInsteadOf = departmentService.getAllInsteadOf(employeeDto.getDepartmentList());
            model.put("departments", allInsteadOf);
            model.put("employee", employeeDto);
            context.sessionAttribute(ALL_OTHERS_DEPARTMENTS, allInsteadOf);
            context.sessionAttribute(EMPLOYEE, employeeDto);
            context.render("/employees/add.html", model);
            return;
        }
        EmployeeDto employee = context.sessionAttribute(EMPLOYEE);
        if (context.formParam("isSubmit") != null) {
            employeeService.update(employee);
            model.put("employee", employee);
            model.put("page", 0);
            context.render("/employees/getDepartments.html", model);
            return;
        }
        List<DepartmentDto> allInsteadOf = context.sessionAttribute(ALL_OTHERS_DEPARTMENTS);
        Long employeeId = Long.valueOf(context.formParam("departmentId"));
        allInsteadOf.removeIf(employeeDto -> employeeDto.getId().equals(employeeId));
        DepartmentDto department = departmentService.get(employeeId);
        employee.getDepartmentList().add(department);
        model.put("departments", allInsteadOf);
        model.put("employee", employee);
        context.render("/employees/add.html", model);
    }
}
