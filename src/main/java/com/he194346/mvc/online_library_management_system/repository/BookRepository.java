package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.Book;
import com.he194346.mvc.online_library_management_system.enums.BookStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Long> {
    boolean existsByTitleIgnoreCase(String title);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.bookId in :ids order by b.bookId")
    List<Book> findAllByIdForUpdate(@Param("ids") List<Long> ids);

    @Query(
            value = """
                    select distinct b
                    from Book b
                    left join b.authors a
                    left join b.categories c
                    where b.status = :status
                      and (:title is null or lower(b.title) like lower(concat('%', :title, '%')))
                      and (:author is null or lower(a.name) like lower(concat('%', :author, '%')))
                      and (:categoryId is null or c.categoryId = :categoryId)
                    """,
            countQuery = """
                    select count(distinct b)
                    from Book b
                    left join b.authors a
                    left join b.categories c
                    where b.status = :status
                      and (:title is null or lower(b.title) like lower(concat('%', :title, '%')))
                      and (:author is null or lower(a.name) like lower(concat('%', :author, '%')))
                      and (:categoryId is null or c.categoryId = :categoryId)
                    """
    )
    Page<Book> searchActiveBooksForReader(@Param("status") BookStatus status,
                                          @Param("title") String title,
                                          @Param("author") String author,
                                          @Param("categoryId") Long categoryId,
                                          Pageable pageable);
}
