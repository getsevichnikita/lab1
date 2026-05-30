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
        UnsafeTaskService service = new UnsafeTaskService();
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
        int lost = tasksCount - actualCount;

        log.info("============================================");
        log.info("=== UNSAFE RACE CONDITION TEST RESULTS ===");
        log.info("============================================");
        log.info("Expected tasks: {}", tasksCount);
        log.info("Actual tasks:   {}", actualCount);
        log.info("Lost tasks:     {} ({}%)", lost, String.format("%.2f", lost * 100.0 / tasksCount));
        log.info("Result:         {}", lost > 0 ? "PASSED - Race condition detected" : "UNEXPECTED - No race condition");
        log.info("============================================\n");

        assertNotEquals(tasksCount, actualCount, "Should demonstrate race condition with lost tasks");
    }
}