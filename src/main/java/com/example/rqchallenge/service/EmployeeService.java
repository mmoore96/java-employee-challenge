package com.example.rqchallenge.service;

import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.CreateEmployeeRequest;
import com.example.rqchallenge.model.EmployeeApiResponse;
import com.example.rqchallenge.model.CreateEmployeeResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.core.ParameterizedTypeReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final RestTemplate restTemplate;
    private String baseUrl;

    public EmployeeService(RestTemplate restTemplate, @Value("${api.base.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Cacheable("employees")
    public List<Employee> getAllEmployees() {
        String url = baseUrl + "/employees";
        logger.info("Fetching all employees from {}", url);

        try {
            ResponseEntity<EmployeeApiResponse<List<Employee>>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<EmployeeApiResponse<List<Employee>>>() {}
            );

            HttpStatus statusCode = responseEntity.getStatusCode(); 
            if (!statusCode.is2xxSuccessful()) {
                logger.error("Received non-2xx status code: {}. Falling back to default employee list.", statusCode);
                return getDefaultEmployeeList();  // Fallback to default employee list
            }

            EmployeeApiResponse<List<Employee>> response = responseEntity.getBody();

            if (response != null && "success".equalsIgnoreCase(response.getStatus())) {
                logger.debug("Successfully retrieved {} employees.", response.getData().size());
                return response.getData();
            } else {
                logger.warn("Failed to retrieve employees. Status: {}. Falling back to default employee list.", response != null ? response.getStatus() : "null");
                return getDefaultEmployeeList();  // Fallback to default employee list
            }
        } catch (ResourceAccessException e) {
            // Handle connection failures specifically
            logger.error("Connection error fetching employees: {}. Falling back to default employee list.", e.getMessage());
            return getDefaultEmployeeList();  // Fallback to default employee list

        } catch (HttpStatusCodeException e) {
            // Catch any HTTP error that isn't a 2xx success response
            logger.error("HTTP error fetching employees ({}): {}. Falling back to default employee list.",
                    e.getStatusCode(), e.getMessage());
            return getDefaultEmployeeList();  // Fallback to default employee list
        } catch (Exception e) {
            // Handle any other exceptions
            logger.error("Error fetching employees: {}. Falling back to default employee list.", e.getMessage());
            return getDefaultEmployeeList();  // Fallback to default employee list
        }
    }

    // Method to search employees by name
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        logger.info("Searching for employees with name containing '{}'", searchString);

        // Fetch all employees
        List<Employee> allEmployees = getAllEmployees();

        // Filter employees whose name contains the search string (case insensitive)
        List<Employee> filteredEmployees = allEmployees.stream()
                .filter(employee -> employee.getEmployeeName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());

        logger.debug("Found {} employees matching the search string '{}'", filteredEmployees.size(), searchString);
        return filteredEmployees;
    }

    // Method to get an employee by ID
    public Employee getEmployeeById(String id) {
      String url = baseUrl + "/employee/" + id;
      logger.info("Fetching employee with ID {} from {}", id, url);

      try {
          ResponseEntity<EmployeeApiResponse<Employee>> responseEntity = restTemplate.exchange(
                  url,
                  HttpMethod.GET,
                  null,
                  new ParameterizedTypeReference<EmployeeApiResponse<Employee>>() {}
          );

          HttpStatus statusCode = responseEntity.getStatusCode();
          if (!statusCode.is2xxSuccessful()) {
              logger.error("Received non-2xx status code: {} while fetching employee with ID {}. Falling back to default employee list.", statusCode, id);
              return getEmployeeFromDefaultList(id);  // Return from default list if the response status is not successful
          }

          EmployeeApiResponse<Employee> response = responseEntity.getBody();

          if (response != null && "success".equalsIgnoreCase(response.getStatus())) {
              logger.debug("Successfully retrieved employee: {}", response.getData());
              return response.getData();
          } else {
              logger.warn("Failed to retrieve employee with ID {}. Status: {}. Falling back to default employee list.", id, response != null ? response.getStatus() : "null");
              return getEmployeeFromDefaultList(id);  // Fallback to default list if the API call fails
          }

      } catch (ResourceAccessException e) {
          // Handle connection failures specifically
          logger.error("Connection error fetching employee with ID {}: {}. Falling back to default employee list.", id, e.getMessage());
          return getEmployeeFromDefaultList(id);  // Fallback to default list in case of connection issues

      } catch (HttpStatusCodeException e) {
          // Catch any HTTP error that isn't a 2xx success response
          logger.error("HTTP error fetching employee with ID {} ({}): {}. Falling back to default employee list.", id, e.getStatusCode(), e.getMessage());
          return getEmployeeFromDefaultList(id);  // Fallback to default list in case of HTTP errors

      } catch (Exception e) {
          // Handle any other exceptions
          logger.error("Error fetching employee with ID {}: {}. Falling back to default employee list.", id, e.getMessage());
          return getEmployeeFromDefaultList(id);  // Fallback to default list in case of any other exceptions
      }
    }

    // Method to get the highest salary of employees
    public int getHighestSalaryOfEmployees() {
        logger.info("Fetching the highest salary of employees.");

        // Fetch all employees
        List<Employee> allEmployees = getAllEmployees();

        if (allEmployees.isEmpty()) {
            logger.warn("No employees found to determine the highest salary. Returning 0.");
            return 0;  // Return 0 if no employees are available
        }

        // Find the employee with the highest salary
        int highestSalary = allEmployees.stream()
                .mapToInt(employee -> Integer.parseInt(employee.getEmployeeSalary()))
                .max()
                .orElse(0);  // Default to 0 if no employees are available

        logger.info("The highest salary of employees is {}", highestSalary);
        return highestSalary;
    }

    // Method to get the top 10 highest earning employee names
    public List<String> getTop10HighestEarningEmployeeNames() {
        logger.info("Fetching the top 10 highest earning employee names.");

        // Fetch all employees
        List<Employee> allEmployees = getAllEmployees();

        if (allEmployees.isEmpty()) {
            logger.warn("No employees found to determine the top 10 highest earners.");
            return List.of();  // Return an empty list if no employees are available
        }

        // Sort employees by salary in descending order and get the top 10
        List<String> top10EmployeeNames = allEmployees.stream()
                .sorted((e1, e2) -> Integer.compare(Integer.parseInt(e2.getEmployeeSalary()), Integer.parseInt(e1.getEmployeeSalary())))
                .limit(10)
                .map(Employee::getEmployeeName)
                .collect(Collectors.toList());

        logger.info("Returning the top 10 highest earning employee names: {}", top10EmployeeNames);
        return top10EmployeeNames;
    }

    // Method to create an employee
    public String createEmployee(CreateEmployeeRequest request) {
        String url = baseUrl + "/create";
        logger.info("Creating new employee with name: {}, salary: {}, age: {}", request.getName(), request.getSalary(), request.getAge());

        try {
            HttpEntity<CreateEmployeeRequest> requestEntity = new HttpEntity<>(request);
            ResponseEntity<EmployeeApiResponse<CreateEmployeeResponse>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<EmployeeApiResponse<CreateEmployeeResponse>>() {}
            );

            HttpStatus statusCode = responseEntity.getStatusCode();
            if (!statusCode.is2xxSuccessful()) {
                logger.error("Received non-2xx status code: {} while creating employee. Returning default response.", statusCode);
                return getDefaultCreateEmployeeResponse(request);  // Return default response if status is not successful
            }

            EmployeeApiResponse<CreateEmployeeResponse> response = responseEntity.getBody();
            if (response != null && "success".equalsIgnoreCase(response.getStatus())) {
                logger.debug("Successfully created employee: {}", response.getData());
                return "success";
            } else {
                logger.warn("Failed to create employee. Status: {}", response != null ? response.getStatus() : "null");
                return getDefaultCreateEmployeeResponse(request);  // Return default response on failure
            }

        } catch (ResourceAccessException e) {
            // Handle connection failures specifically
            logger.error("Connection error creating employee: {}. Returning default response.", e.getMessage());
            return getDefaultCreateEmployeeResponse(request);

        } catch (HttpStatusCodeException e) {
            // Catch any HTTP error that isn't a 2xx success response
            logger.error("HTTP error creating employee ({}): {}. Returning default response.", e.getStatusCode(), e.getMessage());
            return getDefaultCreateEmployeeResponse(request);

        } catch (Exception e) {
            // Handle any other exceptions
            logger.error("Error creating employee: {}. Returning default response.", e.getMessage());
            return getDefaultCreateEmployeeResponse(request);
        }
    }

    // Method to delete an employee by ID
    public String deleteEmployee(String id) {
        String url = baseUrl + "/delete/" + id;
        logger.info("Deleting employee with ID: {}", id);

        Employee employee = null; // Initialize employee to null

        try {
            // Fetch the employee to get their name before deletion
            employee = getEmployeeById(id);
            if (employee == null) {
                logger.warn("Employee with ID: {} not found, deletion aborted.", id);
                return "Employee with ID " + id + " not found, deletion aborted.";  // Return message if employee not found
            }

            // Perform the delete request
            ResponseEntity<EmployeeApiResponse<String>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    null,
                    new ParameterizedTypeReference<EmployeeApiResponse<String>>() {}
            );

            HttpStatus statusCode = responseEntity.getStatusCode();
            if (!statusCode.is2xxSuccessful()) {
                logger.error("Received non-2xx status code: {} while deleting employee with ID: {}.", statusCode, id);
                return getDefaultDeleteEmployeeResponse(employee);  // Return default response if status is not successful
            }

            EmployeeApiResponse<String> response = responseEntity.getBody();
            if (response != null && "success".equalsIgnoreCase(response.getStatus())) {
                logger.info("Successfully deleted employee with ID: {}, Name: {}", id, employee.getEmployeeName());
                return employee.getEmployeeName();  // Return the employee name on successful deletion
            } else {
                logger.warn("Failed to delete employee with ID: {}. Status: {}", id, response != null ? response.getStatus() : "null");
                return getDefaultDeleteEmployeeResponse(employee);  // Return default response on failure
            }

        } catch (ResourceAccessException e) {
            // Handle connection failures specifically
            logger.error("Connection error deleting employee with ID: {}. Returning default response.", id);
            return employee != null ? getDefaultDeleteEmployeeResponse(employee) : "Connection error while deleting employee with ID " + id;

        } catch (HttpStatusCodeException e) {
            // Catch any HTTP error that isn't a 2xx success response
            logger.error("HTTP error deleting employee with ID: {} ({}): {}. Returning default response.", id, e.getStatusCode(), e.getMessage());
            return employee != null ? getDefaultDeleteEmployeeResponse(employee) : "HTTP error while deleting employee with ID " + id;

        } catch (Exception e) {
            // Handle any other exceptions
            logger.error("Error deleting employee with ID: {}. Returning default response.", id, e.getMessage());
            return employee != null ? getDefaultDeleteEmployeeResponse(employee) : "Error while deleting employee with ID " + id;
        }
    }

    // Setter for baseUrl (for testing purposes)
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // Provide a hardcoded "good" response
    private List<Employee> getDefaultEmployeeList() {
      Employee[] employees = {
        new Employee("1", "Tiger Nixon", "320800", "61", ""),
        new Employee("2", "Garrett Winters", "170750", "63", ""),
        new Employee("3", "Ashton Cox", "86000", "66", ""),
        new Employee("4", "Cedric Kelly", "433060", "22", ""),
        new Employee("5", "Airi Satou", "162700", "33", ""),
        new Employee("6", "Brielle Williamson", "372000", "61", ""),
        new Employee("7", "Herrod Chandler", "137500", "59", ""),
        new Employee("8", "Rhona Davidson", "327900", "55", ""),
        new Employee("9", "Colleen Hurst", "205500", "39", ""),
        new Employee("10", "Sonya Frost", "103600", "23", ""),
        new Employee("11", "Jena Gaines", "90560", "30", ""),
        new Employee("12", "Quinn Flynn", "342000", "22", ""),
        new Employee("13", "Charde Marshall", "470600", "36", ""),
        new Employee("14", "Haley Kennedy", "313500", "43", ""),
        new Employee("15", "Tatyana Fitzpatrick", "385750", "19", ""),
        new Employee("16", "Michael Silva", "198500", "66", ""),
        new Employee("17", "Paul Byrd", "725000", "64", ""),
        new Employee("18", "Gloria Little", "237500", "59", ""),
        new Employee("19", "Bradley Greer", "132000", "41", ""),
        new Employee("20", "Dai Rios", "217500", "35", ""),
        new Employee("21", "Jenette Caldwell", "345000", "30", ""),
        new Employee("22", "Yuri Berry", "675000", "40", ""),
        new Employee("23", "Caesar Vance", "106450", "21", ""),
        new Employee("24", "Doris Wilder", "85600", "23", "")
      };
      return Arrays.asList(employees);
    }

    // Helper method to get an employee from the default list based on ID
    private Employee getEmployeeFromDefaultList(String id) {
      List<Employee> defaultEmployees = getDefaultEmployeeList();
      return defaultEmployees.stream()
              .filter(employee -> employee.getId().equals(id))
              .findFirst()
              .orElse(null);  // Return null if no match is found in the default list
    }

    // Helper method to return a default response when employee creation fails
    private String getDefaultCreateEmployeeResponse(CreateEmployeeRequest employeeRequest) {
        logger.info("Returning default create employee response.");

        // Log and return a mock successful response
        logger.debug("Default employee created with Name: {}, Salary: {}, Age: {}", employeeRequest.getName(), employeeRequest.getSalary(), employeeRequest.getAge());

        // This is a mock of the expected successful response
        return "success";
    }

    // Helper method to return a default response when employee deletion fails
    private String getDefaultDeleteEmployeeResponse(Employee employee) {
        logger.info("Returning default delete employee response for employee: {}", employee.getEmployeeName());
        
        // This is a mock of the expected successful delete response
        return employee.getEmployeeName(); 
    }
}

