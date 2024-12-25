package com.backend.puntoxpress.service;

import com.backend.puntoxpress.Dto.TaskDTO;
import com.backend.puntoxpress.entity.Task;
import com.backend.puntoxpress.entity.User;
import com.backend.puntoxpress.exception.TaskNotFoundException;
import com.backend.puntoxpress.exception.UserNotFoundException;
import com.backend.puntoxpress.repository.TaskRepository;
import com.backend.puntoxpress.repository.UserRepository;
import com.backend.puntoxpress.utils.TaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    private User user;
    private Task task;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Task Description");
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setUser(user);

        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Updated Task");
        taskDTO.setDescription("Updated Task Description");
        taskDTO.setCompleted(true);
        taskDTO.setUserId(1L);
    }

    @Test
    void testCreateTaskWithNonExistentUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> taskService.createTask(taskDTO));
    }

    @Test
    void testCreateTask() {
        try (MockedStatic<TaskMapper> mockedMapper = Mockito.mockStatic(TaskMapper.class)) {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            mockedMapper.when(() -> TaskMapper.toTaskEntity(taskDTO)).thenReturn(task);
            mockedMapper.when(() -> TaskMapper.toTaskDTO(task)).thenReturn(taskDTO);
            when(taskRepository.save(any(Task.class))).thenReturn(task);

            TaskDTO createdTask = taskService.createTask(taskDTO);

            assertNotNull(createdTask);
            assertEquals("Updated Task", createdTask.getTitle());
            verify(userRepository).findById(anyLong());
            verify(taskRepository).save(any(Task.class));
            mockedMapper.verify(() -> TaskMapper.toTaskEntity(taskDTO));
            mockedMapper.verify(() -> TaskMapper.toTaskDTO(task));
        }
    }

    @Test
    void testUpdateTaskWithUser() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO updatedTask = taskService.updateTask(1L, taskDTO);

        assertNotNull(updatedTask);
        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals("Updated Task Description", updatedTask.getDescription());
        verify(taskRepository).findById(anyLong());
        verify(userRepository).findById(anyLong());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testUpdateTaskWithNonExistentUser() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> taskService.updateTask(1L, taskDTO));
    }

    @Test
    void testDeleteTask() {
        // Mock task existence
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        // Mock user existence
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // Mock task deletion
        doNothing().when(taskRepository).deleteById(anyLong());

        // Call the deleteTask method
        taskService.deleteTask(1L);

        // Verify interactions
        verify(taskRepository).findById(anyLong());
        verify(userRepository).findById(anyLong());
        verify(taskRepository).deleteById(anyLong());
    }


    @Test
    void testDeleteTaskNotFound() {
        when(taskRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(1L));
    }

    @Test
    void testDeleteTaskWithNonExistentUser() {
        // Mock task existence
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        // Mock user non-existence
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(UserNotFoundException.class, () -> taskService.deleteTask(1L));

        // Verify interactions
        verify(taskRepository).findById(anyLong());
        verify(userRepository).findById(anyLong());
    }


}
