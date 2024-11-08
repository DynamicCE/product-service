package com.erkan.product_service.controller;

import com.erkan.product_service.dto.ProductRequestDto;
import com.erkan.product_service.dto.ProductResponseDto;
import com.erkan.product_service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createProduct_WhenValidProduct_ShouldReturnCreatedStatus() throws Exception {
        // given
        ProductRequestDto iphone = ProductRequestDto.builder()
                .name("iPhone 15 Pro")
                .description("Apple iPhone 15 Pro 256GB")
                .price(BigDecimal.valueOf(74999.99))
                .build();

        ProductResponseDto savedIphone = ProductResponseDto.builder()

                .name("iPhone 15 Pro")
                .description("Apple iPhone 15 Pro 256GB")
                .price(BigDecimal.valueOf(74999.99))
                .build();

        when(productService.createProduct(any(ProductRequestDto.class)))
                .thenReturn(savedIphone);

        // when & then
        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(iphone)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(iphone.getName()))
                .andExpect(jsonPath("$.price").value(74999.99));
    }

    @Test
    void createProduct_WhenInvalidPrice_ShouldReturnBadRequest() throws Exception {
        // given
        ProductRequestDto invalidProduct = ProductRequestDto.builder()
                .name("iPhone 15 Pro")
                .description("Apple iPhone 15 Pro 256GB")
                .price(BigDecimal.valueOf(-1000))
                .build();

        // when & then
        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }
}