package com.erkan.product_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.erkan.product_service.dto.ProductRequestDto;
import com.erkan.product_service.dto.ProductResponseDto;
import com.erkan.product_service.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductRequestDto productRequestDto);

    ProductResponseDto toResponseDto(Product product);
}
