package com.taskflow.taskflow_api.service;

import com.taskflow.taskflow_api.dto.AuthResponse;
import com.taskflow.taskflow_api.dto.LoginRequest;      // ✅ changed
import com.taskflow.taskflow_api.dto.RegisterRequest;   // ✅ changed
import com.taskflow.taskflow_api.model.Role;
import com.taskflow.taskflow_api.model.User;
import com.taskflow.taskflow_api.repository.UserRepository;
import com.taskflow.taskflow_api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {  // ✅ RegisterRequest
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getName());
    }

    public AuthResponse login(LoginRequest request) {  // ✅ LoginRequest
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getName());
    }
}
//package com.taskflow.taskflow_api.service;
//
//import com.taskflow.taskflow_api.dto.AuthRequest;
//import com.taskflow.taskflow_api.dto.AuthResponse;
//import com.taskflow.taskflow_api.model.Role;
//import com.taskflow.taskflow_api.model.User;
//import com.taskflow.taskflow_api.repository.UserRepository;
//import com.taskflow.taskflow_api.security.JwtUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtUtil jwtUtil;
//    private final AuthenticationManager authenticationManager;
//
//    public AuthResponse register( AuthRequest request) {
//        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
//            throw new RuntimeException("Email already registered");
//        }
//
//        User user = User.builder()
//                .name(request.getName())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .role(Role.USER)
//                .build();
//
//        userRepository.save(user);
//        String token = jwtUtil.generateToken(user);
//        return new AuthResponse(token, user.getEmail(), user.getName());
//    }
//
//    public AuthResponse login( AuthRequest request) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//        );
//
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        String token = jwtUtil.generateToken(user);
//        return new AuthResponse(token, user.getEmail(), user.getName());
//    }
//}