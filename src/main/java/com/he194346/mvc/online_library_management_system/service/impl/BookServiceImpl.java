package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.dto.book.BookRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.book.ReaderBookResponseDTO;
import com.he194346.mvc.online_library_management_system.entity.Author;
import com.he194346.mvc.online_library_management_system.entity.Book;
import com.he194346.mvc.online_library_management_system.entity.Category;
import com.he194346.mvc.online_library_management_system.enums.BookStatus;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.mapper.BookMapper;
import com.he194346.mvc.online_library_management_system.repository.AuthorRepository;
import com.he194346.mvc.online_library_management_system.repository.BookRepository;
import com.he194346.mvc.online_library_management_system.repository.CategoryRepository;
import com.he194346.mvc.online_library_management_system.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {
    private static final int READER_BOOK_PAGE_SIZE = 6;

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(()-> new CustomException(ErrorCode.BOOK_NOT_FOUND,"Không tìm thấy sách"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReaderBookResponseDTO> searchActiveBooksForReader(String title, String author, Long categoryId, int page) {
        List<Book> filteredBooks = bookRepository.findAll().stream()
                .filter(book -> book.getStatus() == BookStatus.ACTIVE)
                .filter(book -> matchesTitle(book, title))
                .filter(book -> matchesAuthor(book, author))
                .filter(book -> matchesCategory(book, categoryId))
                .sorted(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();

        List<ReaderBookResponseDTO> mappedBooks = filteredBooks.stream()
                .map(bookMapper::toReaderBookResponseDTO)
                .collect(Collectors.toList());

        int safePage = Math.max(page, 0);
        int start = Math.min(safePage * READER_BOOK_PAGE_SIZE, mappedBooks.size());
        int end = Math.min(start + READER_BOOK_PAGE_SIZE, mappedBooks.size());
        List<ReaderBookResponseDTO> pageContent = new ArrayList<>(mappedBooks.subList(start, end));

        return new PageImpl<>(pageContent, PageRequest.of(safePage, READER_BOOK_PAGE_SIZE), mappedBooks.size());
    }

    @Override
    @Transactional(readOnly = true)
    public ReaderBookResponseDTO findActiveBookDetailForReader(Long id) {
        Book book = findById(id);
        if (book.getStatus() != BookStatus.ACTIVE) {
            throw new CustomException(ErrorCode.BOOK_NOT_FOUND, "Không tìm thấy sách");
        }
        return bookMapper.toReaderBookResponseDTO(book);
    }

    @Override
    public boolean hasReaderBookFilters(String title, String author, Long categoryId) {
        return hasText(title) || hasText(author) || categoryId != null;
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
        if (book.getStatus() == BookStatus.INACTIVE) {
            bookRequestDTO.setStatus(book.getStatus());
        }
    }

    private boolean matchesTitle(Book book, String title) {
        if (!hasText(title)) {
            return true;
        }
        return safeLower(book.getTitle()).contains(safeLower(title));
    }

    private boolean matchesAuthor(Book book, String author) {
        if (!hasText(author)) {
            return true;
        }
        String normalizedAuthor = safeLower(author);
        return book.getAuthors().stream()
                .map(Author::getName)
                .map(this::safeLower)
                .anyMatch(name -> name.contains(normalizedAuthor));
    }

    private boolean matchesCategory(Book book, Long categoryId) {
        if (categoryId == null) {
            return true;
        }
        return book.getCategories().stream().anyMatch(category -> categoryId.equals(category.getCategoryId()));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    // UC10: Approve New Books
    @Override
    public List<Book> getPendingBooks() {
        return bookRepository.findByStatus(BookStatus.INACTIVE);
    }

    @Override
    public void approveBook(Long id) {
        Book book = findById(id);
        if (book.getStatus() != BookStatus.INACTIVE) {
            return;
        }
        book.setStatus(BookStatus.ACTIVE);
        bookRepository.save(book);
    }

    @Override
    public void rejectBook(Long id) {
        Book book = findById(id);
        if (book.getStatus() != BookStatus.INACTIVE) {
            return;
        }
        book.setStatus(BookStatus.INACTIVE);
        bookRepository.save(book);
    }
}
