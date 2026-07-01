package com.he194346.mvc.online_library_management_system.dto.borrow;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReaderSummaryDTO {
    private Long userId;
    private String fullName;
    private String email;
}
