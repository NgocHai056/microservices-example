package com.sunny.catalog_service.services;

import com.sunny.catalog_service.utils.MyThreadLocalsHolder;
import com.sunny.catalog_service.web.models.ProductInventoryResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Slf4j
public class InventoryServiceClient {
    private static final String INVENTORY_API_PATH = "http://inventory-service/api/";
    @Autowired
    private RestTemplate restTemplate;

    /*@HystrixCommand(fallbackMethod = "getProductInventoryByCodeFallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60")
            }
    )*/
    @CircuitBreaker(name = "getProductInventoryByCode", fallbackMethod = "getProductInventoryByCodeFallback")
    public Optional<ProductInventoryResponse> getProductInventoryByCode(String code) {

        log.info("CorrelationID: " + MyThreadLocalsHolder.getCorrelationId());

        ResponseEntity<ProductInventoryResponse> itemResponseEntity = restTemplate.getForEntity(INVENTORY_API_PATH + "inventory/{code}", ProductInventoryResponse.class, code);

        log.info("productInventoryResponse: " + itemResponseEntity);

        return Optional.ofNullable(itemResponseEntity.getStatusCode().is2xxSuccessful() ? itemResponseEntity.getBody() : null);
    }

    Optional<ProductInventoryResponse> getProductInventoryByCodeFallback(String code, Throwable throwable) {
        log.error("Unable to get inventory level for product_code: " + code, throwable);
        return Optional.empty();
    }

}
