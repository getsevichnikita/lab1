package com.library.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskInfo {
    private Long id;
    private TaskStatus status;
    private String errorMessage;
}