package com.chanochoca.app.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import com.chanochoca.app.model.dto.InventoryResponse;

@Service
public class InventoryClient {
    private final WebClient.Builder webClientBuilder;

    public InventoryClient(Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public boolean areAllInStock(List<String> skuCodes) {
        InventoryResponse[] response = webClientBuilder.build()
            .get()
            .uri("http://inventory-service/inventories/in-stock", uriBuilder ->
                uriBuilder.queryParam("skuCode", skuCodes).build())
            .retrieve()
            .bodyToMono(InventoryResponse[].class)
            .block();

        return response != null &&
            Arrays.stream(response).allMatch(InventoryResponse::isInStock);
    }
}
