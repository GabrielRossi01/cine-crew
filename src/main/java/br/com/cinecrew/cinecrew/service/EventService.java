package br.com.cinecrew.cinecrew.service;

import br.com.cinecrew.cinecrew.dto.request.CreateEventRequest;
import br.com.cinecrew.cinecrew.dto.request.UpdateEventStatusRequest;
import br.com.cinecrew.cinecrew.dto.response.EventResponse;
import br.com.cinecrew.cinecrew.exception.BusinessRuleException;
import br.com.cinecrew.cinecrew.exception.ResourceNotFoundException;
import br.com.cinecrew.cinecrew.mapper.EventMapper;
import br.com.cinecrew.cinecrew.model.Club;
import br.com.cinecrew.cinecrew.model.Event;
import br.com.cinecrew.cinecrew.model.EventParticipant;
import br.com.cinecrew.cinecrew.model.Movie;
import br.com.cinecrew.cinecrew.model.User;
import br.com.cinecrew.cinecrew.model.enums.EventStatus;
import br.com.cinecrew.cinecrew.model.enums.PaymentStatus;
import br.com.cinecrew.cinecrew.repository.ClubRepository;
import br.com.cinecrew.cinecrew.repository.EventParticipantRepository;
import br.com.cinecrew.cinecrew.repository.EventRepository;
import br.com.cinecrew.cinecrew.repository.MovieRepository;
import br.com.cinecrew.cinecrew.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final ClubRepository clubRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final ClubService clubService;
    private final EventMapper eventMapper;

    @Transactional
    public EventResponse createEvent(Long authenticatedUserId, Long clubId, CreateEventRequest request) {
        clubService.assertMember(clubId, authenticatedUserId);

        Club club = findClubById(clubId);
        User organizer = findUserById(authenticatedUserId);

        List<Long> participantIds = normalizeParticipantIds(
                request.participantUserIds(),
                authenticatedUserId
        );

        validateParticipantsBelongToClub(clubId, participantIds);

        Movie movie = findOrCreateMovie(request);

        Event event = Event.builder()
                .club(club)
                .movie(movie)
                .organizer(organizer)
                .sessionDateTime(request.sessionDateTime())
                .cinemaName(request.cinemaName().trim())
                .totalAmount(request.totalAmount())
                .status(EventStatus.OPEN)
                .build();

        Event savedEvent = eventRepository.save(event);

        List<EventParticipant> participants = buildParticipants(
                savedEvent,
                participantIds,
                request.totalAmount()
        );

        eventParticipantRepository.saveAll(participants);

        return eventMapper.toResponse(savedEvent);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByClub(Long authenticatedUserId, Long clubId) {
        clubService.assertMember(clubId, authenticatedUserId);

        return eventRepository.findByClubIdOrderBySessionDateTimeDesc(clubId)
                .stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long authenticatedUserId, Long eventId) {
        Event event = findEventById(eventId);

        clubService.assertMember(event.getClub().getId(), authenticatedUserId);

        return eventMapper.toResponse(event);
    }

    @Transactional
    public EventResponse updateStatus(Long authenticatedUserId, Long eventId, UpdateEventStatusRequest request) {
        Event event = findEventById(eventId);

        clubService.assertAdmin(event.getClub().getId(), authenticatedUserId);

        validateStatusTransition(event, request.status());

        event.setStatus(request.status());

        return eventMapper.toResponse(event);
    }

    private List<Long> normalizeParticipantIds(List<Long> requestedParticipantIds, Long organizerId) {
        if (requestedParticipantIds == null || requestedParticipantIds.isEmpty()) {
            throw new BusinessRuleException("O evento deve possuir pelo menos um participante");
        }

        Set<Long> uniqueParticipantIds = new LinkedHashSet<>(requestedParticipantIds);
        uniqueParticipantIds.add(organizerId);

        return new ArrayList<>(uniqueParticipantIds);
    }

    private void validateParticipantsBelongToClub(Long clubId, List<Long> participantIds) {
        for (Long participantId : participantIds) {
            clubService.assertMember(clubId, participantId);
        }
    }

    private Movie findOrCreateMovie(CreateEventRequest request) {
        if (request.tmdbId() != null) {
            return movieRepository.findByTmdbId(request.tmdbId())
                    .orElseGet(() -> movieRepository.save(
                            Movie.builder()
                                    .title(request.movieTitle().trim())
                                    .tmdbId(request.tmdbId())
                                    .posterUrl(request.posterUrl())
                                    .build()
                    ));
        }

        return movieRepository.save(
                Movie.builder()
                        .title(request.movieTitle().trim())
                        .posterUrl(request.posterUrl())
                        .build()
        );
    }

    private List<EventParticipant> buildParticipants(Event event, List<Long> participantIds, BigDecimal totalAmount) {
        BigDecimal baseShare = totalAmount.divide(
                BigDecimal.valueOf(participantIds.size()),
                2,
                RoundingMode.DOWN
        );

        BigDecimal allocatedAmount = baseShare.multiply(
                BigDecimal.valueOf(participantIds.size())
        );

        BigDecimal remainder = totalAmount.subtract(allocatedAmount);

        List<EventParticipant> participants = new ArrayList<>();

        for (int index = 0; index < participantIds.size(); index++) {
            Long participantId = participantIds.get(index);

            BigDecimal amountOwed = index == 0
                    ? baseShare.add(remainder)
                    : baseShare;

            User participantUser = findUserById(participantId);

            participants.add(EventParticipant.builder()
                    .event(event)
                    .user(participantUser)
                    .amountOwed(amountOwed)
                    .paymentStatus(PaymentStatus.PENDING)
                    .build());
        }

        return participants;
    }

    private void validateStatusTransition(Event event, EventStatus targetStatus) {
        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new BusinessRuleException("Um evento cancelado não pode ter seu status alterado");
        }

        if (event.getStatus() == targetStatus) {
            throw new BusinessRuleException("O evento já possui o status informado");
        }

        if (targetStatus == EventStatus.SETTLED) {
            long pendingPayments = eventParticipantRepository
                    .countByEventIdAndPaymentStatus(event.getId(), PaymentStatus.PENDING);

            if (pendingPayments > 0) {
                throw new BusinessRuleException("Não é possível encerrar o evento enquanto existirem pagamentos pendentes");
            }
        }
    }

    private Club findClubById(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Clube", clubId));
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventId));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", userId));
    }
}