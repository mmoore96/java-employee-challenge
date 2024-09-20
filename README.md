# Coding Challenge

### In this assessment you will be tasked with filling out the functionality of different methods that will be listed further down.
These methods will require some level of api interactions with the following base url: https://dummy.restapiexample.com.
Please keep the following in mind when doing this assessment: clean coding practices, test driven development, logging, and scalability.
If you are unable to successfully receive responses from the endpoints, mocking the response calls may prove to be helpful.

### Endpoints to implement

getAllEmployees()

    output - list of employees
    description - this should return all employees

getEmployeesByNameSearch()

    output - list of employees
    description - this should return all employees whose name contains or matches the string input provided

getEmployeeById(string id)

    output - employee
    description - this should return a single employee

getHighestSalaryOfEmployees()

    output - integer of the highest salary
    description -  this should return a single integer indicating the highest salary of all employees

getTop10HighestEarningEmployeeNames()

    output - list of employees
    description -  this should return a list of the top 10 employees based off of their salaries

createEmployee(string name, string salary, string age)

    output - string of the status (i.e. success)
    description -  this should return a status of success or failed based on if an employee was created

deleteEmployee(String id)

    output - the name of the employee that was deleted
    description - this should delete the employee with specified id given

### External endpoints from base url
#### This section will outline all available endpoints and their request and response models from https://dummy.restapiexample.com
/employees

    request:
        method: GET
        parameters: n/a
        full route: https://dummy.restapiexample.com/api/v1/employees
    response:
        {
            "status": "success",
            "data": [
                {
                "id": "1",
                "employee_name": "Tiger Nixon",
                "employee_salary": "320800",
                "employee_age": "61",
                "profile_image": ""
                },
                ....
            ]
        }

/employee/{id}

    request:
        method: GET
        parameters: 
            id (String)
        full route: https://dummy.restapiexample.com/api/v1/employee/{id}
    response: 
        {
            "status": "success",
            "data": {
                "id": "1",
                "employee_name": "Foo Bar",
                "employee_salary": "320800",
                "employee_age": "61",
                "profile_image": ""
            }
        }

/create

    request:
        method: POST
        parameters: 
            name (String),
            salary (String),
            age (String)
        full route: https://dummy.restapiexample.com/api/v1/create
    response:
        {
            "status": "success",
            "data": {
                "name": "test",
                "salary": "123",
                "age": "23",
                "id": 25
            }
        }

/delete/{id}

    request:
        method: DELETE
        parameters:
            id (String)
        full route: https://dummy.restapiexample.com/api/v1/delete/{id}
    response:
        {
            "status": "success",
            "message": "successfully! deleted Record"
        }

# Project Setup and Run Instructions

This guide will walk you through how to build, run, test, and manage this Java project using the provided Makefile and Docker. If you want to quickly build, run, and access the project, you can simply use:

```bash
make
```

This command will run `make all`, which builds the Docker image and starts the service.

## Accessing the Swagger UI
Once the project is running, you can access the Swagger UI to explore the API documentation at:

- [http://localhost:8080/](http://localhost:8080/)
- [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Prerequisites
Before you begin, ensure you have the following installed on your system:
- [Docker](https://www.docker.com/products/docker-desktop) (including Docker Compose) for **ARM architecture** machines.
- [Gradle](https://gradle.org/install/) (for local build and run).
- [Java 11](https://adoptopenjdk.net/) or compatible version (if running locally).
- **Make**: If `make` is not installed, you can install it via your package manager:
  - **macOS**: Install via Homebrew with `brew install make`.
  - **Linux**: Install via your package manager, e.g., `sudo apt-get install make` (Debian/Ubuntu) or `sudo yum install make` (RedHat/CentOS).

## Makefile Targets

The following Makefile targets are available to manage the project:

### 1. **Build Docker Image**
Build the Docker image using Docker Compose and start the service.

```bash
make build
```

This command will:
- Build the Docker image from the `docker-compose.yml` file.
- Start the service in detached mode (`-d`).

### 2. **Start the Service**
Start the Docker containers without rebuilding the image.

```bash
make start
```

This command will:
- Start the service in detached mode, if it is not already running.

### 3. **Build Docker Image and Start the Service**
Build the Docker image using Docker Compose and start the service.

```bash
make build-and-start
```
or
```bash
make all
```

These commands will:
- Build the Docker image from the `docker-compose.yml` file.
- Start the service in detached mode (`-d`).

### 4. **Stop the Service**
Stop the running Docker containers.

```bash
make stop
```

This command will:
- Stop and remove the containers defined in the `docker-compose.yml`.

### 5. **View Logs**
View logs for the running service.

```bash
make logs
```

This command will:
- Display the logs of the `java-app` service in real time. You can change `SERVICE_NAME` in the Makefile if your service name differs.

### 6. **Clean Docker Environment**
Remove containers, networks, and volumes created by Docker Compose.

```bash
make clean
```

This command will:
- Stop the service and remove the containers, networks, and associated volumes.

### 7. **Rebuild and Restart the Service**
Rebuild the Docker image and restart the service.

```bash
make restart
```

This command will:
- Stop the service.
- Rebuild the Docker image and restart the service.

### 8. **Build Locally with Gradle**
Build the project locally using Gradle.

```bash
make build-local
```

This command will:
- Run the `./gradlew build` command to build the project locally.

### 9. **Run Locally with Gradle**
Run the project locally using Gradle.

```bash
make run-local
```

This command will:
- Run the `./gradlew bootRun` command to start the application locally.

### 10. **Clean Local Project**
Clean the local build files and cache using Gradle.

```bash
make clean-local
```

This command will:
- Run `./gradlew clean` to remove local build files.

### 11. **Run Tests**
Run the test suite.

```bash
make test
```

This command will:
- Run `./gradlew cleanTest test` to clean the test directory and execute the test suite.

## Notes
- The default project name is `java-employee-challenge`. You can modify this in the Makefile if you want a different project name.
- The default service name is `employee-api`. You can modify this in the Makefile if you want a different service name.
- The default port for the service is `8080`. You can modify this in the `docker-compose.yml` file if you want a different port.
- Ensure Docker is running when executing Docker-related commands (`build`, `start`, `stop`, etc.).
- If `make` is not installed, follow the instructions under "Prerequisites" to install it.
- This project is designed to run on ARM architecture (if using Docker). Ensure that your Docker setup is compatible with this architecture

