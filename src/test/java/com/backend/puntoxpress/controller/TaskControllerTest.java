package com.backend.puntoxpress.controller;

import com.backend.puntoxpress.entity.Task;
import com.backend.puntoxpress.entity.User;
import com.backend.puntoxpress.repository.TaskRepository;
import com.backend.puntoxpress.repository.UserRepository;
import com.backend.puntoxpress.service.JwtTokenProviderService;
import com.backend.puntoxpress.Dto.TaskDTO;
import com.backend.puntoxpress.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.security.enabled=false"
})
@WithMockUser(username = "testuser", roles = {"USER"})
@TestPropertySource(properties = {
        "SECRET_KEY=test-secret-key",
        "EXPIRATION_TIME=3600000",
        "REFRESH_TOKEN_EXPIRATION_MILLIS=7200000"
})
@MockitoSettings(strictness = Strictness.LENIENT)
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private TaskController taskController;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    Task task;
    @Mock
    private JwtTokenProviderService jwtTokenProviderService;
    @Mock
    private TaskService taskService;
    @Autowired
    private ObjectMapper objectMapper;

    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Test Task Description");
        taskDTO.setCompleted(false);
        taskDTO.setUserId(1L);

        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void testGetAllTasks() throws Exception {
        List<TaskDTO> tasks = Collections.singletonList(taskDTO);
        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"))
                .andExpect(jsonPath("$[0].description").value("Test Task Description"));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void testGetUserTasks() throws Exception {
        List<TaskDTO> tasks = Collections.singletonList(taskDTO);
        when(taskService.getTasksByUserId(1L)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"))
                .andExpect(jsonPath("$[0].description").value("Test Task Description"));

        verify(taskService, times(1)).getTasksByUserId(1L);
    }

    @Test
    void testCreateTask() throws Exception {
        when(taskService.createTask(any(TaskDTO.class))).thenReturn(taskDTO);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Task Description"));
    }

    @Test
    void testUpdateTask() throws Exception {
        TaskDTO updatedTaskDTO = new TaskDTO();
        updatedTaskDTO.setId(1L);
        updatedTaskDTO.setTitle("Updated Task");
        updatedTaskDTO.setDescription("Updated Task Description");
        updatedTaskDTO.setCompleted(true);
        updatedTaskDTO.setUserId(1L);

        when(taskService.updateTask(eq(1L), any(TaskDTO.class))).thenReturn(updatedTaskDTO);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTaskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Task Description"));
    }

    @Test
    void testDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }

    @Test
    void testDeleteTaskSuccess() throws Exception {
        // Arrange: Mock behavior for a successful task deletion
        Long taskId = 1L;
        Task taskForDelete = new Task();
        taskForDelete.setId(taskId);
        taskForDelete.setTitle("Test Task");
        taskForDelete.setDescription("Test Task Description");
        taskForDelete.setCompleted(false);

        Long userId = 1L;
        User userForDelete = new User();
        userForDelete.setId(userId);

        // Mock the repository's findById and deleteById methods
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskForDelete));
        when(userRepository.findById(userId)).thenReturn(Optional.of(userForDelete));
        doNothing().when(taskRepository).deleteById(taskId); // Mock void method

        // Act and Assert: Perform DELETE request and expect 204 No Content
        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNoContent());

        // Verify interactions
        //verify(taskRepository, times(1)).findById(taskId); // Ensure findById was called
        //verify(taskRepository, times(1)).deleteById(taskId); // Ensure deleteById was called
    }

//    @Test
//    void testDeleteTaskNotFound() throws Exception {
//        // Arrange: Mock behavior for when the task is not found
//        Long taskId = 1L;
//        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
//
//        // Act and Assert: Perform DELETE request and expect 404 status with exception message
//        mockMvc.perform(delete("/api/tasks/{id}", taskId))
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(content().string("Task not found with ID: " + taskId));
//
//        // Verify that the repository's findById was called
//        verify(taskRepository, times(1)).findById(taskId);
//        verify(taskRepository, never()).deleteById(taskId); // Ensure delete is not called
//    }
}
