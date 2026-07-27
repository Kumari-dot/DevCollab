package com.devcollab.backend.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    @NotBlank(message = "Task title cannot be blank")
    private String title;

    private String description;
    private String status; // e.g., "TODO", "IN_PROGRESS", "DONE"
    private Integer position; // For ordering cards within a column
    private LocalDateTime createdAt;

    // Default Constructor (Required by Spring/MongoDB)
    public Task() {
        this.createdAt = LocalDateTime.now();
    }

    // Parameterized Constructor (For manually creating new tasks)
    public Task(String title, String description, String status, Integer position) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.position = position;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}