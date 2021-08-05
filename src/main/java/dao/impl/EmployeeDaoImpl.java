package dao.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.DepartmentDao;
import dao.EmployeeDao;
import dto.DepartmentDto;
import dto.EmployeeDto;
import jooq.example.domain.tables.EmployeeDepartment;
import jooq.example.domain.tables.records.EmployeeDepartmentRecord;
import jooq.example.domain.tables.records.EmployeeRecord;
import org.jooq.InsertValuesStep2;
import org.jooq.Record2;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import utils.JdbcConnection;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import static jooq.example.domain.Tables.*;

@Singleton
public class EmployeeDaoImpl implements EmployeeDao {
    private static final SQLDialect POSTGRES = SQLDialect.POSTGRES;
    @Override
    public EmployeeDto create(EmployeeDto employee) {
        try (Connection connection = JdbcConnection.getConnection()) {
            Record2<Long, Date> fetch = DSL.using(connection, POSTGRES)
                    .insertInto(EMPLOYEE,
                            EMPLOYEE.NAME, EMPLOYEE.EMAIL, EMPLOYEE.EXPERIENCE_YEARS)
                    .values(employee.getName(), employee.getEmail(), employee.getExperienceYears())
                    .returningResult(EMPLOYEE.ID, EMPLOYEE.CREATE_DATE).fetch().get(0);
            employee.setId(fetch.value1());
            employee.setCreateDate(fetch.value2().toLocalDate());
            InsertValuesStep2<EmployeeDepartmentRecord, Long, Long> values = DSL.using(connection, POSTGRES)
                    .insertInto(EMPLOYEE_DEPARTMENT, EMPLOYEE_DEPARTMENT.DEPARTMENT_ID, EMPLOYEE_DEPARTMENT.EMPLOYEE_ID);
            employee.getDepartmentList().forEach(department -> values.values(department.getId(), employee.getId()));
            values.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employee;
    }

    @Override
    public Boolean update(EmployeeDto employee) {
        boolean result = false;
        try (Connection connection = JdbcConnection.getConnection();
             Connection connection1 = JdbcConnection.getConnection()) {
            DSL.using(connection, POSTGRES)
                    .delete(EMPLOYEE_DEPARTMENT)
                    .where(EMPLOYEE_DEPARTMENT.EMPLOYEE_ID.eq(employee.getId()))
                    .execute();
            InsertValuesStep2<EmployeeDepartmentRecord, Long, Long> values = DSL.using(connection, POSTGRES)
                    .insertInto(EMPLOYEE_DEPARTMENT, EMPLOYEE_DEPARTMENT.DEPARTMENT_ID, EMPLOYEE_DEPARTMENT.EMPLOYEE_ID);
            employee.getDepartmentList().forEach(department -> values.values(department.getId(), employee.getId()));
            values.execute();
                result = DSL.using(connection1, POSTGRES)
                        .update(EMPLOYEE)
                        .set(EMPLOYEE.NAME, employee.getName())
                        .set(EMPLOYEE.EMAIL, employee.getEmail())
                        .set(EMPLOYEE.EXPERIENCE_YEARS, employee.getExperienceYears())
                        .where(EMPLOYEE.ID.eq(employee.getId()))
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
            DSL.using(connection, POSTGRES)
                    .delete(EmployeeDepartment.EMPLOYEE_DEPARTMENT)
                    .where(EmployeeDepartment.EMPLOYEE_DEPARTMENT.EMPLOYEE_ID.eq(id))
                    .execute();
            return DSL.using(connection1, POSTGRES)
                    .delete(EMPLOYEE)
                    .where(EMPLOYEE.ID.eq(id))
                    .execute() == 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public List<EmployeeDto> getAll() {
        try (Connection dataSource = JdbcConnection.getConnection()) {
            return DSL.using(dataSource, POSTGRES).transactionResult(configuration -> DSL.using(configuration)
                    .selectFrom(EMPLOYEE)
                    .orderBy(EMPLOYEE.ID.asc())
                    .fetchInto(EmployeeDto.class));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public EmployeeDto get(Long id) {
        EmployeeDto employee = null;
        try(Connection dataSource = JdbcConnection.getConnection()) {
            EmployeeRecord employeeRecord = DSL.using(dataSource, POSTGRES)
                    .selectFrom(EMPLOYEE)
                    .where(EMPLOYEE.ID.eq(id)).fetchAny();
            if (employeeRecord == null) {
                return null;
            }
            employee = employeeRecord.into(EmployeeDto.class);
            List<Long> departments = DSL.using(dataSource, POSTGRES)
                    .selectFrom(EMPLOYEE_DEPARTMENT)
                    .where(EMPLOYEE_DEPARTMENT.EMPLOYEE_ID.eq(id))
                    .fetch().getValues(EMPLOYEE_DEPARTMENT.DEPARTMENT_ID, Long.class);
            List<DepartmentDto> departmentList = employee.getDepartmentList();
            departments.forEach(depId -> {
                DepartmentDto departmentDto = new DepartmentDto();
                departmentDto.setId(depId);
                departmentList.add(departmentDto);
            });
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employee;
    }
}
