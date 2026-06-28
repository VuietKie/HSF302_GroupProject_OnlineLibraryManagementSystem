package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    boolean existsByName(String name);
}
