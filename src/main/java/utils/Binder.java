package utils;

import com.google.inject.AbstractModule;
import controllers.departments.DepartmentController;
import controllers.employees.EmployeeController;
import service.DepartmentService;
import service.EmployeeService;
import service.impl.DepartmentServiceImpl;
import service.impl.EmployeeServiceImpl;

public class Binder extends AbstractModule {

   @Override
   protected void configure() {
      bind(DepartmentService.class).to(DepartmentServiceImpl.class).asEagerSingleton();
      bind(EmployeeService.class).to(EmployeeServiceImpl.class).asEagerSingleton();
      requestStaticInjection(DaoUtils.class);
   }

}