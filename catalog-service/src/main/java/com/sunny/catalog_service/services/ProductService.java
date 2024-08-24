package com.sunny.catalog_service.services;

import com.sunny.catalog_service.entities.Product;
import com.sunny.catalog_service.repositories.ProductRepository;
import com.sunny.catalog_service.utils.MyThreadLocalsHolder;
import com.sunny.catalog_service.web.models.ProductInventoryResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryServiceClient inventoryServiceClient;

    @Autowired
    private RestTemplate restTemplate;

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findProductByCode(String code) {
        Optional<Product> productOptional = productRepository.findByCode(code);

        if (productOptional.isPresent()) {
            String correlationId = UUID.randomUUID().toString();
            MyThreadLocalsHolder.setCorrelationId(correlationId);

            log.info("Before CorrelationID: " + MyThreadLocalsHolder.getCorrelationId());
            log.info("Fetching inventory level for product_code: " + code);

            Optional<ProductInventoryResponse> itemResponseEntity =
                    this.inventoryServiceClient.getProductInventoryByCode(code);
            if (itemResponseEntity.isPresent()) {
                Integer quantity = itemResponseEntity.get().getAvailableQuantity();
                productOptional.get().setInStock(quantity > 0);
            }

            log.info("After CorrelationID: " + MyThreadLocalsHolder.getCorrelationId());
        }

        return productOptional;
    }
}
