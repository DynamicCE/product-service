package com.erkan.product_service.integration;

import com.erkan.product_service.dto.ProductRequestDto;
import com.erkan.product_service.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ProductIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveProduct() throws Exception {
        // given - hazırlık aşaması
        ProductRequestDto iphone = ProductRequestDto.builder()
                .name("iPhone 15 Pro")
                .description("Apple iPhone 15 Pro 256GB")
                .price(BigDecimal.valueOf(74999.99))
                .build();

        // when - ürün oluşturma isteği
        MvcResult createResult = mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(iphone)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(iphone.getName()))
                .andExpect(jsonPath("$.price").value(74999.99))
                .andReturn();

        // then - veritabanı kontrolü
        assertThat(productRepository.findAll())
                .hasSize(1)
                .first()
                .satisfies(product -> {
                    assertThat(product.getName()).isEqualTo(iphone.getName());
                    assertThat(product.getPrice()).isEqualTo(iphone.getPrice());
                });

        // and when - ürünleri listeleme isteği
        mockMvc.perform(get("/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(iphone.getName()))
                .andExpect(jsonPath("$[0].price").value(74999.99));
    }

    @Test
    void shouldHandleMultipleProducts() throws Exception {
        // given - iki farklı ürün oluştur
        ProductRequestDto iphone = ProductRequestDto.builder()
                .name("iPhone 15 Pro")
                .description("Apple iPhone 15 Pro 256GB")
                .price(BigDecimal.valueOf(74999.99))
                .build();

        ProductRequestDto macbook = ProductRequestDto.builder()
                .name("MacBook Pro")
                .description("Apple MacBook Pro M3 Max")
                .price(BigDecimal.valueOf(124999.99))
                .build();

        // when - ürünleri kaydet
        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(iphone)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(macbook)))
                .andExpect(status().isCreated());

        // then - veritabanı kontrolü
        assertThat(productRepository.findAll()).hasSize(2);

        // and when - ürünleri listele
        mockMvc.perform(get("/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value(iphone.getName()))
                .andExpect(jsonPath("$[1].name").value(macbook.getName()));
    }
}