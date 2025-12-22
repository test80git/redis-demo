-- Создаем тестовую таблицу
CREATE TABLE employees
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100),
    salary     DECIMAL(10, 2),
    department VARCHAR(50)
);

-- Хранимая функция: возвращает общую зарплату по отделу
CREATE OR REPLACE FUNCTION get_department_salary_total(dept_name VARCHAR)
    RETURNS DECIMAL(10,2)
    LANGUAGE plpgsql
AS
$$
DECLARE
    total DECIMAL(10,2);
BEGIN
    SELECT COALESCE(SUM(salary), 0)
    INTO total
    FROM employees
    WHERE department = dept_name;

    RETURN total;
END;
$$;

-- Вызов функции
    SELECT get_department_salary_total('IT');



-- Удалите старую процедуру, если она существует
    DROP PROCEDURE IF EXISTS increase_department_salary;

-- Хранимая процедура: повышает зарплату всем сотрудникам отдела
CREATE OR REPLACE PROCEDURE increase_department_salary(
    dept_name VARCHAR,
    increase_percent DECIMAL(10,2)
)
LANGUAGE plpgsql
AS $$
BEGIN
UPDATE employees
SET salary = salary * (1 + increase_percent / 100)
WHERE department = dept_name;
END;
$$;

-- 3. Протестируйте процедуру напрямую в psql
CALL increase_department_salary('IT', 10.00::DECIMAL(10,2));