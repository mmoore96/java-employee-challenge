package com.example.rqchallenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateEmployeeRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("salary")
    private String salary;

    @JsonProperty("age")
    private String age;

    // Default constructor
    public CreateEmployeeRequest() {
    }

    // Parameterized constructor
    public CreateEmployeeRequest(String name, String salary, String age) {
        this.name = name;
        this.salary = salary;
        this.age = age;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}

