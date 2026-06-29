package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.entity.Book;
import com.he194346.mvc.online_library_management_system.entity.BookReservation;
import com.he194346.mvc.online_library_management_system.entity.Reservation;
import com.he194346.mvc.online_library_management_system.entity.User;
import com.he194346.mvc.online_library_management_system.enums.BookStatus;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import com.he194346.mvc.online_library_management_system.enums.ReservationStatus;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.repository.BookRepository;
import com.he194346.mvc.online_library_management_system.repository.BookReservationRepository;
import com.he194346.mvc.online_library_management_system.repository.ReservationRepository;
import com.he194346.mvc.online_library_management_system.repository.UserRepository;
import com.he194346.mvc.online_library_management_system.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final BookReservationRepository bookReservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public List<Book> availableBooks() {
        //chỉ hiện sách đang hoạt động và còn ít nhất 1 bản trống để giữ chỗ
        return bookRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookStatus.ACTIVE
                        && b.getAvailableCopies() != null
                        && b.getAvailableCopies() > 0)
                .toList();
    }

    @Override
    public void reserve(String readerEmail, Long bookId) {
        User reader = userRepository.findByEmail(readerEmail);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND, "Không tìm thấy sách"));

        //tạo phiếu giữ chỗ: hiệu lực 2 ngày, trạng thái chờ duyệt
        Reservation reservation = new Reservation();
        reservation.setReader(reader);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setExpiredAt(LocalDateTime.now().plusDays(2));
        reservation.setStatus(ReservationStatus.PENDING);
        //lưu phiếu TRƯỚC để có id, vì quan hệ books không cascade
        reservationRepository.save(reservation);

        //gắn sách vào phiếu vừa lưu (mỗi lần giữ 1 cuốn)
        BookReservation bookReservation = new BookReservation();
        bookReservation.setReservation(reservation);
        bookReservation.setBook(book);
        bookReservation.setQuantity(1);
        bookReservationRepository.save(bookReservation);
    }

    @Override
    public List<Reservation> myReservations(String readerEmail) {
        User reader = userRepository.findByEmail(readerEmail);
        return reservationRepository.findByReader_UserIdOrderByReservedAtDesc(reader.getUserId());
    }

    @Override
    public void cancel(String readerEmail, Long reservationId) {
        User reader = userRepository.findByEmail(readerEmail);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND, "Không tìm thấy phiếu giữ chỗ"));

        //chặn huỷ phiếu của người khác -> coi như không tìm thấy để không lộ dữ liệu
        if (!reservation.getReader().getUserId().equals(reader.getUserId())) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND, "Không tìm thấy phiếu giữ chỗ");
        }

        //chỉ huỷ được khi còn chờ duyệt (PENDING)
        if (reservation.getStatus() == ReservationStatus.PENDING) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
        }
    }
}
