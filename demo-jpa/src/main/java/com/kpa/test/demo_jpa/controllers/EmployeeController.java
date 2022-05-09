package com.kpa.test.demo_jpa.controllers;

import com.kpa.test.demo_jpa.models.Employee;
import com.kpa.test.demo_jpa.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    @Autowired
    EmployeeController(final EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/{name}")
    public Optional<Employee> getEmployeeById(@PathVariable String employeeId) {
        return employeeRepository.findById(employeeId);
    }

    @GetMapping("")
    public List<Employee> getAllPersons(@RequestParam(defaultValue = "100") int limit) {
        return employeeRepository.findAll();
    }

    @PutMapping("")
    public Employee saveEmployee(@RequestBody Employee employee) {
        return employeeRepository.save(employee);
    }
}
