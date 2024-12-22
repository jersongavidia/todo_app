package com.backend.puntoxpress.utils;

import com.backend.puntoxpress.Dto.TaskDTO;
import com.backend.puntoxpress.entity.Task;

public class TaskMapper {
    public static TaskDTO toTaskDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCompleted(task.getCompleted());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setUserId(task.getUser().getId());
        return dto;
    }

    public static Task toTaskEntity(TaskDTO taskDTO) {
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setCompleted(taskDTO.getCompleted());
        // Additional fields can be mapped here if needed
        return task;
    }
}
