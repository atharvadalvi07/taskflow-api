package com.taskflow.taskflow_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.taskflow_api.config.TestSecurityConfig;
import com.taskflow.taskflow_api.dto.TaskRequest;
import com.taskflow.taskflow_api.dto.TaskResponse;
import com.taskflow.taskflow_api.model.Priority;
import com.taskflow.taskflow_api.model.TaskStatus;
import com.taskflow.taskflow_api.security.JwtUtil;
import com.taskflow.taskflow_api.security.UserDetailsServiceImpl;
import com.taskflow.taskflow_api.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(TestSecurityConfig.class) // ✅ use test security, not real SecurityConfig
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private TaskResponse taskResponse;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        taskResponse = new TaskResponse();
        taskResponse.setId(1L);
        taskResponse.setTitle("Test Task");
        taskResponse.setDescription("Test Description");
        taskResponse.setPriority(Priority.MEDIUM);
        taskResponse.setStatus(TaskStatus.TODO);
        taskResponse.setDueDate(LocalDate.now().plusDays(7));

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");
        taskRequest.setDescription("Test Description");
        taskRequest.setPriority(Priority.MEDIUM);
        taskRequest.setStatus(TaskStatus.TODO);
        taskRequest.setDueDate(LocalDate.now().plusDays(7));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getAllTasks_ShouldReturn200WithList() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(taskResponse));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getTaskById_ShouldReturn200WithTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(taskResponse);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createTask_ShouldReturn201WithTask() throws Exception {
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void updateTask_ShouldReturn200WithUpdatedTask() throws Exception {
        taskResponse.setTitle("Updated Task");
        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void deleteTask_ShouldReturn204() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllTasks_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isForbidden());
    }
}

//package com.taskflow.taskflow_api.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.taskflow.taskflow_api.dto.TaskRequest;
//import com.taskflow.taskflow_api.dto.TaskResponse;
//import com.taskflow.taskflow_api.model.Priority;
//import com.taskflow.taskflow_api.model.TaskStatus;
//import com.taskflow.taskflow_api.security.JwtAuthFilter;
//import com.taskflow.taskflow_api.security.JwtUtil;
//import com.taskflow.taskflow_api.service.TaskService;
//import com.taskflow.taskflow_api.security.UserDetailsServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(TaskController.class)
//class TaskControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private TaskService taskService;
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
//    private TaskResponse taskResponse;
//    private TaskRequest taskRequest;
//
//    @BeforeEach
//    void setUp() {
//        taskResponse = new TaskResponse();
//        taskResponse.setId(1L);
//        taskResponse.setTitle("Test Task");
//        taskResponse.setDescription("Test Description");
//        taskResponse.setPriority(Priority.MEDIUM);
//        taskResponse.setStatus(TaskStatus.TODO);
//        taskResponse.setDueDate(LocalDate.now().plusDays(7));
//
//        taskRequest = new TaskRequest();
//        taskRequest.setTitle("Test Task");
//        taskRequest.setDescription("Test Description");
//        taskRequest.setPriority(Priority.MEDIUM);
//        taskRequest.setStatus(TaskStatus.TODO);
//        taskRequest.setDueDate(LocalDate.now().plusDays(7));
//    }
//
//    @Test
//    @WithMockUser(username = "test@example.com")
//    void getAllTasks_ShouldReturn200WithList() throws Exception {
//        when(taskService.getAllTasks()).thenReturn(List.of(taskResponse));
//
//        mockMvc.perform(get("/api/tasks"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].title").value("Test Task"));
//    }
//
//    @Test
//    @WithMockUser(username = "test@example.com")
//    void getTaskById_ShouldReturn200WithTask() throws Exception {
//        when(taskService.getTaskById(1L)).thenReturn(taskResponse);
//
//        mockMvc.perform(get("/api/tasks/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.title").value("Test Task"));
//    }
//
//    @Test
//    @WithMockUser(username = "test@example.com")
//    void createTask_ShouldReturn201WithTask() throws Exception {
//        when(taskService.createTask(any(TaskRequest.class))).thenReturn(taskResponse);
//
//        mockMvc.perform(post("/api/tasks")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(taskRequest)))
//                .andExpect(status().isCreated()) // ✅ 201
//                .andExpect(jsonPath("$.title").value("Test Task"));
//    }
//
//    @Test
//    @WithMockUser(username = "test@example.com")
//    void updateTask_ShouldReturn200WithUpdatedTask() throws Exception {
//        taskResponse.setTitle("Updated Task");
//        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(taskResponse);
//
//        mockMvc.perform(put("/api/tasks/1")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(taskRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value("Updated Task"));
//    }
//
//    @Test
//    @WithMockUser(username = "test@example.com")
//    void deleteTask_ShouldReturn204() throws Exception {
//        doNothing().when(taskService).deleteTask(1L);
//
//        mockMvc.perform(delete("/api/tasks/1")
//                        .with(csrf()))
//                .andExpect(status().isNoContent()); // ✅ 204
//    }
//
//    @Test
//    void getAllTasks_WithoutAuth_ShouldReturn401() throws Exception {
//        mockMvc.perform(get("/api/tasks"))
//                .andExpect(status().isUnauthorized());
//    }
//}