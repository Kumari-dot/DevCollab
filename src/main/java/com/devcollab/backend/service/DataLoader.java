package com.devcollab.backend.config;

import com.devcollab.backend.model.Task;
import com.devcollab.backend.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(TaskRepository repository) {
        return args -> {
            // Only inject data if the database is currently empty
            if (repository.count() == 0) {
                System.out.println("====== MongoDB is empty! Injecting sample tasks for Gemini to analyze... ======");

                // Fixed: Replaced LocalDateTime.now() with Integer positions (1, 2, 3)
                Task task1 = new Task("Design Frontend Dashboard", "Create wireframes and implement Kanban board UI components using Tailwind CSS.", "IN_PROGRESS", 1);
                Task task2 = new Task("Fix Authentication Token Bug", "Resolve intermittent 401 Unauthorized errors caused by expired JWT handling logic.", "TODO", 2);
                Task task3 = new Task("Optimize MongoDB Indexes", "Add indexes to frequently queried task fields to drastically lower fetch latency.", "DONE", 3);

                repository.saveAll(List.of(task1, task2, task3));
                System.out.println("====== Sample data successfully injected! ======");
            } else {
                System.out.println("====== MongoDB already has tasks. Skipping data injection. ======");
            }
        };
    }
}