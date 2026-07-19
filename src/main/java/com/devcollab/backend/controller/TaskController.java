package com.devcollab.backend.controller;

import com.devcollab.backend.model.Task;
import com.devcollab.backend.repository.TaskRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskRepository taskRepository;

    // Dependency Injection: Spring automatically plugs in our repository here
    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    // Handles standard HTTP GET requests to "http://localhost:8080/api/tasks"
    @GetMapping("/api/tasks")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    // Handles messages sent to "/app/task.create"
    @MessageMapping("/task.create")
    @SendTo("/topic/board")

    public List<Task> createTask(Task task) {
        // Save the incoming task to MongoDB
        taskRepository.save(task);

        // Return the fresh list of all tasks to update everyone's board
        return taskRepository.findAll();
    }

    // Handles messages sent to "/app/task.move"
    @MessageMapping("/task.move")
    @SendTo("/topic/board")
    public List<Task> moveTask(Task task) {
        // Fetch the existing task, update its status/position, and save it
        taskRepository.findById(task.getId()).ifPresent(existingTask -> {
            existingTask.setStatus(task.getStatus());
            existingTask.setPosition(task.getPosition());
            taskRepository.save(existingTask);
        });

        // Broadcast the updated state of the board to all connected users
        return taskRepository.findAll();
    }
}