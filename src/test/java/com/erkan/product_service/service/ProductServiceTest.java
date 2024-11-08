package com.erkan.product_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.erkan.product_service.dto.ProductRequestDto;
import com.erkan.product_service.dto.ProductResponseDto;
import com.erkan.product_service.exception.InvalidProductDataException;
import com.erkan.product_service.mapper.ProductMapper;
import com.erkan.product_service.model.Product;
import com.erkan.product_service.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_WhenValidProduct_ShouldReturnProductResponseDto() {
        // given
        ProductRequestDto iphone = ProductRequestDto.builder()
                .name("iPhone 15 Pro")
                .description("Apple iPhone 15 Pro 256GB")
                .price(BigDecimal.valueOf(74999.99))
                .build();

        Product savedProduct = Product.builder()
                .id("65f2b3d40b9c1e5a0e8b4567")
                .name("iPhone 15 Pro")
                .description("Apple iPhone 15 Pro 256GB")
                .price(BigDecimal.valueOf(74999.99))
                .build();

        ProductResponseDto expectedResponse = ProductResponseDto.builder()
                .name("iPhone 15 Pro")
                .description("Apple iPhone 15 Pro 256GB")
                .price(BigDecimal.valueOf(74999.99))
                .build();

        // when
        when(productMapper.toEntity(iphone)).thenReturn(savedProduct);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toResponseDto(savedProduct)).thenReturn(expectedResponse);

        // then
        ProductResponseDto result = productService.createProduct(iphone);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(iphone.getName());
        assertThat(result.getPrice()).isEqualTo(iphone.getPrice());

        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).toEntity(iphone);
        verify(productMapper, times(1)).toResponseDto(savedProduct);
    }

    @Test
    void createProduct_WhenEmptyName_ShouldThrowInvalidProductDataException() {
        // given
        ProductRequestDto invalidProduct = ProductRequestDto.builder()
                .name("")
                .description("Apple iPhone 15 Pro 256GB")
                .price(BigDecimal.valueOf(74999.99))
                .build();

        // when & then
        assertThrows(InvalidProductDataException.class,
                () -> productService.createProduct(invalidProduct),
                "Product name cannot be empty");
    }

    @Test
    void createProduct_WhenNegativePrice_ShouldThrowInvalidProductDataException() {
        // given
        ProductRequestDto invalidProduct = ProductRequestDto.builder()
                .name("iPhone 15 Pro")
                .description("Apple iPhone 15 Pro 256GB")
                .price(BigDecimal.valueOf(-1000))
                .build();

        // when & then
        assertThrows(InvalidProductDataException.class,
                () -> productService.createProduct(invalidProduct),
                "Product price cannot be negative or zero");
    }
}