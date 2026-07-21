package com.he194346.mvc.online_library_management_system.controller;

import com.he194346.mvc.online_library_management_system.entity.User;
import com.he194346.mvc.online_library_management_system.enums.ErrorCode;
import com.he194346.mvc.online_library_management_system.exception.CustomException;
import com.he194346.mvc.online_library_management_system.service.BorrowRecordService;
import com.he194346.mvc.online_library_management_system.service.BorrowService;
import com.he194346.mvc.online_library_management_system.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reader/history")
@AllArgsConstructor
public class ReaderBorrowHistoryController {

    private final BorrowService borrowService;
    private final BorrowRecordService borrowRecordService;
    private final UserService userService;

    // [READ] Hiển thị lịch sử mượn sách của người dùng đang đăng nhập
    @GetMapping
    public String viewBorrowHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = findCurrentUser(userDetails);
        // Lấy lịch sử mượn sách theo ID người dùng và gửi sang giao diện
        model.addAttribute("borrowHistory", borrowService.getBorrowHistoryByReader(currentUser.getUserId()));
        return "reader/borrow-history";
    }

    // [UPDATE] Xử lý yêu cầu trả sách của người dùng
    @PostMapping("/return/{borrowRecordId}")
    public String returnBorrowedBooks(@PathVariable Long borrowRecordId,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {
        User currentUser = findCurrentUser(userDetails);
        boolean ownedRecord = borrowService.existsOwnedBorrowRecord(borrowRecordId, currentUser.getUserId());
        if (!ownedRecord) {
            throw new CustomException(ErrorCode.BORROW_RECORD_NOT_FOUND, "Không tìm thấy phiếu mượn của bạn");
        }
        // Thực hiện xử lý trả toàn bộ sách trong phiếu mượn
        borrowRecordService.returnBorrowRecord(borrowRecordId);
        redirectAttributes.addFlashAttribute("successMessage", "Trả sách thành công.");
        return "redirect:/reader/history";
    }

    private User findCurrentUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername());
    }
}
