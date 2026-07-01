package com.he194346.mvc.online_library_management_system.mapper;

import com.he194346.mvc.online_library_management_system.dto.book.ReaderBookResponseDTO;
import com.he194346.mvc.online_library_management_system.entity.Author;
import com.he194346.mvc.online_library_management_system.entity.Book;
import com.he194346.mvc.online_library_management_system.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "authorNames", source = "authors")
    @Mapping(target = "categoryNames", source = "categories")
    ReaderBookResponseDTO toReaderBookResponseDTO(Book book);

    default String toAuthorName(Author author) {
        return author == null ? null : author.getName();
    }

    default String toCategoryName(Category category) {
        return category == null ? null : category.getCategoryName();
    }
}
