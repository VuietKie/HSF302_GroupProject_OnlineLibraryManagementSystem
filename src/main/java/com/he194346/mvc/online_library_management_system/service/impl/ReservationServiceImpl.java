package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.dto.reservation.ReservationItemRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.reservation.ReservationRequestDTO;
import com.he194346.mvc.online_library_management_system.entity.Book;
import com.he194346.mvc.online_library_management_system.entity.BookBorrowRecord;
import com.he194346.mvc.online_library_management_system.entity.BookCopy;
import com.he194346.mvc.online_library_management_system.entity.BookReservation;
import com.he194346.mvc.online_library_management_system.entity.BorrowRecord;
import com.he194346.mvc.online_library_management_system.entity.Reservation;
import com.he194346.mvc.online_library_management_system.entity.User;
import com.he194346.mvc.online_library_management_system.enums.BookCopyStatus;
import com.he194346.mvc.online_library_management_system.enums.BookStatus;
import com.he194346.mvc.online_library_management_system.enums.BorrowStatus;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import com.he194346.mvc.online_library_management_system.enums.FineStatus;
import com.he194346.mvc.online_library_management_system.enums.ReservationStatus;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.repository.BookBorrowRecordRepository;
import com.he194346.mvc.online_library_management_system.repository.BookCopyRepository;
import com.he194346.mvc.online_library_management_system.repository.BookRepository;
import com.he194346.mvc.online_library_management_system.repository.BookReservationRepository;
import com.he194346.mvc.online_library_management_system.repository.BorrowRecordRepository;
import com.he194346.mvc.online_library_management_system.repository.ReservationRepository;
import com.he194346.mvc.online_library_management_system.repository.UserRepository;
import com.he194346.mvc.online_library_management_system.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private static final long HOLDING_HOURS = 2;
    private static final long BORROW_DAYS = 14;

    private final ReservationRepository reservationRepository;
    private final BookReservationRepository bookReservationRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookBorrowRecordRepository bookBorrowRecordRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Long createHolding(String readerEmail, ReservationRequestDTO request) {
        // Lấy người đọc đang đăng nhập và chuẩn hóa danh sách sách được gửi từ form.
        User reader = findReader(readerEmail);
        Map<Long, Integer> requestedQuantities = normalizeItems(request);
        validateReservationHasBooks(requestedQuantities);

        // Sắp xếp ID trước khi khóa để các request luôn khóa sách theo cùng thứ tự,
        // giúp hạn chế deadlock khi nhiều người cùng đặt sách.
        List<Long> bookIds = getSortedBookIds(requestedQuantities);
        Map<Long, Book> booksById = findAndLockBooks(bookIds);
        validateRequestedBooks(bookIds, requestedQuantities, booksById);

        // Tạo reservation HOLDING, tạo các dòng BookReservation và trừ tồn kho.
        Reservation reservation = createHoldingReservation(reader);
        addBooksToReservation(reservation, bookIds, requestedQuantities, booksById);
        saveReservationBooks(reservation, booksById);

        return reservation.getReservationId();
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation findOwnedReservation(Long reservationId, String readerEmail) {
        // Tìm theo cả ID và email để reader không thể xem reservation của người khác.
        Optional<Reservation> result = reservationRepository.findByReservationIdAndReaderEmail(reservationId, readerEmail);
        if (result.isEmpty()) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND, "Không tìm thấy reservation");
        }
        return result.get();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByReader(String readerEmail) {
        return reservationRepository.findAllByReaderEmailOrderByReservedAtDesc(readerEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findWaitingForApproval() {
        return reservationRepository.findAllByStatusOrderBySubmittedAt(ReservationStatus.WAITING_FOR_APPROVAL);
    }

    @Override
    @Transactional(noRollbackFor = CustomException.class)
    public void submitForApproval(Long reservationId, String readerEmail) {
        // Khóa reservation để scheduler không thể đồng thời chuyển nó sang EXPIRED.
        Reservation reservation = findOwnedReservationForUpdate(reservationId, readerEmail);
        validateHoldingStatus(reservation);

        // Nếu đã quá 2 giờ thì hoàn sách trước khi báo lỗi cho reader.
        // noRollbackFor đảm bảo thao tác hoàn sách vẫn được commit.
        if (isHoldingExpired(reservation)) {
            expireReservation(reservation);
            throw new CustomException(ErrorCode.INVALID_RESERVATION, "Reservation đã hết thời gian giữ sách");
        }

        // Sau khi gửi duyệt, reservation được chờ Librarian xử lý không giới hạn thời gian.
        reservation.setStatus(ReservationStatus.WAITING_FOR_APPROVAL);
        reservation.setExpiredAt(null);
        reservation.setSubmittedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public Long approveReservation(Long reservationId, String librarianEmail) {
        Reservation reservation = findReservationForLibrarianUpdate(reservationId);
        validateWaitingForApprovalStatus(reservation);
        validateReservationContainsBooks(reservation);

        User librarian = findLibrarian(librarianEmail);
        BorrowRecord borrowRecord = createBorrowRecord(reservation, librarian);
        borrowRecord = borrowRecordRepository.save(borrowRecord);

        findAndLockBooks(getReservationBookIds(reservation));
        List<BookBorrowRecord> bookBorrowRecords = createBookBorrowRecords(borrowRecord, reservation);
        bookBorrowRecordRepository.saveAll(bookBorrowRecords);

        reservation.setStatus(ReservationStatus.APPROVED);
        reservationRepository.save(reservation);

        return borrowRecord.getBorrowRecordId();
    }

    @Override
    @Transactional
    public void rejectReservation(Long reservationId, String librarianEmail) {
        Reservation reservation = findReservationForLibrarianUpdate(reservationId);
        validateWaitingForApprovalStatus(reservation);
        validateReservationContainsBooks(reservation);
        findLibrarian(librarianEmail);

        List<Long> bookIds = getReservationBookIds(reservation);
        Map<Long, Book> lockedBooks = findAndLockBooks(bookIds);
        restoreAvailableCopies(reservation, lockedBooks);

        reservation.setStatus(ReservationStatus.REJECTED);
        bookRepository.saveAll(lockedBooks.values());
        reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void expireOverdueHoldings() {
        LocalDateTime now = LocalDateTime.now();

        // Chỉ lấy và khóa các reservation còn HOLDING đã vượt quá expiredAt.
        List<Reservation> overdueReservations =
                reservationRepository.findExpiredForUpdate(ReservationStatus.HOLDING, now);

        for (Reservation reservation : overdueReservations) {
            expireReservation(reservation);
        }
    }

    private User findReader(String readerEmail) {
        User reader = userRepository.findByEmail(readerEmail);
        if (reader == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người đọc");
        }
        return reader;
    }

    private User findLibrarian(String librarianEmail) {
        User librarian = userRepository.findByEmail(librarianEmail);
        if (librarian == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy thủ thư");
        }
        return librarian;
    }

    private void validateReservationHasBooks(Map<Long, Integer> requestedQuantities) {
        if (requestedQuantities.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION, "Vui lòng chọn ít nhất một cuốn sách");
        }
    }

    private List<Long> getSortedBookIds(Map<Long, Integer> requestedQuantities) {
        List<Long> bookIds = new ArrayList<>(requestedQuantities.keySet());
        Collections.sort(bookIds);
        return bookIds;
    }

    private Map<Long, Book> findAndLockBooks(List<Long> bookIds) {
        // Pessimistic write lock ngăn hai request cùng đọc một số lượng tồn kho cũ.
        List<Book> lockedBooks = bookRepository.findAllByIdForUpdate(bookIds);
        Map<Long, Book> booksById = convertBookListToMap(lockedBooks);

        if (booksById.size() != bookIds.size()) {
            throw new CustomException(ErrorCode.BOOK_NOT_FOUND, "Có sách không còn tồn tại");
        }
        return booksById;
    }

    private void validateRequestedBooks(List<Long> bookIds, Map<Long, Integer> quantities, Map<Long, Book> booksById) {
        for (Long bookId : bookIds) {
            Book book = booksById.get(bookId);
            int requestedQuantity = quantities.get(bookId);
            int availableCopies = getAvailableCopies(book);

            // Chỉ sách đang hoạt động mới được phép tạo reservation.
            if (book.getStatus() != BookStatus.ACTIVE) {
                String message = "Sách \"" + book.getTitle() + "\" hiện không hoạt động";
                throw new CustomException(ErrorCode.INVALID_RESERVATION, message);
            }

            // Số lượng yêu cầu của mỗi sách không được vượt quá số lượng còn sẵn.
            if (requestedQuantity > availableCopies) {
                String message = "Sách \"" + book.getTitle() + "\" chỉ còn " + availableCopies + " bản";
                throw new CustomException(ErrorCode.INSUFFICIENT_BOOK_COPIES, message);
            }
        }
    }

    private int getAvailableCopies(Book book) {
        if (book.getAvailableCopies() == null) {
            return 0;
        }
        return book.getAvailableCopies();
    }

    private Reservation createHoldingReservation(User reader) {
        LocalDateTime reservedAt = LocalDateTime.now();

        // Reservation mới giữ sách tối đa 2 giờ trước khi được gửi duyệt.
        Reservation reservation = new Reservation();
        reservation.setReader(reader);
        reservation.setReservedAt(reservedAt);
        reservation.setExpiredAt(reservedAt.plusHours(HOLDING_HOURS));
        reservation.setStatus(ReservationStatus.HOLDING);

        return reservationRepository.save(reservation);
    }

    private void addBooksToReservation(Reservation reservation, List<Long> bookIds,
                                       Map<Long, Integer> quantities, Map<Long, Book> booksById) {
        for (Long bookId : bookIds) {
            Book book = booksById.get(bookId);
            int quantity = quantities.get(bookId);

            // Trừ tồn kho ngay khi tạo HOLDING để người khác không đặt vào số sách đã giữ.
            int newAvailableCopies = book.getAvailableCopies() - quantity;
            book.setAvailableCopies(newAvailableCopies);

            BookReservation bookReservation = createBookReservation(reservation, book, quantity);
            reservation.getBooks().add(bookReservation);
        }
    }

    private BookReservation createBookReservation(Reservation reservation, Book book, int quantity) {
        BookReservation bookReservation = new BookReservation();
        bookReservation.setReservation(reservation);
        bookReservation.setBook(book);
        bookReservation.setQuantity(quantity);
        return bookReservation;
    }

    private void saveReservationBooks(Reservation reservation, Map<Long, Book> booksById) {
        bookRepository.saveAll(booksById.values());
        bookReservationRepository.saveAll(reservation.getBooks());
    }

    private BorrowRecord createBorrowRecord(Reservation reservation, User librarian) {
        LocalDateTime borrowDate = LocalDateTime.now();

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setReader(reservation.getReader());
        borrowRecord.setLibrarian(librarian);
        borrowRecord.setReservation(reservation);
        borrowRecord.setRequestDate(getRequestDate(reservation));
        borrowRecord.setBorrowDate(borrowDate);
        borrowRecord.setDueDate(borrowDate.plusDays(BORROW_DAYS));
        borrowRecord.setFineAmount(0.0);
        borrowRecord.setFineStatus(FineStatus.PAID);
        borrowRecord.setStatus(BorrowStatus.BORROWED);
        return borrowRecord;
    }

    private LocalDateTime getRequestDate(Reservation reservation) {
        if (reservation.getSubmittedAt() != null) {
            return reservation.getSubmittedAt();
        }
        return reservation.getReservedAt();
    }

    private List<BookBorrowRecord> createBookBorrowRecords(BorrowRecord borrowRecord, Reservation reservation) {
        List<BookBorrowRecord> bookBorrowRecords = new ArrayList<>();
        List<BookCopy> borrowedCopies = new ArrayList<>();

        for (BookReservation bookReservation : reservation.getBooks()) {
            Book book = bookReservation.getBook();
            int quantity = bookReservation.getQuantity();

            ensureBookCopyRowsExist(book);
            List<BookCopy> availableCopies = bookCopyRepository.findAllByBookIdAndStatusForUpdate(
                    book.getBookId(), BookCopyStatus.AVAILABLE);

            if (availableCopies.size() < quantity) {
                String message = "Sách \"" + book.getTitle() + "\" không đủ bản sách khả dụng để duyệt";
                throw new CustomException(ErrorCode.INSUFFICIENT_BOOK_COPIES, message);
            }

            for (int index = 0; index < quantity; index++) {
                BookCopy bookCopy = availableCopies.get(index);
                bookCopy.setStatus(BookCopyStatus.BORROWED);
                borrowedCopies.add(bookCopy);
                bookBorrowRecords.add(createBookBorrowRecord(borrowRecord, bookCopy));
            }
        }

        bookCopyRepository.saveAll(borrowedCopies);
        return bookBorrowRecords;
    }

    private void ensureBookCopyRowsExist(Book book) {
        int totalCopies = getTotalCopies(book);
        long existingCopies = bookCopyRepository.countByBookBookId(book.getBookId());

        if (existingCopies >= totalCopies) {
            return;
        }

        List<BookCopy> missingCopies = new ArrayList<>();
        for (long copyNumber = existingCopies + 1; copyNumber <= totalCopies; copyNumber++) {
            BookCopy bookCopy = new BookCopy();
            bookCopy.setBook(book);
            bookCopy.setBarCode("BOOK-" + book.getBookId() + "-" + copyNumber);
            bookCopy.setCreatedAt(LocalDateTime.now());
            bookCopy.setStatus(BookCopyStatus.AVAILABLE);
            missingCopies.add(bookCopy);
        }
        bookCopyRepository.saveAll(missingCopies);
    }

    private int getTotalCopies(Book book) {
        if (book.getTotalCopies() == null || book.getTotalCopies() < 0) {
            return 0;
        }
        return book.getTotalCopies();
    }

    private BookBorrowRecord createBookBorrowRecord(BorrowRecord borrowRecord, BookCopy bookCopy) {
        BookBorrowRecord bookBorrowRecord = new BookBorrowRecord();
        bookBorrowRecord.setBorrowRecord(borrowRecord);
        bookBorrowRecord.setBookCopy(bookCopy);
        borrowRecord.getBooks().add(bookBorrowRecord);
        return bookBorrowRecord;
    }

    private Map<Long, Integer> normalizeItems(ReservationRequestDTO request) {
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        if (request == null || request.getItems() == null) {
            return quantities;
        }

        // Bỏ các dòng có số lượng bằng 0 và gộp số lượng nếu một bookId xuất hiện nhiều lần.
        for (ReservationItemRequestDTO item : request.getItems()) {
            addValidItemQuantity(quantities, item);
        }
        return quantities;
    }

    private void addValidItemQuantity(Map<Long, Integer> quantities, ReservationItemRequestDTO item) {
        if (item == null || item.getBookId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
            return;
        }

        Long bookId = item.getBookId();
        int currentQuantity = 0;
        if (quantities.containsKey(bookId)) {
            currentQuantity = quantities.get(bookId);
        }

        long totalQuantity = (long) currentQuantity + item.getQuantity();
        if (totalQuantity > Integer.MAX_VALUE) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION, "Số lượng sách yêu cầu không hợp lệ");
        }
        quantities.put(bookId, (int) totalQuantity);
    }

    private Reservation findReservationForLibrarianUpdate(Long reservationId) {
        Optional<Reservation> result = reservationRepository.findByIdForLibrarianUpdate(reservationId);
        if (result.isEmpty()) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND, "Không tìm thấy reservation");
        }
        return result.get();
    }

    private Reservation findOwnedReservationForUpdate(Long reservationId, String readerEmail) {
        Optional<Reservation> result = reservationRepository.findOwnedForUpdate(reservationId, readerEmail);
        if (result.isEmpty()) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND, "Không tìm thấy reservation");
        }
        return result.get();
    }

    private void validateHoldingStatus(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.HOLDING) {
            throw new CustomException(
                    ErrorCode.INVALID_RESERVATION, "Chỉ reservation đang HOLDING mới có thể gửi duyệt");
        }
    }

    private void validateWaitingForApprovalStatus(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.WAITING_FOR_APPROVAL) {
            throw new CustomException(
                    ErrorCode.INVALID_RESERVATION, "Chỉ reservation đang chờ phê duyệt mới có thể xử lý");
        }
    }

    private void validateReservationContainsBooks(Reservation reservation) {
        if (reservation.getBooks() == null || reservation.getBooks().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION, "Reservation không có sách để xử lý");
        }
    }

    private boolean isHoldingExpired(Reservation reservation) {
        LocalDateTime expiredAt = reservation.getExpiredAt();
        if (expiredAt == null) {
            return true;
        }
        return !expiredAt.isAfter(LocalDateTime.now());
    }

    private void expireReservation(Reservation reservation) {
        // Kiểm tra lại trạng thái để không hoàn kho hai lần cho cùng một reservation.
        if (reservation.getStatus() != ReservationStatus.HOLDING) {
            return;
        }

        // Khóa lại các sách trước khi cộng trả số lượng vào kho.
        List<Long> bookIds = getReservationBookIds(reservation);
        Map<Long, Book> lockedBooks = findAndLockBooks(bookIds);
        restoreAvailableCopies(reservation, lockedBooks);

        reservation.setStatus(ReservationStatus.EXPIRED);
        bookRepository.saveAll(lockedBooks.values());
        reservationRepository.save(reservation);
    }

    private List<Long> getReservationBookIds(Reservation reservation) {
        List<Long> bookIds = new ArrayList<>();
        for (BookReservation bookReservation : reservation.getBooks()) {
            bookIds.add(bookReservation.getBook().getBookId());
        }
        Collections.sort(bookIds);
        return bookIds;
    }

    private void restoreAvailableCopies(Reservation reservation, Map<Long, Book> lockedBooks) {
        for (BookReservation bookReservation : reservation.getBooks()) {
            Long bookId = bookReservation.getBook().getBookId();
            Book book = lockedBooks.get(bookId);
            int newAvailableCopies = book.getAvailableCopies() + bookReservation.getQuantity();
            book.setAvailableCopies(newAvailableCopies);
        }
    }

    private Map<Long, Book> convertBookListToMap(List<Book> books) {
        Map<Long, Book> booksById = new LinkedHashMap<>();
        for (Book book : books) {
            booksById.put(book.getBookId(), book);
        }
        return booksById;
    }
}
