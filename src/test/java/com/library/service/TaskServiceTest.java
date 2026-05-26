package com.library.service;

import com.library.exception.ResourceNotFoundException;
import com.library.task.TaskInfo;
import com.library.task.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private final TaskService taskService =
            new TaskService();

    @Test
    void createTask_shouldCreateTask() {

        Long taskId = taskService.createTask();

        TaskInfo task =
                taskService.getTask(taskId);

        assertNotNull(taskId);

        assertEquals(
                TaskStatus.IN_PROGRESS,
                task.getStatus()
        );

        assertNull(task.getErrorMessage());
    }

    @Test
    void markDone_shouldSetDoneStatus() {

        Long taskId = taskService.createTask();

        taskService.markDone(taskId);

        TaskInfo task =
                taskService.getTask(taskId);

        assertEquals(
                TaskStatus.DONE,
                task.getStatus()
        );

        assertNull(task.getErrorMessage());
    }

    @Test
    void markFailed_shouldSetFailedStatus() {

        Long taskId = taskService.createTask();

        taskService.markFailed(
                taskId,
                "Some error"
        );

        TaskInfo task =
                taskService.getTask(taskId);

        assertEquals(
                TaskStatus.FAILED,
                task.getStatus()
        );

        assertEquals(
                "Some error",
                task.getErrorMessage()
        );
    }

    @Test
    void getTask_shouldThrowResourceNotFoundException() {

        ResourceNotFoundException ex =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> taskService.getTask(999L)
                );

        assertEquals(
                "Task not found with id = 999",
                ex.getMessage()
        );
    }

    @Test
    void createTask_shouldGenerateDifferentIds() {

        Long firstId =
                taskService.createTask();

        Long secondId =
                taskService.createTask();

        assertNotEquals(
                firstId,
                secondId
        );
    }
}