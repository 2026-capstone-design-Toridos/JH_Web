package com.ghosttracker.shop.controller;

import com.ghosttracker.shop.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/qnas")
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getProductQnas(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Map<String, Object>> result = qnaService.getProductQnas(productId, page, size);
        return ResponseEntity.ok(Map.of(
                "content", result.getContent(),
                "totalPages", result.getTotalPages(),
                "totalElements", result.getTotalElements()
        ));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createQna(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> body) {
        Long productId = Long.parseLong(body.get("productId").toString());
        String question = (String) body.get("question");
        boolean isSecret = body.get("isSecret") != null && (Boolean) body.get("isSecret");
        return ResponseEntity.ok(qnaService.createQna(userDetails.getUsername(), productId, question, isSecret));
    }

    @DeleteMapping("/{qnaId}")
    public ResponseEntity<Map<String, String>> deleteQna(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long qnaId) {
        qnaService.deleteQna(userDetails.getUsername(), qnaId);
        return ResponseEntity.ok(Map.of("message", "문의가 삭제되었습니다."));
    }

    @PatchMapping("/{qnaId}/answer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> answerQna(
            @PathVariable Long qnaId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(qnaService.answerQna(qnaId, body.get("answer")));
    }
}
