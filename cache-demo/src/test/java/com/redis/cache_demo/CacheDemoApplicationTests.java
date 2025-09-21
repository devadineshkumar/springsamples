package com.redis.cache_demo;

import com.redis.cache_demo.entity.Product;
import com.redis.cache_demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.CacheManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CacheDemoApplicationTests {

	@Container
	@ServiceConnection
	static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:latest")).withExposedPorts(6379);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CacheManager cacheManager;


	@BeforeEach
	public void setUp() {
		redis.start();
		productRepository.deleteAll();
	}


	@Test
	public void testCacheableAnnotation() throws Exception {
		// Create a new product
		Product product = new Product();
		product.setName("Test Product");
		product.setPrice(100.0);
		product.setItems(10);

		productRepository.save(product);

		// Verify that the cache is empty
		org.springframework.cache.Cache cache = cacheManager.getCache("PRODUCT_CACHE");
        assertThat(cache).isNotNull();
        assertThat(cache.get(product.getId())).isNull();

		// Get the product by ID and verify that it is cached
		mockMvc.perform(MockMvcRequestBuilders.get("/products/" + product.getId()))
				.andExpect(status().isOk());
		assertThat(cache.get(product.getId())).isNotNull();

		// Get the product by ID again and verify that it is cached
		mockMvc.perform(MockMvcRequestBuilders.get("/products/" + product.getId()))
				.andExpect(status().isOk());
		assertThat(cache.get(product.getId())).isNotNull();

		// Verify that the repository was only called once
		verify(productRepository, times(1)).findById(Mockito.any(UUID.class));
	}

}
