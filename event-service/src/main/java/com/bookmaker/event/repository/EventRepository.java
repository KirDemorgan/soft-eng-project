package com.bookmaker.event.repository;

import com.bookmaker.event.model.Event;
import com.bookmaker.event.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    List<Event> findByStatus(EventStatus status);
    
    @Query("SELECT e FROM Event e WHERE e.startTime > :now AND e.startTime < :endTime AND e.status = :status")
    List<Event> findUpcomingEvents(@Param("now") LocalDateTime now, 
                                  @Param("endTime") LocalDateTime endTime, 
                                  @Param("status") EventStatus status);
    
    @Query("SELECT e FROM Event e WHERE e.startTime < :now AND e.status = :status")
    List<Event> findEventsToStart(@Param("now") LocalDateTime now, 
                                 @Param("status") EventStatus status);
}