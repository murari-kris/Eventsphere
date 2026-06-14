package com.college.events.repository;



import com.college.events.entity.TicketAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface TicketAttendanceRepository extends JpaRepository<TicketAttendance, Long> {
    
    // Finds a unique attendance record matching the student and event
    Optional<TicketAttendance> findByEventIdAndUserId(String eventId, String userId);
    
    // Finds all events a specific student has attended (for their dashboard)
    List<TicketAttendance> findByUserIdAndAttendedTrue(String userId);
}