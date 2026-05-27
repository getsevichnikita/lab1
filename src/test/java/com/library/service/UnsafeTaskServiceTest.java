package com.library.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class UnsafeTaskServiceTest {

    @Test
    void shouldShowRaceCondition() throws Exception {

        UnsafeTaskService service =
                new UnsafeTaskService();

        try (
                ExecutorService executor =
                        Executors.newFixedThreadPool(100)
        ) {
            int tasksCount = 10000;

            for (int i = 0; i < tasksCount; i++) {

                executor.submit(service::createTask);
            }

            executor.shutdown();

            boolean finished = executor.awaitTermination(
                    10,
                    TimeUnit.SECONDS
            );
            assertTrue(finished);

            assertNotEquals(
                    tasksCount,
                    service.getTasksCount()
            );
        }
    }
}