package com.library.model.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookPDF {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] fileData;

    private LocalDateTime uploadedAt;

    private Long ownerId;

    @OneToOne
    @JoinColumn(name = "book_id")
    private Book book;
}