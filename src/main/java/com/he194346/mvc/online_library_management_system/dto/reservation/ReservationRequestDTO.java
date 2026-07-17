package com.he194346.mvc.online_library_management_system.dto.reservation;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReservationRequestDTO {

    @Valid
    private List<ReservationItemRequestDTO> items = new ArrayList<>();
}
