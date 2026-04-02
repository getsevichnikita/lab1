package com.library.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "authors")
    private List<Book> books = new ArrayList<>();
}