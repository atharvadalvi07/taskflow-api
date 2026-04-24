package com.taskflow.taskflow_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.taskflow_api.config.TestSecurityConfig;
import com.taskflow.taskflow_api.dto.AuthResponse;
import com.taskflow.taskflow_api.dto.LoginRequest;
import com.taskflow.taskflow_api.dto.RegisterRequest;
import com.taskflow.taskflow_api.security.JwtUtil;
import com.taskflow.taskflow_api.security.UserDetailsServiceImpl;
import com.taskflow.taskflow_api.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void register_ShouldReturn200WithToken() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new AuthResponse("mock-jwt-token", "test@example.com", "Test User"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    void login_ShouldReturn200WithToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthResponse("mock-jwt-token", "test@example.com", "Test User"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    void register_WithInvalidBody_ShouldReturn400() throws Exception {
        RegisterRequest emptyRequest = new RegisterRequest();
        emptyRequest.setName("");
        emptyRequest.setEmail("");
        emptyRequest.setPassword("");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());
    }
}



//package com.taskflow.taskflow_api.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.taskflow.taskflow_api.dto.AuthResponse;
//import com.taskflow.taskflow_api.dto.LoginRequest;
//import com.taskflow.taskflow_api.dto.RegisterRequest;
//import com.taskflow.taskflow_api.security.JwtAuthFilter;
//import com.taskflow.taskflow_api.security.JwtUtil;
//import com.taskflow.taskflow_api.service.AuthService;
//import com.taskflow.taskflow_api.security.UserDetailsServiceImpl;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.TestPropertySource;
//
//@WebMvcTest(AuthController.class)
//@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
//class AuthControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private AuthService authService;
//
//    @MockitoBean
//    private JwtUtil jwtUtil;
//
//    @MockitoBean
//    private JwtAuthFilter jwtAuthFilter;
//
//    @MockitoBean
//    private UserDetailsServiceImpl userDetailsService;
//
//    @Test
//    void register_ShouldReturn200WithToken() throws Exception {
//        RegisterRequest request = new RegisterRequest();
//        request.setName("Test User");
//        request.setEmail("test@example.com");
//        request.setPassword("password123");
//
//        AuthResponse response = new AuthResponse("mock-jwt-token", "test@example.com", "Test User");
//
//        when(authService.register(any(RegisterRequest.class))).thenReturn(response); // ✅ fixed
//
//        mockMvc.perform(post("/api/auth/register")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
//    }
//
//    @Test
//    void login_ShouldReturn200WithToken() throws Exception {
//        LoginRequest request = new LoginRequest();
//        request.setEmail("test@example.com");
//        request.setPassword("password123");
//
//        AuthResponse response = new AuthResponse("mock-jwt-token", "test@example.com", "Test User");
//
//        when(authService.login(any(LoginRequest.class))).thenReturn(response); // ✅ fixed
//
//        mockMvc.perform(post("/api/auth/login")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
//    }
//
//    @Test
//    void register_WithEmptyBody_ShouldReturn400() throws Exception {
//        mockMvc.perform(post("/api/auth/register")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(status().isBadRequest());
//    }
//}