package com.sunny.catalog_service.services;

import com.sunny.catalog_service.entities.Product;
import com.sunny.catalog_service.repositories.ProductRepository;
import com.sunny.catalog_service.web.models.ProductInventoryResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findProductByCode(String code) {
        Optional<Product> productOptional = productRepository.findByCode(code);

        if (productOptional.isPresent()) {
            log.info("Fetching inventory level for product_code: " + code);
            ResponseEntity<ProductInventoryResponse> productInventoryResponse = restTemplate.getForEntity("http://inventory-service/api/inventory/" + code, ProductInventoryResponse.class);

            if (productInventoryResponse.getStatusCode().is2xxSuccessful()) {
                Integer quantity = productInventoryResponse.getBody().getAvailableQuantity();
                log.info("Available quantity: " + quantity);
                productOptional.get().setInStock(quantity > 0);
            } else {
                log.error("Unable to get inventory level for product_code: " + code +
                        ", StatusCode: " + productInventoryResponse.getStatusCode());
            }
        }

        return productOptional;
    }
}
