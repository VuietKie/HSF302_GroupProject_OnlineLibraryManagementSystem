package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.dto.book.ReaderBookResponseDTO;
import com.he194346.mvc.online_library_management_system.dto.reservation.ReservationItemRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.reservation.ReservationRequestDTO;
import com.he194346.mvc.online_library_management_system.service.BookService;
import com.he194346.mvc.online_library_management_system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reader/books")
@RequiredArgsConstructor
public class ReaderBookController {

    private final BookService bookService;
    private final CategoryService categoryService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String title,
                       @RequestParam(required = false) String author,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) Long bookId,
                       Model model) {
        model.addAttribute("bookPage", bookService.searchActiveBooksForReader(title, author, categoryId, page));
        model.addAttribute("allCategories", categoryService.findAll());
        model.addAttribute("title", title);
        model.addAttribute("author", author);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("hasFilters", bookService.hasReaderBookFilters(title, author, categoryId));
        ReservationRequestDTO reservationRequest = createInitialRequest(bookId);
        model.addAttribute("reservationRequest", reservationRequest);
        model.addAttribute("selectedQuantities", bookId == null || reservationRequest.getItems().isEmpty()
                ? java.util.Collections.emptyMap()
                : java.util.Collections.singletonMap(bookId, 1));
        return "reader/book/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.findActiveBookDetailForReader(id));
        return "reader/book/detail";
    }

    private ReservationRequestDTO createInitialRequest(Long bookId) {
        ReservationRequestDTO request = new ReservationRequestDTO();
        if (bookId == null) {
            return request;
        }

        ReaderBookResponseDTO book = bookService.findActiveBookDetailForReader(bookId);
        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            return request;
        }

        ReservationItemRequestDTO item = new ReservationItemRequestDTO();
        item.setBookId(bookId);
        item.setQuantity(1);
        request.getItems().add(item);
        return request;
    }
}
