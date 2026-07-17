package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.BookCopy;
import com.he194346.mvc.online_library_management_system.enums.BookCopyStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    long countByBookBookId(Long bookId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select bc from BookCopy bc
            where bc.book.bookId = :bookId and bc.status = :status
            order by bc.bookCopyId
            """)
    List<BookCopy> findAllByBookIdAndStatusForUpdate(@Param("bookId") Long bookId,
                                                      @Param("status") BookCopyStatus status);
}
