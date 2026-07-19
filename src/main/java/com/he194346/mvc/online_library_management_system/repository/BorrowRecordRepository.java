package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.BorrowRecord;
import com.he194346.mvc.online_library_management_system.enums.BorrowStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    @Query("""
            select distinct b from BorrowRecord b
            left join fetch b.reader
            left join fetch b.books bb
            left join fetch bb.bookCopy bc
            left join fetch bc.book
            where b.status in :statuses
            order by b.dueDate asc
            """)
    List<BorrowRecord> findAllByStatusInOrderByDueDateAsc(@Param("statuses") List<BorrowStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select distinct b from BorrowRecord b
            left join fetch b.books bb
            left join fetch bb.bookCopy bc
            left join fetch bc.book
            where b.borrowRecordId = :id
            """)
    Optional<BorrowRecord> findByIdForUpdate(@Param("id") Long id);
}
