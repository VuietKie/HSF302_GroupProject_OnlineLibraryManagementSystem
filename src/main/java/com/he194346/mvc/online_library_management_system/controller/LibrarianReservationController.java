package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping("/{id}/approve")
    public String approveReservation(@PathVariable Long id,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        try {
            Long borrowRecordId = reservationService.approveReservation(id, authentication.getName());
            redirectAttributes.addFlashAttribute(
                    "success", "Đã approve reservation #" + id + " và tạo BorrowRecord #" + borrowRecordId);
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/librarian/reservations";
    }

    @PostMapping("/{id}/reject")
    public String rejectReservation(@PathVariable Long id,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            reservationService.rejectReservation(id, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Đã reject reservation #" + id);
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/librarian/reservations";
    }
}
