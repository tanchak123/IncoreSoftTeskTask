import com.google.inject.Guice;
import com.google.inject.Injector;
import controllers.departments.DepartmentController;
import controllers.employees.EmployeeController;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import org.thymeleaf.templatemode.TemplateMode;
import utils.Binder;
import utils.ThymeleafConfig;

public class Main {
    private static int Counter = 0;
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new Binder());
        Javalin javalin = Javalin.create(javalinConfig -> {
                    javalinConfig.requestLogger((ctx, ms) -> System.out.println("Request № " + ++Counter));
                    javalinConfig.addSinglePageRoot("/main", "public/templates/main.html");
                    javalinConfig.addStaticFiles("public");
                    JavalinRenderer.register(JavalinThymeleaf.INSTANCE);
                    JavalinThymeleaf.configure(ThymeleafConfig.templateEngine(
                            ThymeleafConfig.templateResolver(
                                    TemplateMode.HTML, "/public/templates/", ".html"))
                    );
                }
        ).start(7777);
        javalin.get("/", MainController::main);
        javalin.get("/departments/", DepartmentController::getAll);
        javalin.get("/employees/", EmployeeController::getAll);

        javalin.get("/employees/create", EmployeeController::create);
        javalin.post("/employees/create", EmployeeController::create);
        javalin.get("/employees/getAll", EmployeeController::getAllEmployees);
        javalin.get("/employees/get", EmployeeController::getById);
        javalin.get("/employees/getDepartments", EmployeeController::getDepartments);
        javalin.post("/employees/update", EmployeeController::update);
        javalin.post("/employees/delete", EmployeeController::delete);
        javalin.post("/employees/remove", EmployeeController::remove);
        javalin.post("/employees/add", EmployeeController::add);
        javalin.get("/employees/add", EmployeeController::add);


        javalin.get("/departments/create", DepartmentController::create);
        javalin.post("/departments/create", DepartmentController::create);
        javalin.get("/departments/getAll", DepartmentController::getAllDepartments);
        javalin.get("/departments/get", DepartmentController::getById);
        javalin.get("/departments/getEmployees", DepartmentController::getEmployees);
        javalin.post("/departments/update", DepartmentController::update);
        javalin.post("/departments/delete", DepartmentController::delete);
        javalin.post("/departments/remove", DepartmentController::remove);
        javalin.get("/departments/add", DepartmentController::add);
        javalin.post("/departments/add", DepartmentController::add);

        javalin.get("/employees/:id/", EmployeeController::get);
        javalin.get("/departments/:id/", DepartmentController::get);
    }
}
