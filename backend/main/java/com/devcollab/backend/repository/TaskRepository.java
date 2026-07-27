package com.devcollab.backend.repository;

import com.devcollab.backend.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
    // We can also define custom search methods here later if needed,
    // like finding all tasks belonging to a specific Kanban status column:
    List<Task> findByStatusOrderByPositionAsc(String status);
}