package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.dto.statistics.BorrowTrendPointDTO;
import com.he194346.mvc.online_library_management_system.dto.statistics.StatisticsOverviewDTO;
import com.he194346.mvc.online_library_management_system.dto.statistics.TopBorrowedBookDTO;
import com.he194346.mvc.online_library_management_system.enums.BorrowStatus;
import com.he194346.mvc.online_library_management_system.service.StatisticsService;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private static final DateTimeFormatter MONTH_LABEL_FORMAT = DateTimeFormatter.ofPattern("MM/yyyy");

    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public StatisticsOverviewDTO getBorrowingStatistics() {
        long totalBorrowRecords = countBorrowRecords();
        long currentlyBorrowedCount = countBorrowRecordsByStatus(BorrowStatus.BORROWED);
        long returnedCount = countBorrowRecordsByStatus(BorrowStatus.RETURNED);
        long overdueCount = countBorrowRecordsByStatus(BorrowStatus.OVERDUE);
        long requestedCount = countBorrowRecordsByStatus(BorrowStatus.REQUESTED);

        List<BorrowTrendPointDTO> monthlyBorrowTrends = buildTrendPoints(findMonthlyBorrowCounts());
        List<TopBorrowedBookDTO> topBorrowedBooks = buildTopBorrowedBooks(findTopBorrowedBooks());

        return new StatisticsOverviewDTO(
                totalBorrowRecords,
                currentlyBorrowedCount,
                returnedCount,
                overdueCount,
                requestedCount,
                monthlyBorrowTrends,
                topBorrowedBooks
        );
    }

    private long countBorrowRecords() {
        return entityManager.createQuery("select count(b) from BorrowRecord b", Long.class)
                .getSingleResult();
    }

    private long countBorrowRecordsByStatus(BorrowStatus status) {
        return entityManager.createQuery(
                        "select count(b) from BorrowRecord b where b.status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }

    private List<Object[]> findMonthlyBorrowCounts() {
        return entityManager.createQuery("""
                select year(b.borrowDate), month(b.borrowDate), count(b)
                from BorrowRecord b
                where b.borrowDate is not null
                group by year(b.borrowDate), month(b.borrowDate)
                order by year(b.borrowDate), month(b.borrowDate)
                """, Object[].class)
                .getResultList();
    }

    private List<Object[]> findTopBorrowedBooks() {
        return entityManager.createQuery("""
                select bbr.bookCopy.book.title, count(bbr)
                from BookBorrowRecord bbr
                group by bbr.bookCopy.book.title
                order by count(bbr) desc, bbr.bookCopy.book.title asc
                """, Object[].class)
                .getResultList();
    }

    private List<BorrowTrendPointDTO> buildTrendPoints(List<Object[]> rows) {
        Map<YearMonth, Long> trendMap = new LinkedHashMap<>();
        for (Object[] row : rows) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            long borrowCount = ((Number) row[2]).longValue();
            trendMap.put(YearMonth.of(year, month), borrowCount);
        }

        List<BorrowTrendPointDTO> result = new ArrayList<>();
        for (Map.Entry<YearMonth, Long> entry : trendMap.entrySet()) {
            result.add(new BorrowTrendPointDTO(entry.getKey().format(MONTH_LABEL_FORMAT), entry.getValue()));
        }
        return result;
    }

    private List<TopBorrowedBookDTO> buildTopBorrowedBooks(List<Object[]> rows) {
        List<TopBorrowedBookDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            String title = (String) row[0];
            long borrowCount = ((Number) row[1]).longValue();
            result.add(new TopBorrowedBookDTO(title, borrowCount));
        }
        return result;
    }
}
