package com.he194346.mvc.online_library_management_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationExpirationScheduler {

    private final ReservationService reservationService;

    @Scheduled(fixedDelayString = "${library.reservation.expiration-check-ms:60000}")
    public void expireOverdueReservations() {
        reservationService.expireOverdueHoldings();
    }
}
