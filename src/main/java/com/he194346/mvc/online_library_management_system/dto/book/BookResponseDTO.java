package com.he194346.mvc.online_library_management_system.dto.book;

import com.he194346.mvc.online_library_management_system.dto.author.AuthorResponseDTO;
import com.he194346.mvc.online_library_management_system.dto.category.CategoryResponseDTO;
import com.he194346.mvc.online_library_management_system.enums.BookStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class BookResponseDTO {
    private Long bookId;
    private String title;
    private String description;
    private String coverImg;
    private Integer totalCopies;
    private Integer availableCopies;
    private BookStatus status;
    private Set<AuthorResponseDTO> authors = new LinkedHashSet<>();
    private Set<CategoryResponseDTO> categories = new LinkedHashSet<>();
}
