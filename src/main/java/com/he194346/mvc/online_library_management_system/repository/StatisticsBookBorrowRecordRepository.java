package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.BookBorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatisticsBookBorrowRecordRepository extends JpaRepository<BookBorrowRecord, Long> {

    @Query("""
            select bbr.bookCopy.book.title, count(bbr)
            from BookBorrowRecord bbr
            group by bbr.bookCopy.book.title
            order by count(bbr) desc, bbr.bookCopy.book.title asc
            """)
    List<Object[]> findTopBorrowedBooks();
}
