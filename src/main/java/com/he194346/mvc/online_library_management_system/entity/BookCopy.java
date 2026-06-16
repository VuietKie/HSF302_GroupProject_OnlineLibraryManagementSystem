package com.he194346.mvc.online_library_management_system.entity;

import com.he194346.mvc.online_library_management_system.enums.BookCopyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "book_copies")
@Getter
@Setter
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookCopyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    private String barCode;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private BookCopyStatus status;

    @OneToMany(mappedBy = "bookCopy", fetch = FetchType.LAZY)
    private Set<BookBorrowRecord> borrowRecords = new HashSet<>();
}