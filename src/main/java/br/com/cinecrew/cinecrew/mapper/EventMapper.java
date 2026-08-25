package br.com.cinecrew.cinecrew.mapper;

import br.com.cinecrew.cinecrew.dto.response.EventResponse;
import br.com.cinecrew.cinecrew.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final UserMapper userMapper;

    public EventResponse toResponse(Event event) {
        if (event == null) {
            return null;
        }

        return new EventResponse(
                event.getId(),
                event.getMovie().getTitle(),
                event.getMovie().getPosterUrl(),
                event.getSessionDateTime(),
                event.getCinemaName(),
                event.getTotalAmount(),
                event.getStatus(),
                userMapper.toSummaryResponse(event.getOrganizer())
        );
    }
}