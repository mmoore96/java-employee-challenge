package com.example.rqchallenge.controller;

import com.example.rqchallenge.model.HealthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HealthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testHealthCheck() {
        // Act
        ResponseEntity<HealthResponse> responseEntity =
                restTemplate.getForEntity("/v1/health", HealthResponse.class);

        // Assert
        assertEquals(200, responseEntity.getStatusCodeValue(), "Expected HTTP status 200");

        HealthResponse healthResponse = responseEntity.getBody();
        assertNotNull(healthResponse, "Response body should not be null");
        assertEquals("available", healthResponse.getStatus(), "Status should be 'available'");
        assertNotNull(healthResponse.getTimestamp(), "Timestamp should not be null");

        // Validate the timestamp format
        try {
            OffsetDateTime.parse(healthResponse.getTimestamp(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e) {
            fail("Timestamp is not in the correct ISO 8601 format");
        }
    }
}

