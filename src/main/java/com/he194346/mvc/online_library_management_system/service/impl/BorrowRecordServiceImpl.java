package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.entity.Book;
import com.he194346.mvc.online_library_management_system.entity.BookBorrowRecord;
import com.he194346.mvc.online_library_management_system.entity.BookCopy;
import com.he194346.mvc.online_library_management_system.entity.BorrowRecord;
import com.he194346.mvc.online_library_management_system.enums.BookCopyStatus;
import com.he194346.mvc.online_library_management_system.enums.BorrowStatus;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import com.he194346.mvc.online_library_management_system.enums.FineStatus;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.repository.BookCopyRepository;
import com.he194346.mvc.online_library_management_system.repository.BookRepository;
import com.he194346.mvc.online_library_management_system.repository.BorrowRecordRepository;
import com.he194346.mvc.online_library_management_system.service.BorrowRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BorrowRecordServiceImpl implements BorrowRecordService {

    private static final double FINE_PER_DAY = 5000.0;

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecord> findCurrentlyBorrowed() {
        return borrowRecordRepository.findAllByStatusInOrderByDueDateAsc(
                List.of(BorrowStatus.BORROWED, BorrowStatus.OVERDUE));
    }

    @Override
    @Transactional
    public void returnBorrowRecord(Long borrowRecordId) {
        // Khóa phiếu mượn trước để 2 request trả cùng lúc không cùng xử lý 1 phiếu.
        BorrowRecord borrowRecord = findBorrowRecordForUpdate(borrowRecordId);
        validateCurrentlyBorrowed(borrowRecord);

        LocalDateTime now = LocalDateTime.now();
        applyFine(borrowRecord, now);

        borrowRecord.setReturnDate(now);
        borrowRecord.setStatus(BorrowStatus.RETURNED);

        // Trả từng bản copy về kho rồi cộng lại availableCopies theo số lượng đã trả của mỗi sách.
        Map<Long, Integer> returnedQuantitiesByBook = releaseBookCopies(borrowRecord);
        restoreAvailableCopies(returnedQuantitiesByBook);

        borrowRecordRepository.save(borrowRecord);
    }

    private BorrowRecord findBorrowRecordForUpdate(Long borrowRecordId) {
        return borrowRecordRepository.findByIdForUpdate(borrowRecordId)
                .orElseThrow(() -> new CustomException(ErrorCode.BORROW_RECORD_NOT_FOUND, "Không tìm thấy phiếu mượn"));
    }

    private void validateCurrentlyBorrowed(BorrowRecord borrowRecord) {
        // Chỉ phiếu đang BORROWED/OVERDUE mới được ghi nhận trả, tránh trả 2 lần cho cùng 1 phiếu.
        BorrowStatus status = borrowRecord.getStatus();
        if (status != BorrowStatus.BORROWED && status != BorrowStatus.OVERDUE) {
            throw new CustomException(ErrorCode.INVALID_BORROW_RECORD, "Phiếu mượn này đã được xử lý trả sách");
        }
    }

    private void applyFine(BorrowRecord borrowRecord, LocalDateTime now) {
        // Chỉ tính phạt khi trả trễ hạn; đúng hạn thì giữ nguyên fineAmount=0.0/fineStatus=PAID từ lúc approve.
        long overdueDays = ChronoUnit.DAYS.between(borrowRecord.getDueDate(), now);
        if (overdueDays > 0) {
            borrowRecord.setFineAmount(overdueDays * FINE_PER_DAY);
            borrowRecord.setFineStatus(FineStatus.UNPAID);
        }
    }

    private Map<Long, Integer> releaseBookCopies(BorrowRecord borrowRecord) {
        Map<Long, Integer> quantitiesByBook = new LinkedHashMap<>();
        List<BookCopy> returnedCopies = new ArrayList<>();

        for (BookBorrowRecord bookBorrowRecord : borrowRecord.getBooks()) {
            BookCopy bookCopy = bookBorrowRecord.getBookCopy();
            bookCopy.setStatus(BookCopyStatus.AVAILABLE);
            returnedCopies.add(bookCopy);

            Long bookId = bookCopy.getBook().getBookId();
            int currentQuantity = quantitiesByBook.getOrDefault(bookId, 0);
            quantitiesByBook.put(bookId, currentQuantity + 1);
        }

        bookCopyRepository.saveAll(returnedCopies);
        return quantitiesByBook;
    }

    private void restoreAvailableCopies(Map<Long, Integer> quantitiesByBook) {
        List<Long> bookIds = new ArrayList<>(quantitiesByBook.keySet());
        Collections.sort(bookIds);

        // Sắp xếp ID trước khi khóa để tránh deadlock, giống pattern trong ReservationServiceImpl.
        List<Book> lockedBooks = bookRepository.findAllByIdForUpdate(bookIds);
        if (lockedBooks.size() != bookIds.size()) {
            throw new CustomException(ErrorCode.BOOK_NOT_FOUND, "Có sách không còn tồn tại");
        }
        for (Book book : lockedBooks) {
            int returnedQuantity = quantitiesByBook.get(book.getBookId());
            int currentAvailable = book.getAvailableCopies() == null ? 0 : book.getAvailableCopies();
            book.setAvailableCopies(currentAvailable + returnedQuantity);
        }
        bookRepository.saveAll(lockedBooks);
    }
}
