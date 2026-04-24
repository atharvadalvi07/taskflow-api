// src/test/java/com/taskflow/taskflow_api/service/AuthServiceTest.java
package com.taskflow.taskflow_api.service;

import com.taskflow.taskflow_api.dto.AuthResponse;
import com.taskflow.taskflow_api.dto.LoginRequest;
import com.taskflow.taskflow_api.dto.RegisterRequest;
import com.taskflow.taskflow_api.model.Role;
import com.taskflow.taskflow_api.model.User;
import com.taskflow.taskflow_api.repository.UserRepository;
import com.taskflow.taskflow_api.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setRole(Role.USER);
    }

    @Test
    void register_WithNewEmail_ShouldReturnToken() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtUtil.generateToken(any())).thenReturn("mock-jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("mock-jwt-token");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("Test User");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_WithExistingEmail_ShouldThrowException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already registered");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_ShouldEncodePassword() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtUtil.generateToken(any())).thenReturn("mock-jwt-token");

        authService.register(registerRequest);

        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken(any())).thenReturn("mock-jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("mock-jwt-token");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("Test User");
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);

        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void login_WithNonExistentUser_ShouldThrowException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}

//package com.taskflow.taskflow_api.service;
//
//import com.taskflow.taskflow_api.dto.RegisterRequest;
//import com.taskflow.taskflow_api.dto.LoginRequest;
//import com.taskflow.taskflow_api.dto.AuthResponse;
//import com.taskflow.taskflow_api.model.User;
//import com.taskflow.taskflow_api.repository.UserRepository;
//import com.taskflow.taskflow_api.security.JwtUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private JwtUtil jwtUtil;
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @InjectMocks
//    private AuthService authService;
//
//    private AuthRequest authRequest;
//    private User mockUser;
//
//    @BeforeEach
//    void setUp() {
//        authRequest = new AuthRequest();
//        authRequest.setEmail("test@example.com");
//        authRequest.setPassword("password123");
//        authRequest.setName("Test User"); // used in register
//
//        mockUser = new User();
//        mockUser.setId(1L);
//        mockUser.setEmail("test@example.com");
//        mockUser.setName("Test User");
//        mockUser.setPassword("encodedPassword");
//    }
//
//    @Test
//    void register_WithNewEmail_ShouldReturnToken() {
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
//        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
//        when(userRepository.save(any(User.class))).thenReturn(mockUser);
//        when(jwtUtil.generateToken(any())).thenReturn("mock-jwt-token");
//
//        AuthResponse response = authService.register(authRequest);
//
//        assertThat(response.getToken()).isEqualTo("mock-jwt-token");
//        verify(userRepository, times(1)).save(any(User.class));
//    }
//
//    @Test
//    void register_WithExistingEmail_ShouldThrowException() {
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
//
//        assertThatThrownBy(() -> authService.register(authRequest))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Email already registered");
//
//        verify(userRepository, never()).save(any());
//    }
//
//    @Test
//    void login_WithValidCredentials_ShouldReturnToken() {
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(null); // authenticate() returns Authentication; null = no exception = success
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
//        when(jwtUtil.generateToken(any())).thenReturn("mock-jwt-token");
//
//        AuthResponse response = authService.login(authRequest);
//
//        assertThat(response.getToken()).isEqualTo("mock-jwt-token");
//    }
//
//    @Test
//    void login_WithInvalidCredentials_ShouldThrowException() {
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenThrow(new BadCredentialsException("Bad credentials"));
//
//        assertThatThrownBy(() -> authService.login(authRequest))
//                .isInstanceOf(BadCredentialsException.class);
//    }
//}