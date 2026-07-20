package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.dto.book.BookRequestDTO;
import com.he194346.mvc.online_library_management_system.dto.book.ReaderBookResponseDTO;
import com.he194346.mvc.online_library_management_system.entity.Author;
import com.he194346.mvc.online_library_management_system.entity.Book;
import com.he194346.mvc.online_library_management_system.entity.Category;
import com.he194346.mvc.online_library_management_system.entity.User;
import com.he194346.mvc.online_library_management_system.enums.BookStatus;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.mapper.BookMapper;
import com.he194346.mvc.online_library_management_system.repository.AuthorRepository;
import com.he194346.mvc.online_library_management_system.repository.BookRepository;
import com.he194346.mvc.online_library_management_system.repository.CategoryRepository;
import com.he194346.mvc.online_library_management_system.repository.UserRepository;
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
    private final UserRepository userRepository;
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
    public void create(BookRequestDTO bookRequestDTO, String actorEmail) {
        Book book = new Book();
        applyData(book,bookRequestDTO);
        book.setAvailableCopies(bookRequestDTO.getTotalCopies());
        // Sách mới luôn ở trạng thái chờ duyệt (INACTIVE), không tin trạng thái FE gửi lên.
        // Hiện tại chỉ đổi được sang ACTIVE qua form Sửa sách (Admin); UC10 (approve/reject
        // riêng cho Librarian) sẽ thay thế bước này sau khi merge.
        book.setStatus(BookStatus.INACTIVE);
        // Ghi nhận admin đã thêm sách này, dùng để chặn chính admin đó tự sửa/duyệt sau này.
        book.setAddedBy(findAdmin(actorEmail));
        bookRepository.save(book);
    }

    @Override
    public void update(Long id, BookRequestDTO bookRequestDTO, String actorEmail) {
        Book book = findById(id);
        validateEditable(book, actorEmail);
        int oldTotal = book.getTotalCopies() == null ? 0 : book.getTotalCopies();
        int oldAvailable = book.getAvailableCopies() == null ? 0 : book.getAvailableCopies();
        int unavailableCopies = Math.max(0, oldTotal - oldAvailable);
        if (bookRequestDTO.getTotalCopies() < unavailableCopies) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION,
                    "Tổng số bản không thể nhỏ hơn " + unavailableCopies
                            + " bản đang được giữ hoặc đang mượn");
        }
        applyData(book,bookRequestDTO);
        book.setAvailableCopies(bookRequestDTO.getTotalCopies() - unavailableCopies);
        bookRepository.save(book);
    }

    @Override
    public Book findEditable(Long id, String actorEmail) {
        Book book = findById(id);
        validateEditable(book, actorEmail);
        return book;
    }

    @Override
    public void delete(Long id) {
        Book book = findById(id);
        bookRepository.delete(book);
    }

    // Admin đã thêm sách (added_by) không được tự sửa/duyệt chính sách của mình;
    // sách chưa có added_by (dữ liệu cũ trước khi có cột này) thì admin nào cũng sửa được.
    private void validateEditable(Book book, String actorEmail) {
        User addedBy = book.getAddedBy();
        if (addedBy != null && addedBy.getEmail().equalsIgnoreCase(actorEmail)) {
            throw new CustomException(ErrorCode.BOOK_SELF_EDIT_FORBIDDEN,
                    "Bạn là người đã thêm sách này, cần admin khác sửa/duyệt");
        }
    }

    private User findAdmin(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy admin");
        }
        return user;
    }

    private void applyData(Book book, BookRequestDTO bookRequestDTO) {
        book.setTitle(bookRequestDTO.getTitle());
        book.setDescription(bookRequestDTO.getDescription());
        book.setStatus(bookRequestDTO.getStatus());
        book.setTotalCopies(bookRequestDTO.getTotalCopies());
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
