package com.aoher.service;

import com.aoher.enums.Role;
import com.aoher.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceUnitTest {

    EmployeeService employeeService;


    @Mock
    private WebClient webClientMock;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersMock;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;

    @Mock
    private WebClient.RequestBodySpec requestBodyMock;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriMock;

    @Mock
    private WebClient.ResponseSpec responseMock;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(webClientMock);
    }

    @Test
    void givenEmployeeId_whenGetEmployeeById_thenReturnEmployee() {
        Employee mockEmployee = new Employee(100, "Adam", "Sandler", 32, Role.LEAD_ENGINEER);
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri("/employee/{id}", mockEmployee.getEmployeeId())).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(Employee.class)).thenReturn(Mono.just(mockEmployee));

        Mono<Employee> employeeMono = employeeService.getEmployeeById(mockEmployee.getEmployeeId());
        StepVerifier.create(employeeMono)
                .expectNextMatches(employee -> employee.getRole().equals(mockEmployee.getRole()))
                .verifyComplete();
    }

    @Test
    void givenEmployee_whenAddEmployee_thenAddNewEmployee() {
        Employee newEmployee = new Employee(null, "Adam", "Sandler", 32, Role.LEAD_ENGINEER);
        Employee webClientResponse = new Employee(100, "Adam", "Sandler", 32, Role.LEAD_ENGINEER);

        when(webClientMock.post()).thenReturn(requestBodyUriMock);
        when(requestBodyUriMock.uri(EmployeeService.ADD_EMPLOYEE)).thenReturn(requestBodyMock);
        when(requestBodyMock.syncBody(newEmployee)).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(Employee.class)).thenReturn(Mono.just(webClientResponse));

        Mono<Employee> employeeMono = employeeService.addNewEmployee(newEmployee);

        StepVerifier.create(employeeMono)
                .expectNextMatches(employee -> employee.getEmployeeId().equals(webClientResponse.getEmployeeId()))
                .verifyComplete();
    }

    @Test
    void givenEmployee_whenUpdateEmployee_thenUpdatedEmployee() {
        Employee updateEmployee = new Employee(100, "Adam", "Sandler New", 33, Role.LEAD_ENGINEER);
        when(webClientMock.put()).thenReturn(requestBodyUriMock);
        when(requestBodyUriMock.uri(EmployeeService.PATH_PARAM_BY_ID, updateEmployee.getEmployeeId()))
                .thenReturn(requestBodyMock);
        when(requestBodyMock.syncBody(updateEmployee)).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(Employee.class)).thenReturn(Mono.just(updateEmployee));

        Mono<Employee> updatedEmployee = employeeService.updateEmployee(updateEmployee.getEmployeeId(), updateEmployee);

        StepVerifier.create(updatedEmployee)
                .expectNextMatches(employee -> employee.getLastName().equals(updateEmployee.getLastName()) && employee.getAge().equals(updateEmployee.getAge()))
                .verifyComplete();
    }

    @Test
    void givenEmployee_whenDeleteEmployeeById_thenDeleteSuccessful() {
        Integer id = 100;

        String responseMessage = "Employee Deleted SuccessFully";
        when(webClientMock.delete()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri(EmployeeService.PATH_PARAM_BY_ID, id)).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(String.class)).thenReturn(Mono.just(responseMessage));

        Mono<String> deletedEmployee = employeeService.deleteEmployeeById(id);

        StepVerifier.create(deletedEmployee)
                .expectNext(responseMessage)
                .verifyComplete();
    }
}