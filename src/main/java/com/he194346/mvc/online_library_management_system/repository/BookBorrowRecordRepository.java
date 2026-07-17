package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.BookBorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookBorrowRecordRepository extends JpaRepository<BookBorrowRecord, Long> {
}
