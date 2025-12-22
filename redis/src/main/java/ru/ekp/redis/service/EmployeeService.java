package ru.ekp.redis.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ekp.redis.entity.Employee;
import ru.ekp.redis.repository.EmployeeRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EntityManager entityManager;


    // Способ 1: Использование Repository с аннотациями
    public BigDecimal getDepartmentSalaryTotal(String departmentName) {
        return employeeRepository.getDepartmentSalaryTotal(departmentName);
    }

    public void increaseDepartmentSalary(String departmentName, BigDecimal percent) {
        log.info("Повысить зарплату отдела {} до {}%", departmentName, percent.toString());
        employeeRepository.increaseDepartmentSalary(departmentName, percent);
    }


    // Способ 2: Использование EntityManager напрямую
    public BigDecimal getDepartmentSalaryTotalNative(String departmentName) {
        String sql = "SELECT get_department_salary_total(:deptName)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("deptName", departmentName);
        return (BigDecimal) query.getSingleResult();
    }


    public void increaseDepartmentSalaryNative(String departmentName, BigDecimal percent) {
        String sql = "CALL increase_department_salary(:deptName, :percent)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("deptName", departmentName);
        query.setParameter("percent", percent);
        query.executeUpdate();
    }


    // Способ 3: Использование StoredProcedureQuery
    public BigDecimal callFunctionWithStoredProcedureQuery(String departmentName) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("get_department_salary_total")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, BigDecimal.class, ParameterMode.OUT)
                .setParameter(1, departmentName);

        query.execute();
        return (BigDecimal) query.getOutputParameterValue(2);
    }

    // Получение сотрудников по отделу
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getDepartment().equals(department))
                .collect(Collectors.toList());
    }

    public void callIncreaseDepartmentSalary(String departmentName, BigDecimal percent) {
        employeeRepository.callIncreaseDepartmentSalary(departmentName, percent);
    }

    public BigDecimal callGetDepartmentSalaryTotal(String departmentName) {
        return employeeRepository.callGetDepartmentSalaryTotal(departmentName);
    }
}
