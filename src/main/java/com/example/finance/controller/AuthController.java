package com.example.finance.controller;
import com.example.finance.dto.auth.AuthResponse;
import com.example.finance.dto.auth.LoginRequest;
import com.example.finance.dto.auth.RegisterRequest;
import com.example.finance.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  public AuthController(AuthService authService) {
    this.authService = authService;
  }
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    AuthResponse response = authService.register(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(
      @Valid @RequestBody LoginRequest request,
      HttpServletRequest httpRequest) {
    AuthResponse response = authService.login(request, httpRequest);
    return ResponseEntity.ok(response);
  }
  @PostMapping("/logout")
  public ResponseEntity<AuthResponse> logout(HttpServletRequest httpRequest) {
    AuthResponse response = authService.logout(httpRequest);
    return ResponseEntity.ok(response);
  }
}
