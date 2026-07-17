package com.he194346.mvc.online_library_management_system.dto.reservation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationItemRequestDTO {

    @NotNull
    private Long bookId;

    @Min(value = 0, message = "Số lượng không được âm")
    private Integer quantity = 0;
}
