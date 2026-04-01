package com.ghosttracker.shop.controller;

import com.ghosttracker.shop.dto.product.ProductResponse;
import com.ghosttracker.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Page<ProductResponse> products = productService.getProducts(
                categoryId, keyword, minPrice, maxPrice, sort, page, size);

        return ResponseEntity.ok(Map.of(
                "content", products.getContent(),
                "totalPages", products.getTotalPages(),
                "totalElements", products.getTotalElements(),
                "currentPage", products.getNumber()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping("/new-arrivals")
    public ResponseEntity<List<ProductResponse>> getNewArrivals() {
        return ResponseEntity.ok(productService.getNewArrivals());
    }

    @GetMapping("/best-sellers")
    public ResponseEntity<List<ProductResponse>> getBestSellers() {
        return ResponseEntity.ok(productService.getBestSellers());
    }
}
