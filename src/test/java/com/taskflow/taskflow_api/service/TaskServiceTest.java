// src/test/java/com/taskflow/taskflow_api/service/TaskServiceTest.java
package com.taskflow.taskflow_api.service;

import com.taskflow.taskflow_api.dto.TaskRequest;
import com.taskflow.taskflow_api.dto.TaskResponse;
import com.taskflow.taskflow_api.exception.ResourceNotFoundException;
import com.taskflow.taskflow_api.model.Priority;
import com.taskflow.taskflow_api.model.TaskStatus;
import com.taskflow.taskflow_api.model.Task;
import com.taskflow.taskflow_api.model.User;
import com.taskflow.taskflow_api.repository.TaskRepository;
import com.taskflow.taskflow_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User mockUser;
    private Task mockTask;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setName("Test User");

        mockTask = new Task();
        mockTask.setId(1L);
        mockTask.setTitle("Test Task");
        mockTask.setDescription("Test Description");
        mockTask.setPriority(Priority.MEDIUM);
        mockTask.setStatus(TaskStatus.TODO);
        mockTask.setDueDate(LocalDate.now().plusDays(7));
        mockTask.setOwner(mockUser); // ✅ owner must match mockUser for ownership check

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");
        taskRequest.setDescription("Test Description");
        taskRequest.setPriority(Priority.MEDIUM);
        taskRequest.setStatus(TaskStatus.TODO);
        taskRequest.setDueDate(LocalDate.now().plusDays(7));

        // ✅ principal must be the actual User object since service casts directly
        var auth = new UsernamePasswordAuthenticationToken(
                mockUser, null, Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getAllTasks_ShouldReturnTaskListForCurrentUser() {
        when(taskRepository.findByOwner(mockUser)).thenReturn(List.of(mockTask));

        List<TaskResponse> result = taskService.getAllTasks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Task");
        verify(taskRepository, times(1)).findByOwner(mockUser);
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        // ✅ stub findById (not findByIdAndOwner — that method doesn't exist in your repo)
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        TaskResponse result = taskService.getTaskById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Task");
    }

    @Test
    void getTaskById_WhenTaskNotFound_ShouldThrowException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createTask_ShouldSaveAndReturnTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        TaskResponse result = taskService.createTask(taskRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Task");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_WhenTaskExists_ShouldUpdateAndReturn() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        taskRequest.setTitle("Updated Title");
        TaskResponse result = taskService.updateTask(1L, taskRequest);

        assertThat(result).isNotNull();
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldCallDelete() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        doNothing().when(taskRepository).delete(mockTask);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(mockTask);
    }

    @Test
    void deleteTask_WhenTaskNotFound_ShouldThrowException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}