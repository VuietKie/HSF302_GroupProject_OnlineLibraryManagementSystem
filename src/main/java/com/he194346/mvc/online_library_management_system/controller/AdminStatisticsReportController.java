package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.service.StatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/statistics-report")
@AllArgsConstructor
public class AdminStatisticsReportController {

    private final StatisticsService statisticsService;

    // [READ] Hiển thị trang báo cáo thống kê mượn sách
    @GetMapping
    public String viewStatistics(Model model) {
        // Lấy dữ liệu thống kê mượn sách từ StatisticsService
        // và gửi dữ liệu sang giao diện với tên "statistics"
        model.addAttribute("statistics", statisticsService.getBorrowingStatistics());
        return "admin/statistics-report";
    }
}
