package com.devcollab.backend.controller;

import com.devcollab.backend.model.Task;
import com.devcollab.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class BoardController {

    @Autowired
    private TaskService taskService;

    // When a client sends a message to /app/board.update,
    // this method handles it and broadcasts the result to everyone subscribed to /topic/board
    @MessageMapping("/board.update")
    @SendTo("/topic/board")
    public List<Task> updateBoard() {
        // Fetch all current tasks from the service and broadcast them
        return taskService.getAllTasks();
    }
}