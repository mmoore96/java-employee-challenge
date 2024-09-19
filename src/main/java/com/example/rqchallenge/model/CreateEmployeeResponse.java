package com.example.rqchallenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateEmployeeResponse {

    @JsonProperty("name")
    private String name;

    @JsonProperty("salary")
    private String salary;

    @JsonProperty("age")
    private String age;

    @JsonProperty("id")
    private String id;

    // Default constructor
    public CreateEmployeeResponse() {
    }

    // Parameterized constructor
    public CreateEmployeeResponse(String name, String salary, String age, String id) {
        this.name = name;
        this.salary = salary;
        this.age = age;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

