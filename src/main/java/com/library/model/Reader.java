package com.library.model;
import lombok.*;
import jakarta.persistence.*;
import java.util.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "reader", cascade = CascadeType.ALL)
    private List<Loan> loans = new ArrayList<>();
}