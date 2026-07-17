package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.dto.reservation.ReservationItemRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.reservation.ReservationRequestDTO;
import com.he194346.mvc.online_library_management_system.entity.Book;
import com.he194346.mvc.online_library_management_system.enums.BookStatus;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.repository.BookRepository;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/reader")
@RequiredArgsConstructor
public class ReaderReservationController {

    private final BookRepository bookRepository;
    private final ReservationService reservationService;

    @GetMapping("/books")
    public String viewBooks(@RequestParam(required = false) Long bookId, Model model) {
        ReservationRequestDTO request = createInitialRequest(bookId);
        loadBookList(model, request);
        return "reader/book-list";
    }

    @GetMapping("/reservations/new")
    public String reservationForm(@RequestParam(required = false) Long bookId) {
        if (bookId == null) {
            return "redirect:/reader/books";
        }
        return "redirect:/reader/books?bookId=" + bookId;
    }

    @PostMapping("/reservations")
    public String createReservation(@Valid @ModelAttribute("reservationRequest") ReservationRequestDTO request,
                                    BindingResult bindingResult, Authentication authentication, Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu reservation không hợp lệ. Vui lòng kiểm tra lại.");
            loadBookList(model, request);
            return "reader/book-list";
        }

        try {
            Long reservationId = reservationService.createHolding(authentication.getName(), request);
            String message = "Đã giữ sách thành công. Hãy gửi duyệt trước khi hết 2 giờ.";
            redirectAttributes.addFlashAttribute("success", message);
            return "redirect:/reader/reservations/" + reservationId;
        } catch (CustomException exception) {
            model.addAttribute("error", exception.getMessage());
            loadBookList(model, request);
            return "reader/book-list";
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

    private ReservationRequestDTO createInitialRequest(Long selectedBookId) {
        ReservationRequestDTO request = new ReservationRequestDTO();
        if (selectedBookId == null) {
            return request;
        }

        Optional<Book> result = bookRepository.findById(selectedBookId);
        if (result.isEmpty()) {
            return request;
        }

        Book book = result.get();
        if (book.getStatus() != BookStatus.ACTIVE || book.getAvailableCopies() == null
                || book.getAvailableCopies() <= 0) {
            return request;
        }

        ReservationItemRequestDTO item = new ReservationItemRequestDTO();
        item.setBookId(book.getBookId());
        item.setQuantity(1);
        request.getItems().add(item);
        return request;
    }

    private void loadBookList(Model model, ReservationRequestDTO request) {
        model.addAttribute("books", getActiveBooks());
        model.addAttribute("reservationRequest", request);
        model.addAttribute("selectedQuantities", getSelectedQuantities(request));
    }

    private List<Book> getActiveBooks() {
        return bookRepository.findAllByStatusOrderByTitleAsc(BookStatus.ACTIVE);
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
