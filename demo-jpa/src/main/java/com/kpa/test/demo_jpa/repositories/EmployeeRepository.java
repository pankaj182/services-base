package com.kpa.test.demo_jpa.repositories;

import com.kpa.test.demo_jpa.models.Employee;
import com.kpa.test.demo_jpa.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
}
