package ru.ekp.redis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQueries;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
@Entity
@Table(name = "employees")
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = "Employee.increaseDepartmentSalary",
                procedureName = "increase_department_salary",
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "dept_name", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "increase_percent", type = BigDecimal.class)
                }
        )
})
public class Employee {

    // Геттеры и сеттеры
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal salary;
    private String department;

}