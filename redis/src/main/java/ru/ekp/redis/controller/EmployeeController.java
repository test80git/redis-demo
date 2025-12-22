package ru.ekp.redis.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ekp.redis.entity.Employee;
import ru.ekp.redis.service.EmployeeService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/department/{departmentName}/salary-total")
    public ResponseEntity<Map<String, Object>> getDepartmentSalaryTotal(
            @PathVariable String departmentName) {
        log.info("Begin getDepartmentSalaryTotal");
        BigDecimal total = employeeService.getDepartmentSalaryTotal(departmentName);

        Map<String, Object> response = new HashMap<>();
        response.put("department", departmentName);
        response.put("totalSalary", total);
        response.put("employees", employeeService.getEmployeesByDepartment(departmentName));
        log.info("End getDepartmentSalaryTotal");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/department/{departmentName}/increase-salary")
    public ResponseEntity<Map<String, Object>> increaseDepartmentSalary(
            @PathVariable String departmentName,
            @RequestParam BigDecimal percent) {
        log.info("Begin increaseDepartmentSalary");
        // Получаем старые данные
        BigDecimal oldTotal = employeeService.getDepartmentSalaryTotal(departmentName);
        List<Employee> employeesBefore = employeeService.getEmployeesByDepartment(departmentName);
        log.info("oldTotal: {}, employeesBefore: {}", oldTotal, employeesBefore);
        // Вызываем хранимую процедуру
        employeeService.increaseDepartmentSalary(departmentName, percent);

        // Получаем обновленные данные
        BigDecimal newTotal = employeeService.getDepartmentSalaryTotal(departmentName);
        List<Employee> employeesAfter = employeeService.getEmployeesByDepartment(departmentName);
        log.info("newTotal: {}, employeesAfter: {}", newTotal, employeesAfter);

        Map<String, Object> response = new HashMap<>();
        response.put("department", departmentName);
        response.put("increasePercent", percent);
        response.put("oldTotalSalary", oldTotal);
        response.put("newTotalSalary", newTotal);
        response.put("salaryIncrease", newTotal.subtract(oldTotal));
        response.put("employeesBefore", employeesBefore);
        response.put("employeesAfter", employeesAfter);
        log.info("End increaseDepartmentSalary");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/native/department/{departmentName}/increase-salary")
    public ResponseEntity<String> increaseSalaryNative(
            @PathVariable String departmentName,
            @RequestParam BigDecimal percent) {
        log.info("Begin increaseSalaryNative");
        employeeService.increaseDepartmentSalaryNative(departmentName, percent);
        log.info("End increaseSalaryNative");
        return ResponseEntity.ok(
                String.format("Зарплата в отделе '%s' увеличена на %.2f%%",
                        departmentName, percent)
        );
    }

    @GetMapping("/department/{departmentName}/salary-total/StoredProcedureQuery")
    public ResponseEntity<Map<String, Object>> getDepartmentSalaryTotalStoredProcedureQuery(
            @PathVariable String departmentName) {
        log.info("Begin");
        BigDecimal total = employeeService.callFunctionWithStoredProcedureQuery(departmentName);

        Map<String, Object> response = new HashMap<>();
        response.put("department", departmentName);
        response.put("totalSalary", total);
        response.put("employees", employeeService.getEmployeesByDepartment(departmentName));
        log.info("End");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/department/{departmentName}/call-increase-salary")
    public ResponseEntity<Map<String, Object>> callIncreaseDepartmentSalary(
            @PathVariable String departmentName,
            @RequestParam BigDecimal percent) {
        log.info("Begin callIncreaseDepartmentSalary");
        // Получаем старые данные
        BigDecimal oldTotal = employeeService.getDepartmentSalaryTotal(departmentName);
        List<Employee> employeesBefore = employeeService.getEmployeesByDepartment(departmentName);
        log.info("oldTotal: {}, employeesBefore: {}", oldTotal, employeesBefore);
        // Вызываем хранимую процедуру
        employeeService.callIncreaseDepartmentSalary(departmentName, percent);

        // Получаем обновленные данные
        BigDecimal newTotal = employeeService.callGetDepartmentSalaryTotal(departmentName);
        List<Employee> employeesAfter = employeeService.getEmployeesByDepartment(departmentName);
        log.info("newTotal: {}, employeesAfter: {}", newTotal, employeesAfter);

        Map<String, Object> response = new HashMap<>();
        response.put("department", departmentName);
        response.put("increasePercent", percent);
        response.put("oldTotalSalary", oldTotal);
        response.put("newTotalSalary", newTotal);
        response.put("salaryIncrease", newTotal.subtract(oldTotal));
        response.put("employeesBefore", employeesBefore);
        response.put("employeesAfter", employeesAfter);
        log.info("End callIncreaseDepartmentSalary");
        return ResponseEntity.ok(response);
    }

}
