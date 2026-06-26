package com.he194346.mvc.online_library_management_system.service;

import com.he194346.mvc.online_library_management_system.dto.category.CategoryRequestDTO;
import com.he194346.mvc.online_library_management_system.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    Category findById(Long id);
    void create(CategoryRequestDTO request);
    void update(Long id, CategoryRequestDTO request);
    void delete(Long id);
}
