package com.ghosttracker.shop.service;

import com.ghosttracker.shop.entity.*;
import com.ghosttracker.shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaService {

    private final ProductQnaRepository qnaRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Page<Map<String, Object>> getProductQnas(Long productId, int page, int size) {
        return qnaRepository.findByProduct_IdOrderByCreatedAtDesc(productId, PageRequest.of(page, size))
                .map(this::toMap);
    }

    @Transactional
    public Map<String, Object> createQna(String email, Long productId, String question, boolean isSecret) {
        User user = getUser(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        ProductQna qna = ProductQna.builder()
                .product(product).user(user)
                .question(question).isSecret(isSecret)
                .build();
        return toMap(qnaRepository.save(qna));
    }

    @Transactional
    public Map<String, Object> answerQna(Long qnaId, String answer) {
        ProductQna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다."));
        qna.setAnswer(answer);
        qna.setIsAnswered(true);
        return toMap(qnaRepository.save(qna));
    }

    @Transactional
    public void deleteQna(String email, Long qnaId) {
        User user = getUser(email);
        ProductQna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다."));
        if (!qna.getUser().getId().equals(user.getId()))
            throw new IllegalStateException("삭제 권한이 없습니다.");
        qnaRepository.delete(qna);
    }

    private Map<String, Object> toMap(ProductQna q) {
        return Map.of(
                "id", q.getId(),
                "question", q.getIsSecret() ? "비밀 문의입니다." : q.getQuestion(),
                "questionRaw", q.getQuestion(),
                "answer", q.getAnswer() != null ? q.getAnswer() : "",
                "isAnswered", q.getIsAnswered(),
                "isSecret", q.getIsSecret(),
                "userName", q.getUser().getName(),
                "userId", q.getUser().getId(),
                "createdAt", q.getCreatedAt().toString()
        );
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
