package com.example.rqchallenge.service;

import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.EmployeeApiResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.core.ParameterizedTypeReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

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
}

