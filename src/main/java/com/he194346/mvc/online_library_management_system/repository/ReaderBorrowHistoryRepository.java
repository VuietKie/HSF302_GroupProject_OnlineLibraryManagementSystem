package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReaderBorrowHistoryRepository extends JpaRepository<BorrowRecord, Long> {

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
