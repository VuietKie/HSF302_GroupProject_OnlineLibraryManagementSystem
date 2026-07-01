package com.he194346.mvc.online_library_management_system.service;

import com.he194346.mvc.online_library_management_system.entity.Book;
import java.util.List;

public interface BookService {
    List<Book> getPendingBooks();
    void approveBook(Long bookId);
    void rejectBook(Long bookId);
}
