package com.he194346.mvc.online_library_management_system.entity;

import com.he194346.mvc.online_library_management_system.enums.UserRole;
import com.he194346.mvc.online_library_management_system.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String fullName;
    private String email;
    private String phone;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private LocalDateTime createdAt;

    // Reader

    @OneToMany(mappedBy = "reader", fetch = FetchType.LAZY)
    private Set<Reservation> reservations = new HashSet<>();

    @OneToMany(mappedBy = "reader", fetch = FetchType.LAZY)
    private Set<BorrowRecord> borrowRecords = new HashSet<>();

    // Librarian

    @OneToMany(mappedBy = "librarian", fetch = FetchType.LAZY)
    private Set<BorrowRecord> managedBorrowRecords = new HashSet<>();
}