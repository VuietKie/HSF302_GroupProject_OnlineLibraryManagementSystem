package com.he194346.mvc.online_library_management_system.service;

import java.util.List;

public interface StatisticsService {
    // Tổng số lượt mượn
    long totalBorrows();
    // Số lượt mượn theo trạng thái: mỗi phần tử [status, count]
    List<Object[]> countByStatus();
    // Số lượt mượn theo tháng: mỗi phần tử [yyyy-MM, count]
    List<Object[]> countByMonth();
}
