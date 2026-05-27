package com.library.service;

import com.library.exception.ResourceNotFoundException;
import com.library.mapper.ReaderMapper;
import com.library.model.dto.LoginRequest;
import com.library.model.dto.ReaderDTO;
import com.library.model.entity.Reader;
import com.library.model.entity.User;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public ReaderDTO login(LoginRequest req) {

        User user = userRepository.findByUsername(req.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getPassword().equals(req.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        return ReaderMapper.toDto(user.getReader());
    }

    public ReaderDTO register(LoginRequest req) {

        if (userRepository.findByUsername(req.getName()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        Reader reader = new Reader();
        reader.setName(req.getName());

        User user = new User();
        user.setUsername(req.getName());
        user.setPassword(req.getPassword());
        user.setReader(reader);

        userRepository.save(user);

        return ReaderMapper.toDto(reader);
    }
}