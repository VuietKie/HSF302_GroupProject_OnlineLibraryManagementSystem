package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.entity.Book;
import com.he194346.mvc.online_library_management_system.enums.BookStatus;
import com.he194346.mvc.online_library_management_system.repository.BookRepository;
import com.he194346.mvc.online_library_management_system.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<Book> getPendingBooks() {
        return bookRepository.findByStatus(BookStatus.PENDING);
    }

    @Override
    public void approveBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        book.setStatus(BookStatus.ACTIVE);
        bookRepository.save(book);
    }

    @Override
    public void rejectBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        book.setStatus(BookStatus.REJECTED);
        bookRepository.save(book);
    }
}
