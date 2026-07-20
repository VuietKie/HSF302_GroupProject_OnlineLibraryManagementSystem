package com.he194346.mvc.online_library_management_system.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopBorrowedBookDTO {
    private final String title;
    private final long borrowCount;
}
