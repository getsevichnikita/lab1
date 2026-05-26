package com.library.service;

import com.library.exception.ResourceNotFoundException;
import com.library.task.TaskInfo;
import com.library.task.TaskStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {

    private final Map<Long, TaskInfo> tasks =
            new ConcurrentHashMap<>();

    private final AtomicLong counter =
            new AtomicLong(0);

    public Long createTask() {

        Long taskId = counter.incrementAndGet();

        tasks.put(
                taskId,
                new TaskInfo(
                        taskId,
                        TaskStatus.IN_PROGRESS,
                        null
                )
        );

        return taskId;
    }

    public void markDone(Long taskId) {

        tasks.put(
                taskId,
                new TaskInfo(
                        taskId,
                        TaskStatus.DONE,
                        null
                )
        );
    }

    public void markFailed(
            Long taskId,
            String error
    ) {

        tasks.put(
                taskId,
                new TaskInfo(
                        taskId,
                        TaskStatus.FAILED,
                        error
                )
        );
    }

    public TaskInfo getTask(Long taskId) {
        TaskInfo task = tasks.get(taskId);
        if (task == null) {
            throw new ResourceNotFoundException(
                    "Task not found with id = " + taskId
            );
        }
        return task;
    }
}