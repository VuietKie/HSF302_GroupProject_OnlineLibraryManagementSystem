package com.he194346.mvc.online_library_management_system.service;

import com.he194346.mvc.online_library_management_system.dto.reservation.ReservationRequestDTO;
import com.he194346.mvc.online_library_management_system.entity.Reservation;

import java.util.List;

public interface ReservationService {
    Long createHolding(String readerEmail, ReservationRequestDTO request);
    Reservation findOwnedReservation(Long reservationId, String readerEmail);
    List<Reservation> findByReader(String readerEmail);
    void submitForApproval(Long reservationId, String readerEmail);
    void expireOverdueHoldings();
}
