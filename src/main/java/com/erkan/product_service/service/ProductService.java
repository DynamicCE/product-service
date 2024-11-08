package com.erkan.product_service.service;

import org.springframework.stereotype.Service;

import com.erkan.product_service.dto.ProductRequestDto;
import com.erkan.product_service.model.Product;
import com.erkan.product_service.repository.ProductRepository;

import com.erkan.product_service.exception.InvalidProductDataException;
import java.math.BigDecimal;
import com.erkan.product_service.mapper.ProductMapper;
import com.erkan.product_service.dto.ProductResponseDto;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        if (productRequestDto.getName() == null || productRequestDto.getName().isEmpty()) {
            throw new InvalidProductDataException("Product name cannot be empty");
        }
        if (productRequestDto.getDescription() == null || productRequestDto.getDescription().isEmpty()) {
            throw new InvalidProductDataException("Product description cannot be empty");
        }
        if (productRequestDto.getPrice() == null || productRequestDto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidProductDataException("Product price cannot be negative or zero");
        }
        Product product = productMapper.toEntity(productRequestDto);
        productRepository.save(product);
        return productMapper.toResponseDto(product);
    }

    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(productMapper::toResponseDto)
                .collect(Collectors.toList());
    }

}
