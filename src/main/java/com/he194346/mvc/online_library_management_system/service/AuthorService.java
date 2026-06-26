package com.he194346.mvc.online_library_management_system.service;

import com.he194346.mvc.online_library_management_system.dto.author.AuthorRequestDTO;
import com.he194346.mvc.online_library_management_system.entity.Author;

import java.util.List;

public interface AuthorService {
    List<Author> findAll();
    Author findById(Long id);
    void create(AuthorRequestDTO authorRequestDTO);
    void update(Long id, AuthorRequestDTO authorRequestDTO);
    void delete(Long id);
}
