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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {
    private static final int READER_BOOK_PAGE_SIZE = 10;

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
        return bookRepository.findById(id).orElseThrow(() ->
                new CustomException(ErrorCode.BOOK_NOT_FOUND, "Không tìm thấy sách"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReaderBookResponseDTO> searchActiveBooksForReader(String title, String author, Long categoryId, int page) {
        int pageIndex = Math.max(page, 0);

        return bookRepository.searchActiveBooksForReader(
                BookStatus.ACTIVE,
                normalizeSearchValue(title),
                normalizeSearchValue(author),
                categoryId,
                PageRequest.of(pageIndex, READER_BOOK_PAGE_SIZE, Sort.by("title").ascending())
        ).map(bookMapper::toReaderBookResponseDTO);
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
        return normalizeSearchValue(title) != null
                || normalizeSearchValue(author) != null
                || categoryId != null;
    }

    @Override
    public void create(BookRequestDTO bookRequestDTO) {
        Book book = new Book();
        applyData(book, bookRequestDTO);
        bookRepository.save(book);
    }

    @Override
    public void update(Long id, BookRequestDTO bookRequestDTO) {
        Book book = findById(id);
        applyData(book, bookRequestDTO);
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

    private String normalizeSearchValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
