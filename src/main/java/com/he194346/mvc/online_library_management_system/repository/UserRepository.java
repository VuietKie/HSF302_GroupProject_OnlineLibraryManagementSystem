package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    User findByEmail(String email);
}
