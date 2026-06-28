package com.he194346.mvc.online_library_management_system.service;

import com.he194346.mvc.online_library_management_system.dto.book.BookRequestDTO;
import com.he194346.mvc.online_library_management_system.entity.Book;

import java.util.List;

public interface BookService {
    List<Book> findAll();
    Book findById(Long id);
    void create(BookRequestDTO bookRequestDTO);
    void update(Long id, BookRequestDTO bookRequestDTO);
    void delete(Long id);
}
