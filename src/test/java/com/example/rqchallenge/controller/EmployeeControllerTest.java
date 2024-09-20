package com.example.rqchallenge.controller;

import com.example.rqchallenge.employees.EmployeeController;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.CreateEmployeeRequest;
import com.example.rqchallenge.service.EmployeeService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetAllEmployees_ReturnsEmployeeList() throws Exception {
        // Arrange
        Employee employee1 = new Employee("1", "John Doe", "50000", "30", "");
        Employee employee2 = new Employee("2", "Jane Smith", "60000", "25", "");
        List<Employee> employeeList = Arrays.asList(employee1, employee2);

        when(employeeService.getAllEmployees()).thenReturn(employeeList);

        // Act & Assert
        mockMvc.perform(get("/v1/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].employee_name").value("John Doe"))
                .andExpect(jsonPath("$[1].employee_name").value("Jane Smith"));

        // Verify that the service method was called once
        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    public void testGetAllEmployees_NoContent() throws Exception {
        // Arrange
        when(employeeService.getAllEmployees()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/v1/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify that the service method was called once
        verify(employeeService, times(1)).getAllEmployees();
    }

    // Test for searching employees by name
    @Test
    public void testGetEmployeesByNameSearch_ReturnsMatchingEmployees() throws Exception {
        // Arrange
        Employee employee1 = new Employee("1", "John Doe", "50000", "30", "");
        Employee employee2 = new Employee("2", "Jane Smith", "60000", "25", "");
        List<Employee> employeeList = Arrays.asList(employee1, employee2);

        when(employeeService.getEmployeesByNameSearch("John")).thenReturn(Arrays.asList(employee1));

        // Act & Assert
        mockMvc.perform(get("/v1/employees/search/John")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].employee_name").value("John Doe"));

        // Verify that the service method was called once
        verify(employeeService, times(1)).getEmployeesByNameSearch("John");
    }

    @Test
    public void testGetEmployeesByNameSearch_NoMatch() throws Exception {
        // Arrange
        when(employeeService.getEmployeesByNameSearch("Mike")).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/v1/employees/search/Mike")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify that the service method was called once
        verify(employeeService, times(1)).getEmployeesByNameSearch("Mike");
    }

    // Test for getting employee by ID
    @Test
    public void testGetEmployeeById_EmployeeFound() throws Exception {
        // Arrange
        Employee employee = new Employee("1", "John Doe", "50000", "30", "");

        when(employeeService.getEmployeeById("1")).thenReturn(employee);

        // Act & Assert
        mockMvc.perform(get("/v1/employees/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name").value("John Doe"));

        // Verify that the service method was called once
        verify(employeeService, times(1)).getEmployeeById("1");
    }

    @Test
    public void testGetEmployeeById_EmployeeNotFound() throws Exception {
        // Arrange
        when(employeeService.getEmployeeById("99")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/v1/employees/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Verify that the service method was called once
        verify(employeeService, times(1)).getEmployeeById("99");
    }

    @Test
    public void testGetHighestSalaryOfEmployees_ReturnsHighestSalary() throws Exception {
        // Arrange
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(50000);

        // Act & Assert
        mockMvc.perform(get("/v1/employees/highestSalary")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("50000"));  // Check that the response body is the highest salary

        // Verify that the service method was called once
        verify(employeeService, times(1)).getHighestSalaryOfEmployees();
    }

    @Test
    public void testGetHighestSalaryOfEmployees_NoContent() throws Exception {
        // Arrange
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(0);  // Simulate no employees or no salary found

        // Act & Assert
        mockMvc.perform(get("/v1/employees/highestSalary")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify that the service method was called once
        verify(employeeService, times(1)).getHighestSalaryOfEmployees();
    }

    @Test
    public void testGetTop10HighestEarningEmployeeNames_ReturnsTop10Employees() throws Exception {
        // Arrange
        List<String> top10Employees = Arrays.asList(
                "John Doe", "Jane Smith", "Mike Johnson", "Robert Brown", "Emily Davis",
                "Jessica Wilson", "David Taylor", "Sarah Moore", "Daniel Clark", "Lisa White"
        );

        when(employeeService.getTop10HighestEarningEmployeeNames()).thenReturn(top10Employees);

        // Act & Assert
        mockMvc.perform(get("/v1/employees/top10HighestEarningEmployeeNames")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0]").value("John Doe"))
                .andExpect(jsonPath("$[1]").value("Jane Smith"))
                .andExpect(jsonPath("$[2]").value("Mike Johnson"))
                .andExpect(jsonPath("$[9]").value("Lisa White"));

        // Verify that the service method was called once
        verify(employeeService, times(1)).getTop10HighestEarningEmployeeNames();
    }

    @Test
    public void testGetTop10HighestEarningEmployeeNames_NoContent() throws Exception {
        // Arrange
        when(employeeService.getTop10HighestEarningEmployeeNames()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/v1/employees/top10HighestEarningEmployeeNames")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify that the service method was called once
        verify(employeeService, times(1)).getTop10HighestEarningEmployeeNames();
    }

   @Test
    public void testCreateEmployee_Success() throws Exception {
        // Arrange
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest("John Doe", "50000", "30");

        when(employeeService.createEmployee(any(CreateEmployeeRequest.class))).thenReturn("success");

        // Act & Assert
        mockMvc.perform(post("/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createEmployeeRequest)))  // Convert CreateEmployeeRequest to JSON
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        // Verify that the service method was called once
        verify(employeeService, times(1)).createEmployee(any(CreateEmployeeRequest.class));
    }

    @Test
    public void testCreateEmployee_Failure() throws Exception {
        // Arrange
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest("John Doe", "50000", "30");

        when(employeeService.createEmployee(any(CreateEmployeeRequest.class))).thenReturn("failure");

        // Act & Assert
        mockMvc.perform(post("/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createEmployeeRequest)))  // Convert CreateEmployeeRequest to JSON
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to create employee"));

        // Verify that the service method was called once
        verify(employeeService, times(1)).createEmployee(any(CreateEmployeeRequest.class));
    }

    @Test
    public void testDeleteEmployee_Success() throws Exception {
        // Arrange
        String employeeId = "1";
        String deletedEmployeeName = "John Doe";

        when(employeeService.deleteEmployee(employeeId)).thenReturn(deletedEmployeeName);

        // Act & Assert
        mockMvc.perform(delete("/v1/employees/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(deletedEmployeeName));

        // Verify that the service method was called once
        verify(employeeService, times(1)).deleteEmployee(employeeId);
    }

    @Test
    public void testDeleteEmployee_NotFound() throws Exception {
        // Arrange
        String employeeId = "99";

        when(employeeService.deleteEmployee(employeeId)).thenReturn(null);  // Simulate not found

        // Act & Assert
        mockMvc.perform(delete("/v1/employees/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Verify that the service method was called once
        verify(employeeService, times(1)).deleteEmployee(employeeId);
    }
}

