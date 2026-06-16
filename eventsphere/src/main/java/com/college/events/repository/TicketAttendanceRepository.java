package com.college.events.repository;

import com.college.events.entity.TicketAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketAttendanceRepository extends JpaRepository<TicketAttendance, Long> {
    
    // 1. Single record finder (Clears lines in any older controller files)
    Optional<TicketAttendance> findByEventIdIgnoreCaseAndUserIdIgnoreCase(String eventId, String userId);

    // 2. Collection finder (Used by our robust download flow to prevent blocking loops)
    List<TicketAttendance> findAllByEventIdIgnoreCaseAndUserIdIgnoreCase(String eventId, String userId);
}