package com.he194346.mvc.online_library_management_system.dto.borrow;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BorrowRequestItemDTO {

    @NotNull(message = "Vui lòng chọn sách")
    private Long bookId;

    @NotNull(message = "Vui lòng nhập số lượng")
    @Min(value = 1, message = "Số lượng mượn phải ít nhất là 1")
    private Integer quantity = 1;
}
