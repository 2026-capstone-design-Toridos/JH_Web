package com.ghosttracker.shop.controller;

import com.ghosttracker.shop.entity.Category;
import com.ghosttracker.shop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getCategories() {
        List<Map<String, Object>> result = categoryRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(c -> Map.<String, Object>of(
                        "id", c.getId(),
                        "name", c.getName(),
                        "slug", c.getSlug() != null ? c.getSlug() : "",
                        "description", c.getDescription() != null ? c.getDescription() : ""
                ))
                .toList();
        return ResponseEntity.ok(result);
    }
}
