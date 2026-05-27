package com.library.model.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String name;
    private String password;
}