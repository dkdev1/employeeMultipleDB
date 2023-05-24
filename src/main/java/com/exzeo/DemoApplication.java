package com.exzeo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.exzeo.dto.EmployeeDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import com.exzeo.service.EmployeeService;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {
	
	public static final Log LOGGER = LogFactory.getLog(DemoApplication.class);

	@Autowired
	EmployeeService customerLoanService;

	@Autowired
	Environment environment;


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		transferDataFromOneDbToAnother();
	}

	private void transferDataFromOneDbToAnother() {
		// Create data source for student2 database
		DriverManagerDataSource destinationDataSource = new DriverManagerDataSource();
		destinationDataSource.setDriverClassName(environment.getProperty("spring.driverclass"));
		destinationDataSource.setUrl(environment.getProperty("spring.second-datasource.url"));
		destinationDataSource.setUsername(environment.getProperty("spring.second-datasource.username"));
		destinationDataSource.setPassword(environment.getProperty("spring.second-datasource.password"));

		// Create JDBC templates using data sources
		JdbcTemplate destinationJdbcTemplate = new JdbcTemplate(destinationDataSource);

		// Copy data from emp1 to emp2
		EmployeeDTO employeeDTO = getEmployeeData();
		int id = employeeDTO.getId();
		int age = employeeDTO.getAge();
		String name = employeeDTO.getName();

		String insertQuery = "INSERT INTO employee2 (id, name, age) VALUES (?, ?, ?)";

		destinationJdbcTemplate.update(insertQuery,id,name,age);
		System.out.println("Data copy completed successfully.");

	}


	private void copyData(EmployeeDTO employeeDTO) {
		try {
			customerLoanService.copyData(employeeDTO);
			LOGGER.info("Data is saved successfully check in db");
		} catch (Exception e) {
			String message = environment.getProperty(e.getMessage(),"Some exception occurred. Please check log file for more details!!");
			LOGGER.info(message);
		}
	}

	private EmployeeDTO getEmployeeData() {
		try {
			EmployeeDTO employeeDTO=customerLoanService.getData(1);
			LOGGER.info(employeeDTO);
			return employeeDTO;
		} catch (Exception e) {
			String message = environment.getProperty(e.getMessage(),"Some exception occured. Please check log file for more details!!");
			LOGGER.info(message);
		}
		return null;
	}

}


