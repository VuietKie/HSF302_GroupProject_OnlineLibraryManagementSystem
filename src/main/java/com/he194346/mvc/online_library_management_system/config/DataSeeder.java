package com.he194346.mvc.online_library_management_system.config;

import com.he194346.mvc.online_library_management_system.entity.User;
import com.he194346.mvc.online_library_management_system.enums.UserRole;
import com.he194346.mvc.online_library_management_system.enums.UserStatus;
import com.he194346.mvc.online_library_management_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args){
        seedUser("admin@library.com", "Quan Tri Vien", UserRole.ADMIN);
        seedUser("librarian@library.com", "Thu Thu", UserRole.LIBRARIAN);
        seedUser("user1@gmail.com","Nguoi Doc",UserRole.READER);
    }
    private void seedUser(String email, String fullName, UserRole role){
        if(userRepository.existsByEmail(email)){
            return;
        }
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone("0000000000");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
