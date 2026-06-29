package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    // Thống kê số lượt mượn theo trạng thái (group-by trên enum status)
    @Query("select b.status, count(b) from BorrowRecord b group by b.status")
    List<Object[]> countByStatus();

    // Thống kê số lượt mượn theo tháng (yyyy-MM) bằng native query SQL Server FORMAT
    @Query(value = "select format(borrow_date,'yyyy-MM') as ym, count(*) as c from borrow_records where borrow_date is not null group by format(borrow_date,'yyyy-MM') order by ym", nativeQuery = true)
    List<Object[]> countByMonth();
}
