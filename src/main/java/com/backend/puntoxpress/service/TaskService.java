package com.backend.puntoxpress.service;

import com.backend.puntoxpress.Dto.TaskDTO;
import com.backend.puntoxpress.entity.Task;
import com.backend.puntoxpress.entity.User;
import com.backend.puntoxpress.exception.TaskNotFoundException;
import com.backend.puntoxpress.repository.TaskRepository;
import com.backend.puntoxpress.repository.UserRepository;
import com.backend.puntoxpress.utils.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService implements Serializable {
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    UserRepository userRepository;

    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskMapper::toTaskDTO) // Use TaskMapper for conversion
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId).stream()
                .map(TaskMapper::toTaskDTO) // Use TaskMapper for conversion
                .collect(Collectors.toList());
    }

    public TaskDTO createTask(TaskDTO taskDTO) {
        // Fetch the User entity by userId from the TaskDTO
        User user = userRepository.findById(taskDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Convert TaskDTO to Task entity
        Task task = TaskMapper.toTaskEntity(taskDTO);
        // Set the User entity to the task
        task.setUser(user);
        // Save the Task and return the saved TaskDTO
        Task savedTask = taskRepository.save(task);
        return TaskMapper.toTaskDTO(savedTask);
    }

    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        // Fetch the existing task by ID
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // If userId is provided in the DTO, fetch the User entity
        if (taskDTO.getUserId() != null) {
            // Fetch the User entity by userId
            User user = userRepository.findById(taskDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Set the User entity to the existing task
            existingTask.setUser(user);
        }

        // Update the task fields
        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setCompleted(taskDTO.getCompleted());
        existingTask.setLastUpdate();
        // Save the updated task
        Task updatedTask = taskRepository.save(existingTask);
        // Convert the updated task to TaskDTO and return
        return TaskMapper.toTaskDTO(updatedTask); // Use TaskMapper for conversion
    }

    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException("Task not found with ID: " + taskId);
        }
        taskRepository.deleteById(taskId);
    }
}
