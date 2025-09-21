package com.redis.cache_demo.controller;

import com.redis.cache_demo.dto.ProductRecord;
import com.redis.cache_demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductRecord> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{productId}")
    public ProductRecord getProduct(@PathVariable("productId") java.util.UUID productId) {
        return productService.getProductById(productId);
    }

    @PostMapping
    public ProductRecord addProduct(@RequestBody ProductRecord productRecord) {
        return productService.addProduct(productRecord);
    }

    @PutMapping
    public ProductRecord updateProduct(@RequestBody ProductRecord productRecord) {
        return productService.updateProduct(productRecord);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable("id") java.util.UUID id) {
        productService.deleteProduct(id);
    }
}
