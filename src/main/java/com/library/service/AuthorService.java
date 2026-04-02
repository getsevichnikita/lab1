package com.library.service;

import com.library.model.Author;
import com.library.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<Author> getAll() {
        return authorRepository.findAll();
    }

    public Author getById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow();
    }

    public Author save(Author author) {
        return authorRepository.save(author);
    }

    public Author update(Long id, Author updated) {
        Author author = getById(id);
        author.setName(updated.getName());
        return authorRepository.save(author);
    }

    public void delete(Long id) {
        authorRepository.deleteById(id);
    }
}