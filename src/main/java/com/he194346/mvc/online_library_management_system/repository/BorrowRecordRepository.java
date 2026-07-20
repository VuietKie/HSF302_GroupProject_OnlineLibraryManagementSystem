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

    long countByStatus(BorrowStatus status);

    @Query("""
            select year(b.borrowDate), month(b.borrowDate), count(b)
            from BorrowRecord b
            where b.borrowDate is not null
            group by year(b.borrowDate), month(b.borrowDate)
            order by year(b.borrowDate), month(b.borrowDate)
            """)
    List<Object[]> findMonthlyBorrowCounts();

    @Query("""
            select distinct b
            from BorrowRecord b
            left join fetch b.books bb
            left join fetch bb.bookCopy bc
            left join fetch bc.book
            where b.reader.userId = :readerId
            order by b.requestDate desc
            """)
    List<BorrowRecord> findBorrowHistoryByReaderId(@Param("readerId") Long readerId);

    @Query("""
            select count(b) > 0
            from BorrowRecord b
            where b.borrowRecordId = :borrowRecordId
              and b.reader.userId = :readerId
            """)
    boolean existsOwnedBorrowRecord(@Param("borrowRecordId") Long borrowRecordId, @Param("readerId") Long readerId);
}
