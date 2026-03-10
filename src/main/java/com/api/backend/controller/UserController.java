package com.api.backend.controller;

import com.api.backend.model.User;
import com.api.backend.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;


    @Tag(name = "User Management")
    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Success")
    @GetMapping("/public/health")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Public endpoint - no authentication required");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/users/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return ResponseEntity.ok(userRepository.findByUsername(username)
            .orElseGet(() -> {
                User user = new User();
                user.setUsername(username);
                user.setEmail(jwt.getClaimAsString("email"));
                user.setFirstName(jwt.getClaimAsString("given_name"));
                user.setLastName(jwt.getClaimAsString("family_name"));
                return userRepository.save(user);
            }));
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userRepository.save(user));
    }
}
