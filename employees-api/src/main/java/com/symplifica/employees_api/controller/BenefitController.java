package com.symplifica.employees_api.controller;

import com.symplifica.employees_api.model.Benefit;
import com.symplifica.employees_api.service.BenefitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees/{employeeId}/benefits")
public class BenefitController {

    private final BenefitService service;

    public BenefitController(BenefitService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Benefit>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(service.getByEmployee(employeeId));
    }

    @PostMapping
    public ResponseEntity<Benefit> create(@PathVariable Long employeeId, @RequestBody Benefit benefit) {
        return ResponseEntity.ok(service.create(employeeId, benefit));
    }

    @DeleteMapping("/{benefitId}")
    public ResponseEntity<Void> delete(@PathVariable Long benefitId) {
        service.delete(benefitId);
        return ResponseEntity.noContent().build();
    }
}