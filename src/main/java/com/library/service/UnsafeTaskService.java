package com.library.service;

import java.util.HashMap;
import java.util.Map;

public class UnsafeTaskService {

    private final Map<Long, String> tasks =
            new HashMap<>();

    private Long counter = 0L;

    public Long createTask() {

        counter++;

        tasks.put(counter, "TASK");

        return counter;
    }

    public int getTasksCount() {
        return tasks.size();
    }
}