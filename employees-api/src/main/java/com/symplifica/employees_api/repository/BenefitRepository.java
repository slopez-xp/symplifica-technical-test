package com.symplifica.employees_api.repository;

import com.symplifica.employees_api.model.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BenefitRepository extends JpaRepository<Benefit, Long> {
    List<Benefit> findByEmployeeId(Long employeeId);
}