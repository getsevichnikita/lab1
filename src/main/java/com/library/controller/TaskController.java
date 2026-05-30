package com.library.controller;

import com.library.service.UnsafeTaskService;
import com.library.task.TaskInfo;
import com.library.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(
        name = "Tasks",
        description = "Async task tracking endpoints"
)
public class TaskController {

    private final TaskService taskService;

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Task status returned successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found"
            )
    })
    @Operation(
            summary = "Get async task status"
    )
    @GetMapping("/{id}")
    public TaskInfo getStatus(
            @PathVariable Long id
    ) {
        return taskService.getTask(id);
    }
}