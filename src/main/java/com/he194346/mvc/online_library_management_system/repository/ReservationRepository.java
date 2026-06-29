package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Lấy danh sách phiếu giữ chỗ của 1 độc giả, mới nhất lên đầu
    List<Reservation> findByReader_UserIdOrderByReservedAtDesc(Long userId);
}
