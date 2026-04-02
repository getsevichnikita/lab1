package com.library.service;

import com.library.model.Reader;
import com.library.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReaderService {

    private final ReaderRepository readerRepository;

    public List<Reader> getAll() {
        return readerRepository.findAll();
    }

    public Reader getById(Long id) {
        return readerRepository.findById(id)
                .orElseThrow();
    }

    public Reader save(Reader reader) {
        return readerRepository.save(reader);
    }

    public Reader update(Long id, Reader updated) {
        Reader reader = getById(id);
        reader.setName(updated.getName());
        return readerRepository.save(reader);
    }

    public void delete(Long id) {
        readerRepository.deleteById(id);
    }
}