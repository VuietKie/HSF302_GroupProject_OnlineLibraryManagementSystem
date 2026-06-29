package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reader/reservations")
@AllArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    //[READ] Phiếu giữ chỗ của độc giả đang đăng nhập (authentication.getName() = email)
    @GetMapping
    public String list(Authentication authentication, Model model) {
        model.addAttribute("reservations", reservationService.myReservations(authentication.getName()));
        return "reservation/list";
    }

    //[READ] Danh sách sách còn có thể giữ chỗ
    @GetMapping("/books")
    public String books(Model model) {
        model.addAttribute("books", reservationService.availableBooks());
        return "reservation/books";
    }

    //[CREATE] Giữ chỗ 1 cuốn sách
    @PostMapping("/reserve/{bookId}")
    public String reserve(@PathVariable Long bookId, Authentication authentication) {
        reservationService.reserve(authentication.getName(), bookId);
        return "redirect:/reader/reservations";
    }

    //[UPDATE] Huỷ phiếu giữ chỗ (chỉ phiếu PENDING của chính mình)
    @PostMapping("/cancel/{id}")
    public String cancel(@PathVariable Long id, Authentication authentication) {
        reservationService.cancel(authentication.getName(), id);
        return "redirect:/reader/reservations";
    }
}
