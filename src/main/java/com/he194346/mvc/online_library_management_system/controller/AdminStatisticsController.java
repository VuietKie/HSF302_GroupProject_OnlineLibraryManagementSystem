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
public class AdminStatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public String statistics(Model model) {
        model.addAttribute("statistics", statisticsService.getBorrowingStatistics());
        return "admin/statistics";
    }
}
