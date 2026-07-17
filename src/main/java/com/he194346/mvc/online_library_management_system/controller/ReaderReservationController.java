package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.dto.reservation.ReservationItemRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.reservation.ReservationRequestDTO;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.service.BookService;
import com.he194346.mvc.online_library_management_system.service.CategoryService;
import com.he194346.mvc.online_library_management_system.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/reader")
@RequiredArgsConstructor
public class ReaderReservationController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final ReservationService reservationService;

    @GetMapping("/reservations/new")
    public String reservationForm(@RequestParam(required = false) Long bookId) {
        if (bookId == null) {
            return "redirect:/reader/books";
        }
        return "redirect:/reader/books?bookId=" + bookId;
    }

    @PostMapping("/reservations")
    public String createReservation(@Valid @ModelAttribute("reservationRequest") ReservationRequestDTO request,
                                    BindingResult bindingResult, Authentication authentication,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(required = false) String title,
                                    @RequestParam(required = false) String author,
                                    @RequestParam(required = false) Long categoryId,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu reservation không hợp lệ. Vui lòng kiểm tra lại.");
            loadBookList(model, request, page, title, author, categoryId);
            return "reader/book/list";
        }

        try {
            Long reservationId = reservationService.createHolding(authentication.getName(), request);
            String message = "Đã giữ sách thành công. Hãy gửi duyệt trước khi hết 2 giờ.";
            redirectAttributes.addFlashAttribute("success", message);
            return "redirect:/reader/reservations/" + reservationId;
        } catch (CustomException exception) {
            model.addAttribute("error", exception.getMessage());
            loadBookList(model, request, page, title, author, categoryId);
            return "reader/book/list";
        }
    }

    @GetMapping("/reservations")
    public String myReservations(Authentication authentication, Model model) {
        model.addAttribute("reservations", reservationService.findByReader(authentication.getName()));
        return "reader/reservation-list";
    }

    @GetMapping("/reservations/{id}")
    public String reservationDetail(@PathVariable Long id, Authentication authentication, Model model) {
        model.addAttribute("reservation", reservationService.findOwnedReservation(id, authentication.getName()));
        return "reader/reservation-detail";
    }

    @PostMapping("/reservations/{id}/submit")
    public String submit(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            reservationService.submitForApproval(id, authentication.getName());
            String message = "Reservation đã được gửi và đang chờ thư viện phê duyệt.";
            redirectAttributes.addFlashAttribute("success", message);
        } catch (CustomException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/reader/reservations/" + id;
    }

    private void loadBookList(Model model, ReservationRequestDTO request, int page, String title, String author,
                              Long categoryId) {
        model.addAttribute("bookPage", bookService.searchActiveBooksForReader(title, author, categoryId, page));
        model.addAttribute("allCategories", categoryService.findAll());
        model.addAttribute("title", title);
        model.addAttribute("author", author);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("hasFilters", bookService.hasReaderBookFilters(title, author, categoryId));
        model.addAttribute("reservationRequest", request);
        model.addAttribute("selectedQuantities", getSelectedQuantities(request));
    }

    private Map<Long, Integer> getSelectedQuantities(ReservationRequestDTO request) {
        Map<Long, Integer> selectedQuantities = new LinkedHashMap<>();
        if (request == null || request.getItems() == null) {
            return selectedQuantities;
        }

        for (ReservationItemRequestDTO item : request.getItems()) {
            if (item != null && item.getBookId() != null && item.getQuantity() != null && item.getQuantity() > 0) {
                selectedQuantities.put(item.getBookId(), item.getQuantity());
            }
        }
        return selectedQuantities;
    }
}
