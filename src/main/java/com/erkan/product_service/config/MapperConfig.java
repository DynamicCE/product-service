package com.erkan.product_service.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.erkan.product_service.mapper.ProductMapper;

@Configuration
public class MapperConfig {

    @Bean
    public ProductMapper productMapper() {
        return Mappers.getMapper(ProductMapper.class);
    }
}