package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.dto.author.AuthorRequestDTO;
import com.he194346.mvc.online_library_management_system.entity.Author;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.repository.AuthorRepository;
import com.he194346.mvc.online_library_management_system.service.AuthorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Author findById(Long id) {
        return authorRepository.findById(id).orElseThrow(()-> new CustomException(ErrorCode.AUTHOR_NOT_FOUND,"Không tìm thấy tác giả"));
    }

    @Override
    public void create(AuthorRequestDTO authorRequestDTO) {
        if(authorRepository.existsByName(authorRequestDTO.getName())){
            throw new CustomException(ErrorCode.AUTHOR_ALREADY_EXISTS,"Tác giả đã tồn tại");
        }
        Author author = new Author();
        author.setName(authorRequestDTO.getName());
        authorRepository.save(author);
    }

    @Override
    public void update(Long id, AuthorRequestDTO authorRequestDTO) {
        Author author = findById(id);
        author.setName(authorRequestDTO.getName());
        authorRepository.save(author);
    }

    @Override
    public void delete(Long id) {
        Author author = findById(id);
        authorRepository.delete(author);
    }
}
