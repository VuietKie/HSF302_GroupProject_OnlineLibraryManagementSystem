package com.he194346.mvc.online_library_management_system.config;

import com.he194346.mvc.online_library_management_system.entity.BorrowRecord;
import com.he194346.mvc.online_library_management_system.entity.User;
import com.he194346.mvc.online_library_management_system.enums.BorrowStatus;
import com.he194346.mvc.online_library_management_system.enums.FineStatus;
import com.he194346.mvc.online_library_management_system.enums.UserRole;
import com.he194346.mvc.online_library_management_system.enums.UserStatus;
import com.he194346.mvc.online_library_management_system.repository.BorrowRecordRepository;
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
    private final BorrowRecordRepository borrowRecordRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args){
        seedUser("admin@library.com", "Quan Tri Vien", UserRole.ADMIN);
        seedUser("librarian@library.com", "Thu Thu", UserRole.LIBRARIAN);
        seedUser("reader@library.com", "Doc Gia", UserRole.READER);
        //gieo dữ liệu mượn mẫu để trang thống kê (UC12) có số liệu hiển thị
        seedBorrowRecords();
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

    //chỉ gieo 1 lần (khi bảng trống); rải mốc mượn qua 3 tháng với trạng thái đa dạng
    private void seedBorrowRecords(){
        if(borrowRecordRepository.count() != 0){
            return;
        }
        User reader = userRepository.findByEmail("reader@library.com");
        User librarian = userRepository.findByEmail("librarian@library.com");
        saveBorrow(reader, librarian, LocalDateTime.now().minusMonths(0), BorrowStatus.BORROWED);
        saveBorrow(reader, librarian, LocalDateTime.now().minusMonths(0), BorrowStatus.RETURNED);
        saveBorrow(reader, librarian, LocalDateTime.now().minusMonths(1), BorrowStatus.BORROWED);
        saveBorrow(reader, librarian, LocalDateTime.now().minusMonths(1), BorrowStatus.RETURNED);
        saveBorrow(reader, librarian, LocalDateTime.now().minusMonths(2), BorrowStatus.OVERDUE);
        saveBorrow(reader, librarian, LocalDateTime.now().minusMonths(2), BorrowStatus.RETURNED);
    }

    private void saveBorrow(User reader, User librarian, LocalDateTime borrowDate, BorrowStatus status){
        BorrowRecord record = new BorrowRecord();
        record.setReader(reader);
        record.setLibrarian(librarian);
        record.setBorrowDate(borrowDate);
        record.setDueDate(borrowDate.plusDays(14));//hạn trả = ngày mượn + 14 ngày
        record.setStatus(status);
        if(status == BorrowStatus.RETURNED){
            record.setReturnDate(borrowDate.plusDays(10));//đã trả trước hạn
        }
        if(status == BorrowStatus.OVERDUE){
            record.setOverdueDate(borrowDate.plusDays(14));
            record.setFineAmount(20000.0);//phạt quá hạn, chưa thanh toán
            record.setFineStatus(FineStatus.UNPAID);
        }
        borrowRecordRepository.save(record);
    }
}
