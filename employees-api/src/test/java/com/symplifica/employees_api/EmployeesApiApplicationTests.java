package com.symplifica.employees_api;

import com.symplifica.employees_api.model.Employee;
import com.symplifica.employees_api.repository.EmployeeRepository;
import com.symplifica.employees_api.service.EmployeeService;
import com.symplifica.employees_api.service.OpenStreetMapService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeesApiApplicationTests {

	@Mock
	private EmployeeRepository repository;

	@Mock
	private OpenStreetMapService openStreetMapService;

	@InjectMocks
	private EmployeeService service;

	@Test
	void getAll_returnsAllEmployees() {
		Employee emp1 = new Employee();
		emp1.setName("Maria Garcia");
		emp1.setEmail("maria@symplifica.com");
		emp1.setCity("Bogota");

		Employee emp2 = new Employee();
		emp2.setName("Carlos Mendez");
		emp2.setEmail("carlos@symplifica.com");
		emp2.setCity("Medellin");

		when(repository.findAll()).thenReturn(List.of(emp1, emp2));

		List<Employee> result = service.getAll();

		assertEquals(2, result.size());
		assertEquals("Maria Garcia", result.get(0).getName());
		verify(repository, times(1)).findAll();
	}

	@Test
	void create_savesAndReturnsEmployee() {
		Employee emp = new Employee();
		emp.setName("Ana Rodriguez");
		emp.setEmail("ana@symplifica.com");
		emp.setCity("Buenos Aires");

		when(repository.save(emp)).thenReturn(emp);

		Employee result = service.create(emp);

		assertNotNull(result);
		assertEquals("Ana Rodriguez", result.getName());
		verify(repository, times(1)).save(emp);
	}

	@Test
	void delete_callsRepositoryDeleteById() {
		service.delete(1L);
		verify(repository, times(1)).deleteById(1L);
	}

	@Test
	void update_throwsException_whenEmployeeNotFound() {
		when(repository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> {
			service.update(99L, new Employee());
		});
	}
}