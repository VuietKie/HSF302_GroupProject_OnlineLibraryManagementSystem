package com.he194346.mvc.online_library_management_system.dto.borrow;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BorrowRequestDTO {

    @Valid
    @NotEmpty(message = "Yêu cầu mượn phải có ít nhất một sách")
    private List<BorrowRequestItemDTO> items = new ArrayList<>();
}
