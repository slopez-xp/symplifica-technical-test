package com.symplifica.employees_api.model;

public class EmployeeDetail {
    private Employee employee;
    private LocationData location;

    public EmployeeDetail(Employee employee, LocationData location) {
        this.employee = employee;
        this.location = location;
    }

    public Employee getEmployee() { return employee; }
    public LocationData getLocation() { return location; }
}