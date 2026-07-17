package com.he194346.mvc.online_library_management_system.repository;

import com.he194346.mvc.online_library_management_system.entity.Reservation;
import com.he194346.mvc.online_library_management_system.enums.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            select distinct r from Reservation r
            left join fetch r.books br
            left join fetch br.book
            where r.reservationId = :reservationId and r.reader.email = :email
            """)
    Optional<Reservation> findByReservationIdAndReaderEmail(@Param("reservationId") Long reservationId,
                                                            @Param("email") String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select distinct r from Reservation r
            left join fetch r.books br
            left join fetch br.book
            where r.reservationId = :reservationId and r.reader.email = :email
            """)
    Optional<Reservation> findOwnedForUpdate(@Param("reservationId") Long reservationId,
                                              @Param("email") String email);

    @Query("""
            select distinct r from Reservation r
            left join fetch r.books br
            left join fetch br.book
            where r.reader.email = :email
            order by r.reservedAt desc
            """)
    List<Reservation> findAllByReaderEmailOrderByReservedAtDesc(@Param("email") String email);

    @Query("""
            select distinct r from Reservation r
            left join fetch r.reader
            left join fetch r.books br
            left join fetch br.book
            where r.status = :status
            order by r.submittedAt asc
            """)
    List<Reservation> findAllByStatusOrderBySubmittedAt(@Param("status") ReservationStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select distinct r from Reservation r
            left join fetch r.reader
            left join fetch r.books br
            left join fetch br.book
            where r.reservationId = :reservationId
            """)
    Optional<Reservation> findByIdForLibrarianUpdate(@Param("reservationId") Long reservationId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select distinct r from Reservation r
            left join fetch r.books br
            left join fetch br.book
            where r.status = :status and r.expiredAt <= :now
            """)
    List<Reservation> findExpiredForUpdate(@Param("status") ReservationStatus status,
                                            @Param("now") LocalDateTime now);
}
