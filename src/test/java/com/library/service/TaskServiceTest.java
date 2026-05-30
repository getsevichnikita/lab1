package com.library.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TaskServiceTest {

    @Test
    void shouldWorkCorrectlyWithAtomicAndConcurrentMap() throws Exception {
        TaskService service = new TaskService();
        int tasksCount = 10000;

        try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
            for (int i = 0; i < tasksCount; i++) {
                executor.submit(service::createTask);
            }
            executor.shutdown();
            boolean finished = executor.awaitTermination(10, TimeUnit.SECONDS);
            assertTrue(finished);
        }

        int actualCount = service.getTasksCount();

        log.info("============================================");
        log.info("=== THREAD-SAFE ATOMIC COUNTER TEST RESULTS ===");
        log.info("============================================");
        log.info("Expected tasks: {}", tasksCount);
        log.info("Actual tasks:   {}", actualCount);
        log.info("Lost tasks:     {}", tasksCount - actualCount);
        log.info("Result:         {}", actualCount == tasksCount ? "PASSED - No race condition" : "FAILED");
        log.info("============================================\n");

        assertEquals(tasksCount, actualCount, "Atomic counter should have no lost tasks");
    }
}