package com.library.service;

import com.library.exception.ResourceNotFoundException;
import com.library.mapper.ReaderMapper;
import com.library.model.dto.LoginRequest;
import com.library.model.dto.ReaderDTO;
import com.library.model.entity.Reader;
import com.library.model.entity.User;
import com.library.repository.ReaderRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ReaderRepository readerRepository;
    private final UserRepository userRepository;

    public ReaderDTO login(LoginRequest req) {

        User user = userRepository.findByUsername(req.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getPassword().equals(req.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        return ReaderMapper.toDto(user.getReader());
    }

    @Transactional
    public ReaderDTO register(LoginRequest req) {

        if (req.getName() == null || req.getName().isBlank()) {
            throw new RuntimeException("Name is empty");
        }

        if (userRepository.findByUsername(req.getName()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        Reader reader = new Reader();
        reader.setName(req.getName());
        reader = readerRepository.save(reader);

        User user = new User();
        user.setUsername(req.getName());
        user.setPassword(req.getPassword());
        user.setReader(reader);

        userRepository.save(user);

        return ReaderMapper.toDto(reader);
    }
}