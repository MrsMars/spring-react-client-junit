package com.aoher.service;

import com.aoher.enums.Role;
import com.aoher.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class EmployeeServiceIntegrationTest {

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String JSON_APPLICATION = "application/json";

    public static MockWebServer mockBackEnd;
    private EmployeeService employeeService;
    private ObjectMapper MAPPER = new ObjectMapper();

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void init() {
        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        employeeService = new EmployeeService(baseUrl);
    }

    @Test
    void getEmployeeById() throws Exception {
        Employee mockEmployee = new Employee(100, "Adam", "Sandler", 32, Role.LEAD_ENGINEER);
        mockBackEnd.enqueue(new MockResponse().setBody(MAPPER.writeValueAsString(mockEmployee))
                .addHeader(CONTENT_TYPE_HEADER, JSON_APPLICATION));

        Mono<Employee> employeeMono = employeeService.getEmployeeById(mockEmployee.getEmployeeId());

        StepVerifier.create(employeeMono)
                .expectNextMatches(employee -> employee.getRole().equals(mockEmployee.getRole()))
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/employee/" + mockEmployee.getEmployeeId().toString(), recordedRequest.getPath());
    }

    @Test
    void addNewEmployee() throws Exception {
        Employee newEmployee = new Employee(null, "Adam", "Sandler", 32, Role.LEAD_ENGINEER);
        Employee webClientResponse = new Employee(100, "Adam", "Sandler", 32, Role.LEAD_ENGINEER);
        mockBackEnd.enqueue(new MockResponse().setBody(MAPPER.writeValueAsString(webClientResponse))
                .addHeader(CONTENT_TYPE_HEADER, JSON_APPLICATION));

        Mono<Employee> employeeMono = employeeService.addNewEmployee(newEmployee);

        StepVerifier.create(employeeMono)
                .expectNextMatches(employee -> employee.getEmployeeId().equals(webClientResponse.getEmployeeId()))
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/employee", recordedRequest.getPath());
    }

    @Test
    void updateEmployee() throws Exception {
        Employee updateEmployee = new Employee(100, "Adam", "Sandler New", 33, Role.LEAD_ENGINEER);
        mockBackEnd.enqueue(new MockResponse().setBody(MAPPER.writeValueAsString(updateEmployee))
                .addHeader(CONTENT_TYPE_HEADER, JSON_APPLICATION));

        Mono<Employee> updatedEmployee = employeeService.updateEmployee(updateEmployee.getEmployeeId(), updateEmployee);

        StepVerifier.create(updatedEmployee)
                .expectNextMatches(employee -> employee.getLastName().equals(updateEmployee.getLastName()) && employee.getAge().equals(updateEmployee.getAge()))
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("PUT", recordedRequest.getMethod());
        assertEquals("/employee/" + updateEmployee.getEmployeeId(), recordedRequest.getPath());
    }

    @Test
    void deleteEmployee() throws Exception {
        String responseMessage = "Employee Deleted SuccessFully";
        Integer employeeId = 100;
        mockBackEnd.enqueue(new MockResponse().setBody(MAPPER.writeValueAsString(responseMessage))
                .addHeader(CONTENT_TYPE_HEADER, JSON_APPLICATION));

        Mono<String> deletedEmployee = employeeService.deleteEmployeeById(employeeId);

        StepVerifier.create(deletedEmployee)
                .expectNext(String.format("\"%s\"", responseMessage))
                .verifyComplete();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("DELETE", recordedRequest.getMethod());
        assertEquals("/employee/" + employeeId, recordedRequest.getPath());
    }
}
