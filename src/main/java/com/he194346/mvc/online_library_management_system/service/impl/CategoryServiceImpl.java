package com.he194346.mvc.online_library_management_system.service.impl;

import com.he194346.mvc.online_library_management_system.dto.category.CategoryRequestDTO;
import com.he194346.mvc.online_library_management_system.entity.Category;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.repository.CategoryRepository;
import com.he194346.mvc.online_library_management_system.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(()-> new CustomException(ErrorCode.CATEGORY_NOT_FOUND,"Không tìm thấy thể loại"));
    }

    @Override
    public void create(CategoryRequestDTO request) {
        //Chặn trùng tên
        if (categoryRepository.existsByCategoryName(request.getCategoryName())){
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS, "Tên thể loại đã tồn tại!!!!");
        }
        Category category= new Category();
        category.setCategoryName(request.getCategoryName());
        categoryRepository.save(category);
    }

    @Override
    public void update(Long id, CategoryRequestDTO request) {
        Category category= findById(id);
        category.setCategoryName(request.getCategoryName());
        categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        Category category= findById(id);
        categoryRepository.delete(category);
    }
}