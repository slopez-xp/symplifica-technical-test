package com.symplifica.employees_api.service;

import com.symplifica.employees_api.model.Employee;
import com.symplifica.employees_api.model.EmployeeDetail;
import com.symplifica.employees_api.model.LocationData;
import com.symplifica.employees_api.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;
    private final OpenStreetMapService openStreetMapService;

    public EmployeeService(EmployeeRepository repository, OpenStreetMapService openStreetMapService) {
        this.repository = repository;
        this.openStreetMapService = openStreetMapService;
    }

    public List<Employee> getAll() {
        return repository.findAll();
    }

    public Employee create(Employee employee) {
        return repository.save(employee);
    }

    public EmployeeDetail getById(Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        LocationData location = openStreetMapService.getLocation(employee.getCity());
        return new EmployeeDetail(employee, location);
    }

    public Employee update(Long id, Employee updated) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setName(updated.getName());
        employee.setEmail(updated.getEmail());
        employee.setCity(updated.getCity());
        return repository.save(employee);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

}
