package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.BorrowRecord;
import com.he194346.mvc.online_library_management_system.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    @Query("""
            select distinct br
            from BorrowRecord br
            left join fetch br.books bbr
            left join fetch bbr.bookCopy bc
            left join fetch bc.book
            where br.reader.userId = :readerId
            order by br.requestDate desc
            """)
    List<BorrowRecord> findBorrowHistoryByReaderId(@Param("readerId") Long readerId);

    long countByStatus(BorrowStatus status);

    @Query("""
            select year(br.borrowDate), month(br.borrowDate), count(br)
            from BorrowRecord br
            where br.borrowDate is not null
            group by year(br.borrowDate), month(br.borrowDate)
            order by year(br.borrowDate), month(br.borrowDate)
            """)
    List<Object[]> findMonthlyBorrowCounts();
}
