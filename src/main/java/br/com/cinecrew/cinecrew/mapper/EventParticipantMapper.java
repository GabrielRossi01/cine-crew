package br.com.cinecrew.cinecrew.mapper;

import br.com.cinecrew.cinecrew.dto.response.EventBalanceResponse;
import br.com.cinecrew.cinecrew.model.EventParticipant;
import org.springframework.stereotype.Component;

@Component
public class EventParticipantMapper {

    public EventBalanceResponse.ParticipantBalance toParticipantBalance(EventParticipant participant) {
        if (participant == null) {
            return null;
        }

        return new EventBalanceResponse.ParticipantBalance(
                participant.getUser().getId(),
                participant.getUser().getName(),
                participant.getAmountOwed(),
                participant.getPaymentStatus(),
                participant.getPaidAt()
        );
    }
}