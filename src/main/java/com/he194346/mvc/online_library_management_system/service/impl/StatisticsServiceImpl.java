package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.dto.statistics.BorrowTrendPointDTO;
import com.he194346.mvc.online_library_management_system.dto.statistics.StatisticsOverviewDTO;
import com.he194346.mvc.online_library_management_system.dto.statistics.TopBorrowedBookDTO;
import com.he194346.mvc.online_library_management_system.enums.BorrowStatus;
import com.he194346.mvc.online_library_management_system.repository.StatisticsBookBorrowRecordRepository;
import com.he194346.mvc.online_library_management_system.repository.StatisticsBorrowRecordRepository;
import com.he194346.mvc.online_library_management_system.service.StatisticsService;
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

    private final StatisticsBorrowRecordRepository statisticsBorrowRecordRepository;
    private final StatisticsBookBorrowRecordRepository statisticsBookBorrowRecordRepository;

    @Override
    @Transactional(readOnly = true)
    public StatisticsOverviewDTO getBorrowingStatistics() {
        long totalBorrowRecords = statisticsBorrowRecordRepository.count();
        long currentlyBorrowedCount = statisticsBorrowRecordRepository.countByStatus(BorrowStatus.BORROWED);
        long returnedCount = statisticsBorrowRecordRepository.countByStatus(BorrowStatus.RETURNED);
        long overdueCount = statisticsBorrowRecordRepository.countByStatus(BorrowStatus.OVERDUE);
        long requestedCount = statisticsBorrowRecordRepository.countByStatus(BorrowStatus.REQUESTED);

        List<BorrowTrendPointDTO> monthlyBorrowTrends = buildTrendPoints(
                statisticsBorrowRecordRepository.findMonthlyBorrowCounts());
        List<TopBorrowedBookDTO> topBorrowedBooks = buildTopBorrowedBooks(
                statisticsBookBorrowRecordRepository.findTopBorrowedBooks());

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
