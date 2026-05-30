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

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(
        name = "Tasks",
        description = "Async task tracking endpoints"
)
public class TaskController {

    private final TaskService taskService;
    private final UnsafeTaskService unsafeTaskService;

    @GetMapping("/race-test")
    public Map<String, Object> raceTest() throws InterruptedException {
        try (ExecutorService executor = Executors.newFixedThreadPool(50)) {
            CountDownLatch latch = new CountDownLatch(1000);

            for (int i = 0; i < 1000; i++) {
                executor.submit(() -> {
                    unsafeTaskService.createTask();
                    latch.countDown();
                });
            }

            latch.await();

            return Map.of(
                    "expected", 1000,
                    "actual", unsafeTaskService.getTasksCount(),
                    "lost", 1000 - unsafeTaskService.getTasksCount()
            );
        }
    }

    @GetMapping("/atomic-test")
    public Map<String, Object> atomicTest() throws InterruptedException {
        try (ExecutorService executor = Executors.newFixedThreadPool(50)) {
            int initial = taskService.getTasksCount();
            CountDownLatch latch = new CountDownLatch(1000);

            for (int i = 0; i < 1000; i++) {
                executor.submit(() -> {
                    taskService.createTask();
                    latch.countDown();
                });
            }

            latch.await();

            return Map.of(
                    "expected", 1000,
                    "actual", taskService.getTasksCount() - initial,
                    "lost", 0
            );
        }
    }

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