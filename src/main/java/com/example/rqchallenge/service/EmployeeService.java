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
import org.springframework.core.ParameterizedTypeReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            HttpStatus statusCode = responseEntity.getStatusCode();  // Use HttpStatus instead of HttpStatusCode
            if (!statusCode.is2xxSuccessful()) {
                logger.error("Received non-2xx status code: {}", statusCode);
                return List.of();  // Return empty list instead of throwing an exception
            }

            EmployeeApiResponse<List<Employee>> response = responseEntity.getBody();

            if (response != null && "success".equalsIgnoreCase(response.getStatus())) {
                logger.debug("Successfully retrieved {} employees.", response.getData().size());
                return response.getData();
            } else {
                logger.warn("Failed to retrieve employees. Status: {}", response != null ? response.getStatus() : "null");
                return List.of();
            }
        } catch (HttpStatusCodeException e) {
            // TODO: Create a DefaultEmployeeList class to handle this fallback
            // Catch any HTTP error that isn't a 2xx success response
            logger.error("HTTP error fetching employees ({}): {}. Falling back to default employee list.",
                    e.getStatusCode(), e.getMessage());
            return List.of();  // Fallback to empty employee list
        } catch (Exception e) {
            logger.error("Error fetching employees: {}", e.getMessage(), e);
            return List.of();  // Return empty list on any other error
        }
    }

    // Setter for baseUrl (for testing purposes)
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}

