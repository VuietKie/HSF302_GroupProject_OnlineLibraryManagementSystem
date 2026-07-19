package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.dto.book.BookRequestDTO;
import com.he194346.mvc.online_library_management_system.entity.Author;
import com.he194346.mvc.online_library_management_system.entity.Book;
import com.he194346.mvc.online_library_management_system.entity.Category;
import com.he194346.mvc.online_library_management_system.enums.BookStatus;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.repository.AuthorRepository;
import com.he194346.mvc.online_library_management_system.repository.BookRepository;
import com.he194346.mvc.online_library_management_system.repository.CategoryRepository;
import com.he194346.mvc.online_library_management_system.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(()-> new CustomException(ErrorCode.BOOK_NOT_FOUND,"Không tìm thấy sách"));
    }

    @Override
    public void create(BookRequestDTO bookRequestDTO) {
        Book book = new Book();
        applyData(book,bookRequestDTO);
        bookRepository.save(book);
    }

    @Override
    public void update(Long id, BookRequestDTO bookRequestDTO) {
        Book book = findById(id);
        preserveModerationStatus(book, bookRequestDTO);
        applyData(book,bookRequestDTO);
        bookRepository.save(book);
    }

    @Override
    public void delete(Long id) {
        Book book = findById(id);
        bookRepository.delete(book);
    }

    private void applyData(Book book, BookRequestDTO bookRequestDTO) {
        book.setTitle(bookRequestDTO.getTitle());
        book.setDescription(bookRequestDTO.getDescription());
        book.setStatus(bookRequestDTO.getStatus());
        book.setTotalCopies(bookRequestDTO.getTotalCopies());
        book.setAvailableCopies(bookRequestDTO.getTotalCopies());// tạm: số sẵn= tổng(chưa quản BookCopy)
        if (bookRequestDTO.getCoverImg()!=null){
            book.setCoverImg(bookRequestDTO.getCoverImg());
        }
        Set<Author> authors= new HashSet<>(authorRepository.findAllById(bookRequestDTO.getAuthorIds()));
        Set<Category> categories= new HashSet<>(categoryRepository.findAllById(bookRequestDTO.getCategoryIds()));
        book.setAuthors(authors);
        book.setCategories(categories);
    }

    private void preserveModerationStatus(Book book, BookRequestDTO bookRequestDTO) {
        if (book.getStatus() == BookStatus.PENDING || book.getStatus() == BookStatus.REJECTED) {
            bookRequestDTO.setStatus(book.getStatus());
        }
    }

    // UC10: Approve New Books
    @Override
    public List<Book> getPendingBooks() {
        return bookRepository.findByStatus(BookStatus.PENDING);
    }

    @Override
    public void approveBook(Long id) {
        Book book = findById(id);
        if (book.getStatus() != BookStatus.PENDING) {
            return;
        }
        book.setStatus(BookStatus.ACTIVE);
        bookRepository.save(book);
    }

    @Override
    public void rejectBook(Long id) {
        Book book = findById(id);
        if (book.getStatus() != BookStatus.PENDING) {
            return;
        }
        book.setStatus(BookStatus.REJECTED);
        bookRepository.save(book);
    }
}
