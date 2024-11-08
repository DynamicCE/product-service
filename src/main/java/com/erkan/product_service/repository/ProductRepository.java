package com.erkan.product_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.erkan.product_service.model.Product;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

}
