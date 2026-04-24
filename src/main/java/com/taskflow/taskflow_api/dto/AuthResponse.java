// src/main/java/com/taskflow/taskflow_api/dto/AuthResponse.java
package com.taskflow.taskflow_api.dto;

public class AuthResponse {

    private String token;
    private String email;
    private String name;

    public AuthResponse() {}

    public AuthResponse(String token, String email, String name) {
        this.token = token;
        this.email = email;
        this.name = name;
    }

    // ✅ explicit getters so Jackson can serialize
    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getName() { return name; }
}

//package com.taskflow.taskflow_api.dto;
//
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import lombok.Data;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class AuthResponse {
//    private String token;
//    private String email;
//    private String name;
//    public AuthResponse(String token) {
//        this.token = token;
//    }
//}