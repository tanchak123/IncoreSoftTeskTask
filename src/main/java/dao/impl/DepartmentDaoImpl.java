package dao.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.DepartmentDao;
import dao.EmployeeDao;
import dto.DepartmentDto;
import dto.EmployeeDto;
import jooq.example.domain.tables.Department;
import jooq.example.domain.tables.EmployeeDepartment;
import jooq.example.domain.tables.records.DepartmentRecord;
import jooq.example.domain.tables.records.EmployeeDepartmentRecord;
import org.jooq.InsertValuesStep2;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import utils.JdbcConnection;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import static jooq.example.domain.Tables.DEPARTMENT;
import static jooq.example.domain.Tables.EMPLOYEE_DEPARTMENT;

@Singleton
public class DepartmentDaoImpl implements DepartmentDao {
    private static final SQLDialect POSTGRES = SQLDialect.POSTGRES;
    @Override
    public DepartmentDto create(DepartmentDto department) {
        try (Connection connection = JdbcConnection.getConnection()) {

            Result<Record2<Long, Date>> afterInsert = DSL.using(connection, POSTGRES)
                    .transactionResult(context -> DSL.using(context)
                            .insertInto(DEPARTMENT, DEPARTMENT.NAME)
                            .values(department.getName()).returningResult(DEPARTMENT.ID, DEPARTMENT.CREATE_DATE).fetch());
            department.setId(afterInsert.get(0).value1());
            department.setCreateDate(afterInsert.get(0).value2().toLocalDate());
            InsertValuesStep2<EmployeeDepartmentRecord, Long, Long> values = DSL.using(connection, POSTGRES)
                    .insertInto(EMPLOYEE_DEPARTMENT, EMPLOYEE_DEPARTMENT.EMPLOYEE_ID, EMPLOYEE_DEPARTMENT.DEPARTMENT_ID);
            department.getEmployeeList().forEach(employee -> values.values(employee.getId(), department.getId()));
            values.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return department;
    }


    @Override
    public Boolean update(DepartmentDto department) {
        boolean result = false;
        try (Connection connection = JdbcConnection.getConnection();
        Connection connection1 = JdbcConnection.getConnection()) {
            DSL.using(connection, POSTGRES)
                    .delete(EMPLOYEE_DEPARTMENT)
                    .where(EMPLOYEE_DEPARTMENT.DEPARTMENT_ID.eq(department.getId()))
                    .execute();
            InsertValuesStep2<EmployeeDepartmentRecord, Long, Long> values = DSL.using(connection, POSTGRES)
                    .insertInto(EMPLOYEE_DEPARTMENT, EMPLOYEE_DEPARTMENT.EMPLOYEE_ID, EMPLOYEE_DEPARTMENT.DEPARTMENT_ID);
            department.getEmployeeList().forEach(employee -> values.values(employee.getId(), department.getId()));
            values.execute();
                result = DSL.using(connection1, POSTGRES)
                        .update(DEPARTMENT)
                        .set(DEPARTMENT.NAME, department.getName())
                        .where(DEPARTMENT.ID.eq(department.getId()))
                        .execute() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    @Override
    public Boolean delete(Long id) {
        try (Connection connection = JdbcConnection.getConnection();
                Connection connection1 = JdbcConnection.getConnection()) {
            DSL.using(connection, POSTGRES).transaction(context ->
                    DSL.using(context)
                            .delete(EmployeeDepartment.EMPLOYEE_DEPARTMENT)
                            .where(EmployeeDepartment.EMPLOYEE_DEPARTMENT.DEPARTMENT_ID.eq(id))
                            .execute());
            return DSL.using(connection1, POSTGRES).transactionResult(context ->
                    DSL.using(context)
                            .delete(Department.DEPARTMENT)
                            .where(Department.DEPARTMENT.ID.eq(id))
                            .execute() == 1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public List<DepartmentDto> getAll() {
        try(Connection connection = JdbcConnection.getConnection()) {
            return DSL.using(connection, POSTGRES).transactionResult(configuration -> DSL.using(configuration)
                    .selectFrom(Department.DEPARTMENT)
                    .fetchInto(DepartmentDto.class));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public DepartmentDto get(Long id) {
        DepartmentDto department = null;
        try (Connection connection = JdbcConnection.getConnection();
             Connection connection1 = JdbcConnection.getConnection()) {
            department = DSL.using(connection, POSTGRES)
                    .transactionResult(context -> {
                        DepartmentRecord departmentRecord = DSL.using(context)
                                .selectFrom(Department.DEPARTMENT)
                                .where(DEPARTMENT.ID.eq(id)).fetchAny();
                        if (departmentRecord == null) {
                            return null;
                        }
                        return departmentRecord
                                .into(DepartmentDto.class);
                    });
            List<Long> employeesIds = DSL.using(connection1, POSTGRES)
                            .selectFrom(EMPLOYEE_DEPARTMENT)
                            .where(EMPLOYEE_DEPARTMENT.DEPARTMENT_ID.eq(id))
                            .fetch().getValues(EMPLOYEE_DEPARTMENT.EMPLOYEE_ID, Long.class);
            List<EmployeeDto> employeeList = department.getEmployeeList();
            employeesIds.forEach(emplId -> {
                EmployeeDto employeeDto = new EmployeeDto();
                employeeDto.setId(emplId);
                employeeList.add(employeeDto);
            });
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return department;
    }
}
