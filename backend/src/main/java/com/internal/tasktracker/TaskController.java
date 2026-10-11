package com.internal.tasktracker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<?> searchTasks(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {

        // FIX #3: Validate pagination params — prevents IndexOutOfBoundsException
        if (page < 1 || pageSize < 1) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "page and pageSize must be >= 1"));
        }

        // Normalize query input
        String query = q == null ? "" : q.trim();
        String searchTerm = "%" + query.toLowerCase() + "%";

        // FIX #2: Safely parse status — return 400 (not 500) on bad input
        String normalizedStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                normalizedStatus = TaskStatus.valueOf(status.toUpperCase()).name();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Invalid status. Valid values: OPEN, IN_PROGRESS, DONE"));
            }
        }

        // FIX #1: Removed the Thread.sleep() block — it was freezing the server up to 1s per request
        System.out.println("[TaskController] q=\"" + query + "\" status=" + normalizedStatus
                + " page=" + page + " pageSize=" + pageSize);

        List<Task> allResults = taskRepository.searchTasks(searchTerm, normalizedStatus);

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allResults.size());
        List<Task> pageResults = (start < allResults.size())
                ? allResults.subList(start, end)
                : Collections.emptyList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", pageResults);
        response.put("total", allResults.size());
        response.put("page", page);
        response.put("pageSize", pageSize);

        return ResponseEntity.ok(response);
    }
}
