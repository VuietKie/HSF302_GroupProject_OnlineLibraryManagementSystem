package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.Book;
import com.he194346.mvc.online_library_management_system.enums.BookStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Long> {
    List<Book> findByStatus(BookStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select b from Book b
            where b.bookId in :bookIds
            """)
    List<Book> findAllByIdForUpdate(@Param("bookIds") List<Long> bookIds);
}
