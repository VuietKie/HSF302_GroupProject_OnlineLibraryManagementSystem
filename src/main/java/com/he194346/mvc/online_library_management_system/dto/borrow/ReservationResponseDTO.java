package com.he194346.mvc.online_library_management_system.dto.borrow;

import com.he194346.mvc.online_library_management_system.enums.ReservationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class ReservationResponseDTO {
    private Long reservationId;
    private ReaderSummaryDTO reader;
    private LocalDateTime reservedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime submittedAt;
    private ReservationStatus status;
    private Set<ReservationItemResponseDTO> books = new LinkedHashSet<>();
    private BorrowRecordSummaryDTO borrowRecord;
}
