package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByCategoryName(String categoryName);
}
