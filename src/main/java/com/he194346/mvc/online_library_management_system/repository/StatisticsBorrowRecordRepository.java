package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.BorrowRecord;
import com.he194346.mvc.online_library_management_system.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatisticsBorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    long countByStatus(BorrowStatus status);

    @Query("""
            select year(b.borrowDate), month(b.borrowDate), count(b)
            from BorrowRecord b
            where b.borrowDate is not null
            group by year(b.borrowDate), month(b.borrowDate)
            order by year(b.borrowDate), month(b.borrowDate)
            """)
    List<Object[]> findMonthlyBorrowCounts();
}
