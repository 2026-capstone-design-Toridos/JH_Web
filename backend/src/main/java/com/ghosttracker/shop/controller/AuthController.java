package com.ghosttracker.shop.controller;

import com.ghosttracker.shop.dto.auth.AuthResponse;
import com.ghosttracker.shop.dto.auth.LoginRequest;
import com.ghosttracker.shop.dto.auth.RegisterRequest;
import com.ghosttracker.shop.entity.User;
import com.ghosttracker.shop.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal UserDetails userDetails) {
        User user = authService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "phone", user.getPhone() != null ? user.getPhone() : "",
                "address", user.getAddress() != null ? user.getAddress() : "",
                "addressDetail", user.getAddressDetail() != null ? user.getAddressDetail() : "",
                "zipCode", user.getZipCode() != null ? user.getZipCode() : "",
                "role", user.getRole().name()
        ));
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {
        User user = authService.updateProfile(
                userDetails.getUsername(),
                body.get("name"),
                body.get("phone"),
                body.get("address"),
                body.get("addressDetail"),
                body.get("zipCode")
        );
        return ResponseEntity.ok(Map.of(
                "message", "프로필이 업데이트되었습니다.",
                "name", user.getName()
        ));
    }
}
