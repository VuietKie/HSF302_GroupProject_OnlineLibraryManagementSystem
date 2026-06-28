package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Long> {
}
