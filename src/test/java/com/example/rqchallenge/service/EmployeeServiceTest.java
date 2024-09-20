package com.example.rqchallenge.service;

import com.example.rqchallenge.model.CreateEmployeeRequest;
import com.example.rqchallenge.model.EmployeeApiResponse;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.CreateEmployeeResponse;

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

    @Test
    public void testGetHighestSalaryOfEmployees_Success() {
        // Arrange
        Employee employee1 = new Employee("1", "John Doe", "50000", "30", "");
        Employee employee2 = new Employee("2", "Jane Smith", "60000", "25", "");
        Employee employee3 = new Employee("3", "Mike Tyson", "70000", "35", "");
        List<Employee> employeeList = Arrays.asList(employee1, employee2, employee3);

        EmployeeApiResponse<List<Employee>> apiResponse = new EmployeeApiResponse<>("success", employeeList);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(apiResponse));

        // Act
        int result = employeeService.getHighestSalaryOfEmployees();

        // Assert
        assertEquals(70000, result);  // The highest salary should be 70000
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testGetHighestSalaryOfEmployees_ConnectionError() {
        // Arrange
        // Simulate a ResourceAccessException (connection failure)
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        // Act
        int result = employeeService.getHighestSalaryOfEmployees();

        // Assert
        assertEquals(725000, result);  // Highest salary in the default list is 725000
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testGetTop10HighestEarningEmployeeNames_Success() {
        // Arrange
        Employee employee1 = new Employee("1", "John Doe", "50000", "30", "");
        Employee employee2 = new Employee("2", "Jane Smith", "60000", "25", "");
        Employee employee3 = new Employee("3", "Mike Tyson", "70000", "35", "");
        Employee employee4 = new Employee("4", "Bruce Wayne", "80000", "40", "");
        Employee employee5 = new Employee("5", "Clark Kent", "90000", "32", "");
        Employee employee6 = new Employee("6", "Diana Prince", "100000", "28", "");
        Employee employee7 = new Employee("7", "Barry Allen", "110000", "27", "");
        Employee employee8 = new Employee("8", "Hal Jordan", "120000", "36", "");
        Employee employee9 = new Employee("9", "Arthur Curry", "130000", "33", "");
        Employee employee10 = new Employee("10", "Victor Stone", "140000", "29", "");
        Employee employee11 = new Employee("11", "Peter Parker", "150000", "26", "");
        List<Employee> employeeList = Arrays.asList(employee1, employee2, employee3, employee4, employee5, employee6, employee7, employee8, employee9, employee10, employee11);

        EmployeeApiResponse<List<Employee>> apiResponse = new EmployeeApiResponse<>("success", employeeList);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(apiResponse));

        // Act
        List<String> result = employeeService.getTop10HighestEarningEmployeeNames();

        // Assert
        assertNotNull(result);
        assertEquals(10, result.size());
        assertEquals("Peter Parker", result.get(0));  // Highest earner should be Peter Parker
        assertEquals("Victor Stone", result.get(1));  // Second highest earner should be Victor Stone
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testGetTop10HighestEarningEmployeeNames_ConnectionError() {
        // Arrange
        // Simulate a ResourceAccessException (connection failure)
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        // Act
        List<String> result = employeeService.getTop10HighestEarningEmployeeNames();

        // Assert
        assertNotNull(result);
        assertEquals(10, result.size());
        assertEquals("Paul Byrd", result.get(0));  // Default list highest earner should be Paul Byrd
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

  @Test
    public void testCreateEmployee_Success() {
        // Arrange
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest("John Doe", "50000", "30");
        CreateEmployeeResponse createEmployeeResponse = new CreateEmployeeResponse("John Doe", "50000", "30", "1");
        EmployeeApiResponse<CreateEmployeeResponse> apiResponse = new EmployeeApiResponse<>("success", createEmployeeResponse);

        when(restTemplate.exchange(
                eq("https://dummy.restapiexample.com/api/v1/create"),
                eq(HttpMethod.POST),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(apiResponse));

        // Act
        String result = employeeService.createEmployee(createEmployeeRequest);

        // Assert
        assertEquals("success", result);  // The response should be "success"
        verify(restTemplate, times(1)).exchange(
                eq("https://dummy.restapiexample.com/api/v1/create"),
                eq(HttpMethod.POST),
                any(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testCreateEmployee_ConnectionError() {
        // Arrange
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest("John Doe", "50000", "30");

        // Simulate a ResourceAccessException (connection failure)
        when(restTemplate.exchange(
                eq("https://dummy.restapiexample.com/api/v1/create"),
                eq(HttpMethod.POST),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        // Act
        String result = employeeService.createEmployee(createEmployeeRequest);

        // Assert
        assertEquals("success", result);  // Should fallback to default response
        verify(restTemplate, times(1)).exchange(
                eq("https://dummy.restapiexample.com/api/v1/create"),
                eq(HttpMethod.POST),
                any(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testCreateEmployee_HttpError() {
        // Arrange
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest("John Doe", "50000", "30");

        // Simulate a non-2xx HTTP error
        when(restTemplate.exchange(
                eq("https://dummy.restapiexample.com/api/v1/create"),
                eq(HttpMethod.POST),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.status(500).build());

        // Act
        String result = employeeService.createEmployee(createEmployeeRequest);

        // Assert
        assertEquals("success", result);  // Should fallback to default response
        verify(restTemplate, times(1)).exchange(
                eq("https://dummy.restapiexample.com/api/v1/create"),
                eq(HttpMethod.POST),
                any(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testDeleteEmployee_Success() {
        // Arrange
        String employeeId = "1";
        Employee employee = new Employee(employeeId, "John Doe", "50000", "30", "");
        EmployeeApiResponse<String> apiResponse = new EmployeeApiResponse<>("success", "successfully! deleted Record");

        // Mock fetching the employee first
        when(restTemplate.exchange(
                eq("https://dummy.restapiexample.com/api/v1/employee/" + employeeId),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(new EmployeeApiResponse<>("success", employee)));

        // Mock the delete operation
        when(restTemplate.exchange(
                eq("https://dummy.restapiexample.com/api/v1/delete/" + employeeId),
                eq(HttpMethod.DELETE),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(apiResponse));

        // Act
        String result = employeeService.deleteEmployee(employeeId);

        // Assert
        assertEquals("John Doe", result);  // Expect the name of the deleted employee
        verify(restTemplate, times(1)).exchange(
                eq("https://dummy.restapiexample.com/api/v1/employee/" + employeeId),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
        verify(restTemplate, times(1)).exchange(
                eq("https://dummy.restapiexample.com/api/v1/delete/" + employeeId),
                eq(HttpMethod.DELETE),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testDeleteEmployee_NotFound() {
        // Arrange
        String employeeId = "999";
        EmployeeApiResponse<Employee> notFoundResponse = new EmployeeApiResponse<>("error", null);

        // Simulate employee not found
        when(restTemplate.exchange(
                eq("https://dummy.restapiexample.com/api/v1/employee/" + employeeId),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.status(404).body(notFoundResponse));

        // Act
        String result = employeeService.deleteEmployee(employeeId);

        // Assert
        assertEquals("Employee with ID " + employeeId + " not found, deletion aborted.", result);  // Expect the appropriate message
        verify(restTemplate, times(1)).exchange(
                eq("https://dummy.restapiexample.com/api/v1/employee/" + employeeId),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
        verify(restTemplate, never()).exchange(
                eq("https://dummy.restapiexample.com/api/v1/delete/" + employeeId),
                eq(HttpMethod.DELETE),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testDeleteEmployee_ConnectionError() {
        // Arrange
        String employeeId = "1";
        Employee employee = new Employee(employeeId, "John Doe", "50000", "30", "");

        // Mock fetching the employee first
        when(restTemplate.exchange(
                eq("https://dummy.restapiexample.com/api/v1/employee/" + employeeId),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(new EmployeeApiResponse<>("success", employee)));

        // Simulate a ResourceAccessException (connection failure) during the DELETE request
        when(restTemplate.exchange(
                eq("https://dummy.restapiexample.com/api/v1/delete/" + employeeId),
                eq(HttpMethod.DELETE),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        // Act
        String result = employeeService.deleteEmployee(employeeId);

        // Assert
        assertEquals("John Doe", result);  // The fallback should return "John Doe" from the fetched employee
        verify(restTemplate, times(1)).exchange(
                eq("https://dummy.restapiexample.com/api/v1/employee/" + employeeId),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
        verify(restTemplate, times(1)).exchange(
                eq("https://dummy.restapiexample.com/api/v1/delete/" + employeeId),
                eq(HttpMethod.DELETE),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testDeleteEmployee_HttpErrorOnDelete() {
        // Arrange
        String employeeId = "1";
        Employee employee = new Employee(employeeId, "John Doe", "50000", "30", "");

        // Mock fetching the employee first
        when(restTemplate.exchange(
                eq("https://dummy.restapiexample.com/api/v1/employee/" + employeeId),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(new EmployeeApiResponse<>("success", employee)));

        // Simulate HTTP error during delete
        when(restTemplate.exchange(
                eq("https://dummy.restapiexample.com/api/v1/delete/" + employeeId),
                eq(HttpMethod.DELETE),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.status(500).build());

        // Act
        String result = employeeService.deleteEmployee(employeeId);

        // Assert
        assertEquals("John Doe", result);  // Expect the default response
        verify(restTemplate, times(1)).exchange(
                eq("https://dummy.restapiexample.com/api/v1/employee/" + employeeId),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
        verify(restTemplate, times(1)).exchange(
                eq("https://dummy.restapiexample.com/api/v1/delete/" + employeeId),
                eq(HttpMethod.DELETE),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }
}

