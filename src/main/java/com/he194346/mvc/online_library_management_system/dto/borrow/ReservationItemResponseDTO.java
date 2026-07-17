package com.he194346.mvc.online_library_management_system.dto.borrow;

import com.he194346.mvc.online_library_management_system.dto.book.BookSummaryDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationItemResponseDTO {
    private Long bookReservationId;
    private BookSummaryDTO book;
    private Integer quantity;
}
