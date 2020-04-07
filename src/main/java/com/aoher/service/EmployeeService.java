package com.aoher.service;

import com.aoher.model.Employee;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class EmployeeService {

    public static final String PATH_PARAM_BY_ID = "/employee/{id}";
    public static final String ADD_EMPLOYEE = "/employee";

    private WebClient webClient;

    public EmployeeService(WebClient webClient) {
        this.webClient = webClient;
    }

    public EmployeeService(String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    public Mono<Employee> getEmployeeById(Integer empId) {
        return webClient.get()
                .uri(PATH_PARAM_BY_ID, empId)
                .retrieve()
                .bodyToMono(Employee.class);
    }

    public Mono<Employee> addNewEmployee(Employee newEmployee) {
        return webClient.post()
                .uri(ADD_EMPLOYEE)
                .syncBody(newEmployee)
                .retrieve()
                .bodyToMono(Employee.class);
    }

    public Mono<Employee> updateEmployee(Integer empId, Employee updatedEmployee) {
        return webClient.put()
                .uri(PATH_PARAM_BY_ID, empId)
                .syncBody(updatedEmployee)
                .retrieve()
                .bodyToMono(Employee.class);
    }

    public Mono<String> deleteEmployeeById(Integer empId) {
        return webClient.delete()
                .uri(PATH_PARAM_BY_ID, empId)
                .retrieve()
                .bodyToMono(String.class);
    }
}
