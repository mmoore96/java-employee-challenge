package com.example.rqchallenge.service;

import com.example.rqchallenge.model.EmployeeApiResponse;
import com.example.rqchallenge.model.Employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class EmployeeServiceTest {

    private EmployeeService employeeService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeService(restTemplate, "https://dummy.restapiexample.com/api/v1");
    }

    @Test
    public void testGetAllEmployees_Success() {
        // Arrange
        Employee employee1 = new Employee("1", "John Doe", "50000", "30", "");
        Employee employee2 = new Employee("2", "Jane Smith", "60000", "25", "");
        List<Employee> employeeList = Arrays.asList(employee1, employee2);

        EmployeeApiResponse<List<Employee>> apiResponse = new EmployeeApiResponse<>("success", employeeList);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(apiResponse));

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testGetAllEmployees_ConnectionError() {
        // Arrange
        // Simulate a ResourceAccessException (connection failure)
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(24, result.size());  // Default employee list has 24 employees
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testGetEmployeesByNameSearch_MatchFound() {
        // Arrange
        Employee employee1 = new Employee("1", "John Doe", "50000", "30", "");
        Employee employee2 = new Employee("2", "Jane Smith", "60000", "25", "");
        List<Employee> employeeList = Arrays.asList(employee1, employee2);

        EmployeeApiResponse<List<Employee>> apiResponse = new EmployeeApiResponse<>("success", employeeList);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(apiResponse));

        // Act
        List<Employee> result = employeeService.getEmployeesByNameSearch("John");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getEmployeeName());

        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testGetEmployeesByNameSearch_NoMatch() {
        // Arrange
        Employee employee1 = new Employee("1", "John Doe", "50000", "30", "");
        Employee employee2 = new Employee("2", "Jane Smith", "60000", "25", "");
        List<Employee> employeeList = Arrays.asList(employee1, employee2);

        EmployeeApiResponse<List<Employee>> apiResponse = new EmployeeApiResponse<>("success", employeeList);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(apiResponse));

        // Act
        List<Employee> result = employeeService.getEmployeesByNameSearch("Mike");

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());  // No employee matches "Mike"

        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testGetEmployeeById_Success() {
        // Arrange
        Employee employee = new Employee("1", "John Doe", "50000", "30", "");
        EmployeeApiResponse<Employee> apiResponse = new EmployeeApiResponse<>("success", employee);

        when(restTemplate.exchange(
                eq("https://dummy.restapiexample.com/api/v1/employee/1"),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(apiResponse));

        // Act
        Employee result = employeeService.getEmployeeById("1");

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getEmployeeName());
        verify(restTemplate, times(1)).exchange(
                eq("https://dummy.restapiexample.com/api/v1/employee/1"),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testGetEmployeeById_NotFound() {
        // Arrange
        EmployeeApiResponse<Employee> apiResponse = new EmployeeApiResponse<>("error", null);

        when(restTemplate.exchange(
                eq("https://dummy.restapiexample.com/api/v1/employee/999"),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.status(404).body(apiResponse));

        // Act
        Employee result = employeeService.getEmployeeById("999");

        // Assert
        assertNull(result);  // Employee should not be found
        verify(restTemplate, times(1)).exchange(
                eq("https://dummy.restapiexample.com/api/v1/employee/999"),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testGetEmployeeById_ConnectionError() {
        // Arrange
        when(restTemplate.exchange(
                eq("https://dummy.restapiexample.com/api/v1/employee/1"),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        // Act
        Employee result = employeeService.getEmployeeById("1");

        // Assert
        assertNotNull(result);  // Should return a default employee, not null
        assertEquals("Tiger Nixon", result.getEmployeeName());  // Verify it's from the default list
        verify(restTemplate, times(1)).exchange(
                eq("https://dummy.restapiexample.com/api/v1/employee/1"),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }
}

