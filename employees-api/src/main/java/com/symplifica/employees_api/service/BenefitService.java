package com.symplifica.employees_api.service;

import com.symplifica.employees_api.model.Benefit;
import com.symplifica.employees_api.model.Employee;
import com.symplifica.employees_api.repository.BenefitRepository;
import com.symplifica.employees_api.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BenefitService {

    private final BenefitRepository benefitRepository;
    private final EmployeeRepository employeeRepository;

    public BenefitService(BenefitRepository benefitRepository, EmployeeRepository employeeRepository) {
        this.benefitRepository = benefitRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Benefit> getByEmployee(Long employeeId) {
        return benefitRepository.findByEmployeeId(employeeId);
    }

    public Benefit create(Long employeeId, Benefit benefit) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        benefit.setEmployee(employee);
        return benefitRepository.save(benefit);
    }

    public void delete(Long benefitId) {
        benefitRepository.deleteById(benefitId);
    }
}