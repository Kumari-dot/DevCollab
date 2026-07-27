package com.devcollab.backend.controller;

import com.devcollab.backend.model.Task;
import com.devcollab.backend.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate; // 1. Import SimpMessagingTemplate
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskRepository taskRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // 2. Inject messaging template

    @Autowired
    private ObjectMapper objectMapper;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // 1. Update a task's details via REST
    @PutMapping("/api/tasks/{id}")
    public Task updateTask(@PathVariable String id, @Valid @RequestBody Task updatedTask) {
        Task saved = taskRepository.findById(id)
                .map(task -> {
                    task.setTitle(updatedTask.getTitle());
                    task.setDescription(updatedTask.getDescription());
                    task.setStatus(updatedTask.getStatus());
                    task.setPosition(updatedTask.getPosition());
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        // Broadcast updated board state to WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/board", taskRepository.findAll());
        return saved;
    }

    // 2. Delete a task entirely from the board via REST
    @DeleteMapping("/api/tasks/{id}")
    public void deleteTask(@PathVariable String id) {
        taskRepository.deleteById(id);

        // Broadcast updated board state to WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/board", taskRepository.findAll());
    }

    // Handles standard HTTP GET requests
    @GetMapping("/api/tasks")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Handles standard HTTP POST requests to create a task
    @PostMapping("/api/tasks")
    public Task createTaskRest(@Valid @RequestBody Task task) {
        Task saved = taskRepository.save(task);

        // Broadcast updated board state to WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/board", taskRepository.findAll());
        return saved;
    }

    // Handles WebSocket messages sent to "/app/task.create"
    @MessageMapping("/task.create")
    @SendTo("/topic/board")
    public List<Task> createTask(@Valid Task task) {
        taskRepository.save(task);
        return taskRepository.findAll();
    }

    // Handles WebSocket messages sent to "/app/task.move"
    @MessageMapping("/task.move")
    @SendTo("/topic/board")
    public List<Task> moveTask(Task task) {
        taskRepository.findById(task.getId()).ifPresent(existingTask -> {
            existingTask.setStatus(task.getStatus());
            existingTask.setPosition(task.getPosition());
            taskRepository.save(existingTask);
        });
        return taskRepository.findAll();
    }
}