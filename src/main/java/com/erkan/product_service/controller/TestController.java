package com.erkan.product_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.mongodb.core.MongoTemplate;

@RestController
public class TestController {

    private final MongoTemplate mongoTemplate;

    public TestController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("/test")
    public String testConnection() {
        try {
            mongoTemplate.getDb().getName();
            return "MongoDB bağlantısı başarılı!";
        } catch (Exception e) {
            return "Hata: " + e.getMessage();
        }
    }
}