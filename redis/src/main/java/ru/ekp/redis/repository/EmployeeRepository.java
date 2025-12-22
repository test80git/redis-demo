package ru.ekp.redis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ekp.redis.entity.Employee;

import java.math.BigDecimal;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Вызов хранимой процедуры через @Procedure
    @Procedure(name = "Employee.increaseDepartmentSalary")
    void increaseDepartmentSalary(@Param("dept_name") String departmentName,
                                  @Param("increase_percent") BigDecimal increasePercent);

    // Вызов хранимой функции через @Query с nativeQuery
    @Query(value = "SELECT get_department_salary_total(:departmentName)",
            nativeQuery = true)
    BigDecimal getDepartmentSalaryTotal(@Param("departmentName") String departmentName);


    // Альтернативный способ вызова процедуры через EntityManager
    @Modifying
    @Query(value = "CALL increase_department_salary(:deptName, :percent)",
            nativeQuery = true)
    void callIncreaseDepartmentSalary(@Param("deptName") String departmentName,
                                      @Param("percent") BigDecimal percent);

    // Вызов функции через EntityManager
    @Query(value = "SELECT * FROM get_department_salary_total(:deptName)",
            nativeQuery = true)
    BigDecimal callGetDepartmentSalaryTotal(@Param("deptName") String departmentName);

}
