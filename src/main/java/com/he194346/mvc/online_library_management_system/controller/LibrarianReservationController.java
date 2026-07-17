package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/librarian/reservations")
@RequiredArgsConstructor
public class LibrarianReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public String viewWaitingReservations(Model model) {
        model.addAttribute("reservations", reservationService.findWaitingForApproval());
        return "librarian/reservation-list";
    }
}
