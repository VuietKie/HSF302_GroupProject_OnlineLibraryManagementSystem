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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reader")
@RequiredArgsConstructor
public class ReaderReservationController {

    private final BookRepository bookRepository;
    private final ReservationService reservationService;

    @GetMapping("/books")
    public String viewBooks(Model model) {
        model.addAttribute("books", getActiveBooks());
        return "reader/book-list";
    }

    @GetMapping("/reservations/new")
    public String reservationForm(@RequestParam(required = false) Long bookId, Model model) {
        List<Book> books = getAvailableBooks();
        ReservationRequestDTO request = createReservationRequest(books, bookId);

        model.addAttribute("reservationRequest", request);
        model.addAttribute("books", books);
        return "reader/reservation-form";
    }

    @PostMapping("/reservations")
    public String createReservation(@Valid @ModelAttribute("reservationRequest") ReservationRequestDTO request,
                                    BindingResult bindingResult, Authentication authentication, Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            loadReservationForm(model, request);
            return "reader/reservation-form";
        }

        try {
            Long reservationId = reservationService.createHolding(authentication.getName(), request);
            String message = "Đã giữ sách thành công. Hãy gửi duyệt trước khi hết 2 giờ.";
            redirectAttributes.addFlashAttribute("success", message);
            return "redirect:/reader/reservations/" + reservationId;
        } catch (CustomException exception) {
            model.addAttribute("error", exception.getMessage());
            loadReservationForm(model, request);
            return "reader/reservation-form";
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

    private ReservationRequestDTO createReservationRequest(List<Book> books, Long selectedBookId) {
        ReservationRequestDTO request = new ReservationRequestDTO();

        for (Book book : books) {
            int quantity = 0;
            if (book.getBookId().equals(selectedBookId)) {
                quantity = 1;
            }

            ReservationItemRequestDTO item = new ReservationItemRequestDTO();
            item.setBookId(book.getBookId());
            item.setQuantity(quantity);
            request.getItems().add(item);
        }
        return request;
    }

    private void loadReservationForm(Model model, ReservationRequestDTO request) {
        model.addAttribute("books", getBooksForRequest(request));
    }

    private List<Book> getActiveBooks() {
        return bookRepository.findAllByStatusOrderByTitleAsc(BookStatus.ACTIVE);
    }

    private List<Book> getAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        List<Book> activeBooks = getActiveBooks();

        for (Book book : activeBooks) {
            Integer availableCopies = book.getAvailableCopies();
            if (availableCopies != null && availableCopies > 0) {
                availableBooks.add(book);
            }
        }
        return availableBooks;
    }

    private List<Book> getBooksForRequest(ReservationRequestDTO request) {
        List<Long> bookIds = getBookIdsFromRequest(request);
        List<Book> booksFromDatabase = bookRepository.findAllById(bookIds);
        Map<Long, Book> booksById = convertBookListToMap(booksFromDatabase);
        List<Book> orderedBooks = new ArrayList<>();

        for (Long bookId : bookIds) {
            Book book = booksById.get(bookId);
            if (book != null) {
                orderedBooks.add(book);
            }
        }
        return orderedBooks;
    }

    private List<Long> getBookIdsFromRequest(ReservationRequestDTO request) {
        List<Long> bookIds = new ArrayList<>();
        if (request == null || request.getItems() == null) {
            return bookIds;
        }

        for (ReservationItemRequestDTO item : request.getItems()) {
            if (item != null && item.getBookId() != null) {
                bookIds.add(item.getBookId());
            }
        }
        return bookIds;
    }

    private Map<Long, Book> convertBookListToMap(List<Book> books) {
        Map<Long, Book> booksById = new LinkedHashMap<>();
        for (Book book : books) {
            booksById.put(book.getBookId(), book);
        }
        return booksById;
    }
}
