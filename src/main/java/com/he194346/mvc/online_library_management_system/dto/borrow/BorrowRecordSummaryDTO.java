package com.he194346.mvc.online_library_management_system.dto.borrow;

import com.he194346.mvc.online_library_management_system.enums.BorrowStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BorrowRecordSummaryDTO {
    private Long borrowRecordId;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private BorrowStatus status;
}
