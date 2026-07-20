package com.he194346.mvc.online_library_management_system.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StatisticsOverviewDTO {
    private final long totalBorrowRecords;
    private final long currentlyBorrowedCount;
    private final long returnedCount;
    private final long overdueCount;
    private final long requestedCount;
    private final List<BorrowTrendPointDTO> monthlyBorrowTrends;
    private final List<TopBorrowedBookDTO> topBorrowedBooks;
}
