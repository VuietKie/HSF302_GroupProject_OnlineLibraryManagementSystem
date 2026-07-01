package com.he194346.mvc.online_library_management_system.entity;

import com.he194346.mvc.online_library_management_system.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reservations")
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id")
    private User reader;

    private LocalDateTime reservedAt;

    private LocalDateTime expiredAt;

    // Thời điểm reader gửi reservation cho Librarian phê duyệt.
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @OneToMany(mappedBy = "reservation", fetch = FetchType.LAZY)
    private Set<BookReservation> books = new HashSet<>();

    @OneToMany(mappedBy = "reservation", fetch = FetchType.LAZY)
    private Set<BorrowRecord> borrowRecords = new HashSet<>();
}
