package com.library.model;
import lombok.*;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "categories")
    private List<Book> books = new ArrayList<>();
}