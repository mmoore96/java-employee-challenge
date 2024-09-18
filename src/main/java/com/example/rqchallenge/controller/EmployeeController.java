package com.example.rqchallenge.employees;

import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.EmployeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Get all employees.
     *
     * @return ResponseEntity containing the list of employees.
     * @throws IOException
     */
    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees() throws IOException {
        logger.info("Received request to get all employees.");

        List<Employee> employees = employeeService.getAllEmployees();

        if (employees.isEmpty()) {
            logger.warn("No employees found.");
            return ResponseEntity.noContent().build();
        } else {
            logger.info("Returning {} employees.", employees.size());
            return ResponseEntity.ok(employees);
        }
    }

    /**
     * Search for employees by name.
     *
     * @param searchString The string to search for in employee names.
     * @return ResponseEntity containing the list of matching employees.
     */
    @GetMapping("/employees/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        logger.info("Received request to search employees by name containing '{}'.", searchString);

        List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);

        if (employees.isEmpty()) {
            logger.warn("No employees found matching '{}'.", searchString);
            return ResponseEntity.noContent().build();
        } else {
            logger.info("Returning {} employees matching '{}'.", employees.size(), searchString);
            return ResponseEntity.ok(employees);
        }
    }

    /**
     * Get an employee by ID.
     *
     * @param id The ID of the employee to retrieve.
     * @return ResponseEntity containing the employee or a 404 status if not found.
     */
    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        logger.info("Received request to get employee by ID '{}'.", id);

        Employee employee = employeeService.getEmployeeById(id);

        if (employee == null) {
            logger.warn("Employee with ID '{}' not found.", id);
            return ResponseEntity.notFound().build();
        } else {
            logger.info("Returning employee with ID '{}'.", id);
            return ResponseEntity.ok(employee);
        }
    }

    /**
     * Get the highest salary of all employees.
     *
     * @return ResponseEntity containing the highest salary as an integer.
     */
    @GetMapping("/employees/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        logger.info("Received request to get the highest salary of employees.");

        int highestSalary = employeeService.getHighestSalaryOfEmployees();

        if (highestSalary == 0) {
            logger.warn("No employees found or could not retrieve the highest salary.");
            return ResponseEntity.noContent().build();
        } else {
            logger.info("Returning the highest salary: {}", highestSalary);
            return ResponseEntity.ok(highestSalary);
        }
    }
    //
    // @GetMapping("/topTenHighestEarningEmployeeNames")
    // ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames();
    //
    // @PostMapping()
    // ResponseEntity<Employee> createEmployee(@RequestBody Map<String, Object> employeeInput);
    //
    // @DeleteMapping("/{id}")
    // ResponseEntity<String> deleteEmployeeById(@PathVariable String id);

}
