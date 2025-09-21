package com.redis.cache_demo.service;

import com.redis.cache_demo.dto.ProductRecord;
import com.redis.cache_demo.entity.Product;
import com.redis.cache_demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductRecord> getAllProducts() {
        return productRepository.findAll().stream()
                .map(p -> new ProductRecord(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        p.getItems(),
                        p.getCreatedDt(),
                        p.getUpdatedDt(),
                        p.getVersion()
                ))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "PRODUCT_CACHE", key = "#id")
    public ProductRecord getProductById(java.util.UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return new ProductRecord(
                product.getId(), product.getName(), product.getPrice(), product.getItems(),
                product.getCreatedDt(), product.getUpdatedDt(), product.getVersion()
        );
    }

    @CachePut(value = "PRODUCT_CACHE", key = "#result.id")
    public ProductRecord addProduct(ProductRecord productRecord) {
        Product product = Product.builder()
                .name(productRecord.name())
                .price(productRecord.price())
                .items(productRecord.items())
                .build();

        Product saved = productRepository.save(product);
        return new ProductRecord(
                saved.getId(), saved.getName(), saved.getPrice(), saved.getItems(),
                saved.getCreatedDt(), saved.getUpdatedDt(), saved.getVersion()
        );
    }

    @CachePut(value = "PRODUCT_CACHE", key = "#productRecord.id")
    public ProductRecord updateProduct(ProductRecord productRecord) {
        Product product = productRepository.findById(productRecord.id())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(productRecord.name());
        product.setPrice(productRecord.price());
        product.setItems(productRecord.items());
        Product saved = productRepository.save(product);
        return new ProductRecord(
                saved.getId(), saved.getName(), saved.getPrice(), saved.getItems(),
                saved.getCreatedDt(), saved.getUpdatedDt(), saved.getVersion()
        );
    }

    @CacheEvict(value = "PRODUCT_CACHE", key = "#id")
    public void deleteProduct(java.util.UUID id) {
        productRepository.deleteById(id);
    }
}
