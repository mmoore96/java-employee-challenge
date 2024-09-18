package com.example.rqchallenge.controller;

import com.example.rqchallenge.employees.EmployeeController;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.EmployeeService;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
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
}

