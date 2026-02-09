package com.example.finance.service;
import com.example.finance.dto.auth.AuthResponse;
import com.example.finance.dto.auth.LoginRequest;
import com.example.finance.dto.auth.RegisterRequest;
import com.example.finance.entity.User;
import com.example.finance.exception.ConflictException;
import com.example.finance.exception.UnauthorizedException;
import com.example.finance.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private HttpServletRequest httpServletRequest;
  @Mock
  private HttpSession httpSession;
  @Mock
  private Authentication authentication;
  @InjectMocks
  private AuthService authService;
  private RegisterRequest registerRequest;
  private LoginRequest loginRequest;
  private User testUser;
  @BeforeEach
  void setUp() {
    registerRequest = new RegisterRequest("testuser", "password123");
    loginRequest = new LoginRequest("testuser", "password123");
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testuser");
    testUser.setPassword("encodedPassword");
  }
  @Test
  void register_Success() {

    when(userRepository.existsByUsername("testuser")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    AuthResponse response = authService.register(registerRequest);

    assertNotNull(response);
    assertEquals("User registered successfully", response.getMessage());
    assertEquals("testuser", response.getUsername());
    verify(userRepository, times(1)).save(any(User.class));
  }
  @Test
  void register_UsernameExists_ThrowsConflictException() {

    when(userRepository.existsByUsername("testuser")).thenReturn(true);

    assertThrows(ConflictException.class, () -> authService.register(registerRequest));
    verify(userRepository, never()).save(any(User.class));
  }
  @Test
  void login_Success() {

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(httpServletRequest.getSession(true)).thenReturn(httpSession);

    AuthResponse response = authService.login(loginRequest, httpServletRequest);

    assertNotNull(response);
    assertEquals("Login successful", response.getMessage());
    assertEquals("testuser", response.getUsername());
    verify(authenticationManager, times(1)).authenticate(any());
  }
  @Test
  void login_InvalidCredentials_ThrowsUnauthorizedException() {

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Invalid credentials"));

    assertThrows(UnauthorizedException.class,
        () -> authService.login(loginRequest, httpServletRequest));
  }
  @Test
  void logout_Success() {

    when(httpServletRequest.getSession(false)).thenReturn(httpSession);

    AuthResponse response = authService.logout(httpServletRequest);

    assertNotNull(response);
    assertEquals("Logout successful", response.getMessage());
    verify(httpSession, times(1)).invalidate();
  }
  @Test
  void logout_NoSession_Success() {

    when(httpServletRequest.getSession(false)).thenReturn(null);

    AuthResponse response = authService.logout(httpServletRequest);

    assertNotNull(response);
    assertEquals("Logout successful", response.getMessage());
  }
}
