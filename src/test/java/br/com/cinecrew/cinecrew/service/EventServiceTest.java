package br.com.cinecrew.cinecrew.service;

import br.com.cinecrew.cinecrew.dto.request.CreateEventRequest;
import br.com.cinecrew.cinecrew.mapper.EventMapper;
import br.com.cinecrew.cinecrew.model.*;
import br.com.cinecrew.cinecrew.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventParticipantRepository eventParticipantRepository;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClubService clubService;

    @Mock
    private EventMapper eventMapper;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService(
                eventRepository,
                eventParticipantRepository,
                clubRepository,
                movieRepository,
                userRepository,
                clubService,
                eventMapper
        );
    }

    @Test
    void deveDividirValorTotalIgualmenteQuandoDivisaoForExata() {
        Long organizerId = 1L;
        Long clubId = 10L;

        Club club = Club.builder().build();
        club.setId(clubId);

        when(clubRepository.findById(clubId)).thenReturn(Optional.of(club));

        when(userRepository.findById(any())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            User user = User.builder().name("User " + id).build();
            user.setId(id);
            return Optional.of(user);
        });

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateEventRequest request = new CreateEventRequest(
                "Duna 2",
                null,
                null,
                OffsetDateTime.now().plusDays(1),
                "Cinemark",
                new BigDecimal("90.00"),
                List.of(2L, 3L)
        );

        eventService.createEvent(organizerId, clubId, request);

        ArgumentCaptor<List<EventParticipant>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventParticipantRepository).saveAll(captor.capture());

        List<EventParticipant> participants = captor.getValue();
        BigDecimal total = participants.stream()
                .map(EventParticipant::getAmountOwed)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(participants).hasSize(3);
        assertThat(total).isEqualByComparingTo(new BigDecimal("90.00"));
    }

    @Test
    void deveAlocarCentavoRestanteAoPrimeiroParticipanteQuandoDivisaoNaoForExata() {
        Long organizerId = 1L;
        Long clubId = 10L;

        Club club = Club.builder().build();
        club.setId(clubId);

        User organizer = User.builder().name("Organizador").build();
        organizer.setId(organizerId);

        when(clubRepository.findById(clubId)).thenReturn(Optional.of(club));
        when(userRepository.findById(any())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            User user = User.builder().name("User " + id).build();
            user.setId(id);
            return Optional.of(user);
        });
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateEventRequest request = new CreateEventRequest(
                "Duna 2",
                null,
                null,
                OffsetDateTime.now().plusDays(1),
                "Cinemark",
                new BigDecimal("100.00"),
                List.of(2L, 3L)
        );

        eventService.createEvent(organizerId, clubId, request);

        ArgumentCaptor<List<EventParticipant>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventParticipantRepository).saveAll(captor.capture());

        List<EventParticipant> participants = captor.getValue();
        BigDecimal total = participants.stream()
                .map(EventParticipant::getAmountOwed)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(total).isEqualByComparingTo(new BigDecimal("100.00"));
    }
}