package com.chanochoca.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.chanochoca.app.model.Product;
import com.chanochoca.app.model.dto.Status;
import com.chanochoca.app.repository.ProductRepository;

import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProductApplicationTests {
	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
			.withDatabaseName("testing")
			.withUsername("root")
			.withPassword("test");

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository repository;

	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
	}

	@Test
	void shouldCreateProduct() throws Exception {
		Product product = getProduct();

		mockMvc.perform(
			MockMvcRequestBuilders.post("/products")
			    .contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(product))
		).andExpect(status().isCreated());

		Assertions.assertEquals(1, repository.count());
	}

	private Product getProduct() {
		Product p = new Product();
		p.setName("iPhone 17");
		p.setDescription("iPhone 17");
		p.setPrice(1200.0);
		p.setStatus(Status.CREATED);
		return p;
	}
}
