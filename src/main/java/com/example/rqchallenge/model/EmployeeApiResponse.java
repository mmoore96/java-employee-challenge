package com.example.rqchallenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmployeeApiResponse<T> {

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private T data;

    // Default constructor
    public EmployeeApiResponse() {
    }

    // Parameterized constructor
    public EmployeeApiResponse(String status, T data) {
        this.status = status;
        this.data = data;
    }

    // Getters and Setters

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status= status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data= data;
    }
}

