package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.service.StatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/statistics")
@AllArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    //[READ] Bảng thống kê mượn sách: tổng số, theo trạng thái, theo tháng
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalBorrows", statisticsService.totalBorrows());
        model.addAttribute("statusCounts", statisticsService.countByStatus());
        model.addAttribute("monthCounts", statisticsService.countByMonth());
        return "statistic/dashboard";
    }
}
