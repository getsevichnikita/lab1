package com.library.controller;

import com.library.model.dto.LoginRequest;
import com.library.model.dto.ReaderDTO;
import com.library.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ReaderDTO login(@RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/register")
    public ReaderDTO register(@RequestBody LoginRequest req) {
        return authService.register(req);
    }
}