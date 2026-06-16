package com.college.events.repository;

import com.college.events.entity.TicketAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface TicketAttendanceRepository extends JpaRepository<TicketAttendance, Long> {
    
    // FIXED: Added IgnoreCase so it finds matching records regardless of capital letters
    Optional<TicketAttendance> findByEventIdIgnoreCaseAndUserIdIgnoreCase(String eventId, String userId);
    
    List<TicketAttendance> findByUserIdAndAttendedTrue(String userId);
}