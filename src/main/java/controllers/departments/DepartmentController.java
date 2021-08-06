package controllers.departments;

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
import java.util.*;
import java.util.stream.Collectors;

import static utils.DaoUtils.departmentService;
import static utils.DaoUtils.employeeService;

public class DepartmentController {

    private static List<DepartmentDto> departments = new ArrayList<>();
    private static final String ALL_OTHERS_EMPLOYEES = "allOthersEmployees";
    private static final String DEPARTMENT = "departmentId";

    public static void getAll(Context context) {
        if (departments.isEmpty()) {
            departments = departmentService.getAll();
            Thread thread =  new Thread(() -> {
                try {
                    while (true) {
                        System.out.println("Update departments every minute");
                        Thread.sleep(60000);
                        departments = departmentService.getAll();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
        context.result(JSON.toJSONString(departments));
    }
    public static void get(Context context) {
        String id = context.pathParam("id");
        DepartmentDto department = departmentService.get(Long.parseLong(id));
        if (department == null) {
            context.result("No Department with id: " + context.pathParam("id"));
            return;
        }
        context.result(JSON.toJSONString(department));
    }

    public static void getById(Context context) {
        String id = context.queryParam("id");
        String isUpdate = context.queryParam("isUpdate");
        DepartmentDto departmentDto;
        if (id != null) {
            departmentDto = departmentService.get(Long.parseLong(id));
            if (departmentDto != null) {
                Map<String, Object> model = new HashMap<>();
                model.put("department", departmentDto);
                model.put("message", context.queryParam("message"));
                if (isUpdate == null) {
                    context.render("departments/get.html", model);
                } else {
                    context.render("departments/update.html", model);
                }
                return;
            }
        }
        getAllDepartments(context);
    }

    public static void getAllDepartments(Context context) {
        Map<String, Object> model = new HashMap<>();
        String page = context.queryParam("page");
        if (page == null) {
            page = "1";
        }
        model.put("page", Long.parseLong(page));
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL("http://localhost:7777/departments");
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
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        List<DepartmentDto> value = JSONObject.parseArray(content.toString(), DepartmentDto.class);
//        try {
//            value = Arrays.asList(mapper.readValue(content.toString(), DepartmentDto[].class));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
        value = value.stream().skip((Long.parseLong(page) * 10) - 10).limit(10).collect(Collectors.toList());
        model.put("departments", value);
        context.render("departments/getAll.html", model);
    }

    public static void update(Context context) {
        DepartmentDto department = new DepartmentDto();
        department.setId(Long.valueOf(context.formParam("id")));
        department.setName(context.formParam("name"));
        departmentService.update(department);
        context.redirect("/departments/get?id=" + context.formParam("id"));
    }

    public static void delete(Context context) {
        Long id = Long.valueOf(context.formParam("id"));
        departmentService.delete(id);
        departments.removeIf(departmentDto -> departmentDto.getId().equals(id));
        getAllDepartments(context);
    }
    public static void create(Context context) {
        HashMap<String, Object> model = new HashMap<>();
        String name = context.formParam("name");
        DepartmentDto department = new DepartmentDto();
        department.setName(name);
        if (context.method().equals("GET")) {
            model.put("department", department);
            context.render("/departments/create.html", model);
            return;
        }
        departmentService.create(department);
        context.redirect("/departments/get?id=" + department.getId());
    }


    public static void getEmployees(Context context) {
        Map<String, Object> model = new HashMap<>();
        String page = context.queryParam("page");
        if (page == null || Long.parseLong(page) < 0) {
            page = "1";
        }
        model.put("page", Long.parseLong(page));
        DepartmentDto department = departmentService.get(Long.valueOf(context.queryParam("id")));
        departmentService.installEmployees(department);
        department.setEmployeeList(department.getEmployeeList()
                .stream().skip((Long.parseLong(page) * 10) - 10).limit(10).collect(Collectors.toList()));
        model.put("department", department);
        context.render("/departments/getEmployees.html", model);
    }

    public static void remove(Context context) {
        Long employeeId = Long.valueOf(Objects.requireNonNull(context.formParam("employeeId"), "id IS NULL"));
        Long departmentId = Long.valueOf(context.formParam("departmentId"));
        DepartmentDto department = departmentService.get(departmentId);
        departmentService.installEmployees(department);
        department.getEmployeeList().removeIf(employeeDto -> employeeDto.getId().equals(employeeId));
        departmentService.update(department);
        Map<String, Object> model = new HashMap<>();
        model.put("department", department);
        model.put("page", 0);
        context.render("/departments/getEmployees.html", model);
    }

    public static void add(Context context) {
        Map<String, Object> model = new HashMap<>();
        if (context.method().equals("GET")) {
            Long departmentId = Long.valueOf(context.queryParam("departmentId"));
            DepartmentDto departmentDto = departmentService.get(departmentId);
            departmentService.installEmployees(departmentDto);
            List<EmployeeDto> allInsteadOf = employeeService.getAllInsteadOf(departmentDto.getEmployeeList());
            model.put("employees", allInsteadOf);
            model.put("department", departmentDto);
            context.sessionAttribute(ALL_OTHERS_EMPLOYEES, allInsteadOf);
            context.sessionAttribute(DEPARTMENT, departmentDto);
            context.render("/departments/add.html", model);
            return;
        }
        DepartmentDto departmentDto = context.sessionAttribute(DEPARTMENT);
        if (context.formParam("isSubmit") != null) {
            departmentService.update(departmentDto);
            model.put("department", departmentDto);
            model.put("page", 0);
            context.render("/departments/getEmployees.html", model);
            return;
        }
        List<EmployeeDto> allInsteadOf = context.sessionAttribute(ALL_OTHERS_EMPLOYEES);
        Long employeeId = Long.valueOf(context.formParam("employeeId"));
        allInsteadOf.removeIf(employeeDto -> employeeDto.getId().equals(employeeId));
        EmployeeDto e = employeeService.get(employeeId);
        departmentDto.getEmployeeList().add(e);
        model.put("employees", allInsteadOf);
        model.put("department", departmentDto);
        context.render("/departments/add.html", model);
    }
}
