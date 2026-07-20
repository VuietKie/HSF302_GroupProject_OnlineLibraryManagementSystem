package com.he194346.mvc.online_library_management_system.service;

import com.he194346.mvc.online_library_management_system.dto.statistics.StatisticsOverviewDTO;

public interface StatisticsService {
    StatisticsOverviewDTO getBorrowingStatistics();
}
