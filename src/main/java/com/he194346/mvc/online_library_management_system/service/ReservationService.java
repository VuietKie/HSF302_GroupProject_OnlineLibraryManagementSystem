package com.he194346.mvc.online_library_management_system.service;

import com.he194346.mvc.online_library_management_system.entity.Book;
import com.he194346.mvc.online_library_management_system.entity.Reservation;

import java.util.List;

public interface ReservationService {
    // Danh sách sách còn có thể giữ chỗ (đang hoạt động + còn bản trống)
    List<Book> availableBooks();
    // Độc giả giữ chỗ 1 cuốn sách
    void reserve(String readerEmail, Long bookId);
    // Danh sách phiếu giữ chỗ của độc giả đang đăng nhập
    List<Reservation> myReservations(String readerEmail);
    // Huỷ 1 phiếu giữ chỗ (chỉ khi còn PENDING)
    void cancel(String readerEmail, Long reservationId);
}
