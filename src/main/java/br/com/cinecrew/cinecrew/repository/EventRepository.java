package br.com.cinecrew.cinecrew.repository;

import br.com.cinecrew.cinecrew.model.Event;
import br.com.cinecrew.cinecrew.model.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByClubIdOrderBySessionDateTimeDesc(Long clubId);

    List<Event> findByClubIdAndStatus(Long clubId, EventStatus status);

    List<Event> findByOrganizerId(Long organizerId);
}
