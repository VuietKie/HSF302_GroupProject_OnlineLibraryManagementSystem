package com.he194346.mvc.online_library_management_system.entity;

import com.he194346.mvc.online_library_management_system.enums.BorrowStatus;
import com.he194346.mvc.online_library_management_system.enums.FineStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "borrow_records")
@Getter
@Setter
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long borrowRecordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id")
    private User reader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "librarian_id")
    private User librarian;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private LocalDateTime requestDate;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private LocalDateTime overdueDate;

    private Double fineAmount;

    @Enumerated(EnumType.STRING)
    private FineStatus fineStatus;

    @Enumerated(EnumType.STRING)
    private BorrowStatus status;

    @OneToMany(mappedBy = "borrowRecord", fetch = FetchType.LAZY)
    private Set<BookBorrowRecord> books = new HashSet<>();
}