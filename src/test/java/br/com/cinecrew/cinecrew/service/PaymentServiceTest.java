package br.com.cinecrew.cinecrew.service;

import br.com.cinecrew.cinecrew.exception.BusinessRuleException;
import br.com.cinecrew.cinecrew.mapper.EventParticipantMapper;
import br.com.cinecrew.cinecrew.model.Event;
import br.com.cinecrew.cinecrew.model.EventParticipant;
import br.com.cinecrew.cinecrew.model.enums.EventStatus;
import br.com.cinecrew.cinecrew.model.enums.PaymentStatus;
import br.com.cinecrew.cinecrew.repository.EventParticipantRepository;
import br.com.cinecrew.cinecrew.repository.EventRepository;
import br.com.cinecrew.cinecrew.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventParticipantRepository eventParticipantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClubService clubService;

    @Mock
    private EventParticipantMapper eventParticipantMapper;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(
                eventRepository,
                eventParticipantRepository,
                userRepository,
                clubService,
                eventParticipantMapper
        );
    }

    @Test
    void deveLancarExcecaoAoConfirmarPagamentoJaConfirmado() {
        Long eventId = 1L;
        Long participantUserId = 2L;
        Long authenticatedUserId = 2L;

        var club = br.com.cinecrew.cinecrew.model.Club.builder().build();
        club.setId(10L);

        Event event = Event.builder()
                .club(club)
                .status(EventStatus.OPEN)
                .build();
        event.setId(eventId);

        EventParticipant participant = EventParticipant.builder()
                .amountOwed(new BigDecimal("30.00"))
                .paymentStatus(PaymentStatus.PAID)
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        lenient().when(clubService.isAdmin(club.getId(), authenticatedUserId)).thenReturn(false);
        when(eventParticipantRepository.findByEventIdAndUserIdForUpdate(eventId, participantUserId))
                .thenReturn(Optional.of(participant));

        assertThatThrownBy(() ->
                paymentService.confirmPayment(authenticatedUserId, eventId, participantUserId)
        ).isInstanceOf(BusinessRuleException.class);
    }
}