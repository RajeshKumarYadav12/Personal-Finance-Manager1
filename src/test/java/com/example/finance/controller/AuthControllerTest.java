package com.example.finance.controller;
import com.example.finance.dto.auth.AuthResponse;
import com.example.finance.dto.auth.LoginRequest;
import com.example.finance.dto.auth.RegisterRequest;
import com.example.finance.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private AuthService authService;
  @Test
  void register_Success() throws Exception {

    RegisterRequest request = new RegisterRequest("testuser", "password123");
    AuthResponse response = new AuthResponse("User registered successfully", "testuser");
    when(authService.register(any(RegisterRequest.class))).thenReturn(response);

    mockMvc.perform(post("/auth/register")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("User registered successfully"))
        .andExpect(jsonPath("$.username").value("testuser"));
  }
  @Test
  void register_ValidationError() throws Exception {

    RegisterRequest request = new RegisterRequest("", "123"); 

    mockMvc.perform(post("/auth/register")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
  @Test
  void login_Success() throws Exception {

    LoginRequest request = new LoginRequest("testuser", "password123");
    AuthResponse response = new AuthResponse("Login successful", "testuser");
    when(authService.login(any(LoginRequest.class), any(HttpServletRequest.class)))
        .thenReturn(response);

    mockMvc.perform(post("/auth/login")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Login successful"))
        .andExpect(jsonPath("$.username").value("testuser"));
  }
  @Test
  @WithMockUser
  void logout_Success() throws Exception {

    AuthResponse response = new AuthResponse("Logout successful", null);
    when(authService.logout(any(HttpServletRequest.class))).thenReturn(response);

    mockMvc.perform(post("/auth/logout")
        .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Logout successful"));
  }
}
