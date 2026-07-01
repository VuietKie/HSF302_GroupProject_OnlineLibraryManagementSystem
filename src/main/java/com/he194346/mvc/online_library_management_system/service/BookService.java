package com.he194346.mvc.online_library_management_system.service;

import com.he194346.mvc.online_library_management_system.dto.book.BookRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.book.ReaderBookResponseDTO;
import com.he194346.mvc.online_library_management_system.entity.Book;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {
    List<Book> findAll();
    Book findById(Long id);
    Page<ReaderBookResponseDTO> searchActiveBooksForReader(String title, String author, Long categoryId, int page);
    ReaderBookResponseDTO findActiveBookDetailForReader(Long id);
    boolean hasReaderBookFilters(String title, String author, Long categoryId);
    void create(BookRequestDTO bookRequestDTO);
    void update(Long id, BookRequestDTO bookRequestDTO);
    void delete(Long id);
}
