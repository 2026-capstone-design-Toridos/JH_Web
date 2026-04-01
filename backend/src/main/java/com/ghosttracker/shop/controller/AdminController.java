package com.ghosttracker.shop.controller;

import com.ghosttracker.shop.dto.order.OrderResponse;
import com.ghosttracker.shop.dto.product.ProductRequest;
import com.ghosttracker.shop.dto.product.ProductResponse;
import com.ghosttracker.shop.entity.Category;
import com.ghosttracker.shop.repository.CategoryRepository;
import com.ghosttracker.shop.service.AdminService;
import com.ghosttracker.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // --- Products ---
    @PostMapping(value = "/products", consumes = "multipart/form-data")
    public ResponseEntity<ProductResponse> createProduct(
            @RequestPart("data") ProductRequest req,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        return ResponseEntity.ok(productService.createProduct(req, mainImage, images));
    }

    @PutMapping(value = "/products/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestPart("data") ProductRequest req,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        return ResponseEntity.ok(productService.updateProduct(id, req, mainImage, images));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "상품이 삭제되었습니다."));
    }

    // --- Orders ---
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<OrderResponse> orders = adminService.getAllOrders(page, size);
        return ResponseEntity.ok(Map.of(
                "content", orders.getContent(),
                "totalPages", orders.getTotalPages(),
                "totalElements", orders.getTotalElements()
        ));
    }

    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminService.updateOrderStatus(orderId, body.get("status")));
    }

    // --- Categories ---
    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category req) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
        category.setName(req.getName());
        category.setSlug(req.getSlug());
        category.setDescription(req.getDescription());
        category.setDisplayOrder(req.getDisplayOrder());
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "카테고리가 삭제되었습니다."));
    }
}
