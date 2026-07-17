package com.he194346.mvc.online_library_management_system.config;

import com.he194346.mvc.online_library_management_system.entity.Book;
import com.he194346.mvc.online_library_management_system.entity.User;
import com.he194346.mvc.online_library_management_system.enums.BookStatus;
import com.he194346.mvc.online_library_management_system.enums.UserRole;
import com.he194346.mvc.online_library_management_system.enums.UserStatus;
import com.he194346.mvc.online_library_management_system.repository.BookRepository;
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
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args){
//        seedUser("admin@library.com", "Quan Tri Vien", UserRole.ADMIN);
//        seedUser("librarian@library.com", "Thu Thu", UserRole.LIBRARIAN);
//        seedUser("reader@library.com","Doc Gia", UserRole.READER);
//        seedBook("Nhà Giả Kim",
//                "Hành trình theo đuổi ước mơ và khám phá ý nghĩa cuộc sống.", 8);
//        seedBook("Đắc Nhân Tâm",
//                "Những nguyên tắc ứng xử giúp xây dựng các mối quan hệ tốt đẹp.", 10);
//        seedBook("Tuổi Trẻ Đáng Giá Bao Nhiêu",
//                "Những chia sẻ về học tập, trải nghiệm và trưởng thành.", 7);
//        seedBook("Tôi Thấy Hoa Vàng Trên Cỏ Xanh",
//                "Câu chuyện trong trẻo về tuổi thơ, tình thân và quê hương.", 6);
//        seedBook("Dế Mèn Phiêu Lưu Ký",
//                "Cuộc phiêu lưu và hành trình trưởng thành của Dế Mèn.", 9);
//        seedBook("Cho Tôi Xin Một Vé Đi Tuổi Thơ",
//                "Một chuyến trở về thế giới tuổi thơ hồn nhiên và giàu tưởng tượng.", 5);
    }

    private void seedUser(String email, String fullName, UserRole role){
        if(userRepository.existsByEmail(email)){
            return;
        }
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone("0000000000");
        user.setPassword(passwordEncoder.encode("Admin@123"));
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private void seedBook(String title, String description, int totalCopies) {
        if (bookRepository.existsByTitleIgnoreCase(title)) {
            return;
        }

        Book book = new Book();
        book.setTitle(title);
        book.setDescription(description);
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(totalCopies);
        book.setStatus(BookStatus.ACTIVE);
        bookRepository.save(book);
    }
}
