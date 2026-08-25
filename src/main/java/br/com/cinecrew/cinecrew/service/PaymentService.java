package br.com.cinecrew.cinecrew.service;

import br.com.cinecrew.cinecrew.dto.request.AddParticipantRequest;
import br.com.cinecrew.cinecrew.dto.response.EventBalanceResponse;
import br.com.cinecrew.cinecrew.exception.BusinessRuleException;
import br.com.cinecrew.cinecrew.exception.ForbiddenOperationException;
import br.com.cinecrew.cinecrew.exception.ResourceNotFoundException;
import br.com.cinecrew.cinecrew.mapper.EventParticipantMapper;
import br.com.cinecrew.cinecrew.model.Event;
import br.com.cinecrew.cinecrew.model.EventParticipant;
import br.com.cinecrew.cinecrew.model.User;
import br.com.cinecrew.cinecrew.model.enums.EventStatus;
import br.com.cinecrew.cinecrew.model.enums.PaymentStatus;
import br.com.cinecrew.cinecrew.repository.EventParticipantRepository;
import br.com.cinecrew.cinecrew.repository.EventRepository;
import br.com.cinecrew.cinecrew.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final UserRepository userRepository;
    private final ClubService clubService;
    private final EventParticipantMapper eventParticipantMapper;

    @Transactional
    public void addParticipant(Long authenticatedUserId, Long eventId, AddParticipantRequest request) {
        Event event = findEventById(eventId);

        assertEventOpen(event);
        clubService.assertAdmin(event.getClub().getId(), authenticatedUserId);
        clubService.assertMember(event.getClub().getId(), request.userId());

        if (eventParticipantRepository.existsByEventIdAndUserId(eventId, request.userId())) {
            throw new BusinessRuleException("Este usuário já participa da rachadinha");
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", request.userId()));

        EventParticipant participant = EventParticipant.builder()
                .event(event)
                .user(user)
                .amountOwed(request.amountOwed())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        eventParticipantRepository.save(participant);
    }

    @Transactional
    public void confirmPayment(Long authenticatedUserId, Long eventId, Long participantUserId) {
        Event event = findEventById(eventId);

        assertEventOpen(event);

        boolean isAdmin = clubService.isAdmin(
                event.getClub().getId(),
                authenticatedUserId
        );
        boolean isOwnPayment = authenticatedUserId.equals(participantUserId);

        if (!isAdmin && !isOwnPayment) {
            throw new ForbiddenOperationException("Você só pode confirmar o próprio pagamento");
        }

        EventParticipant participant = eventParticipantRepository
                .findByEventIdAndUserIdForUpdate(eventId, participantUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Participante do evento", participantUserId));

        if (participant.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BusinessRuleException("Este pagamento já foi confirmado");
        }

        participant.setPaymentStatus(PaymentStatus.PAID);
        participant.setPaidAt(Instant.now());
    }

    @Transactional(readOnly = true)
    public EventBalanceResponse getBalance(Long authenticatedUserId, Long eventId) {
        Event event = findEventById(eventId);

        clubService.assertMember(event.getClub().getId(), authenticatedUserId);

        List<EventParticipant> participants = eventParticipantRepository
                .findByEventId(eventId);

        BigDecimal totalPaid = participants.stream()
                .filter(participant -> participant.getPaymentStatus() == PaymentStatus.PAID)
                .map(EventParticipant::getAmountOwed)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPending = participants.stream()
                .filter(participant -> participant.getPaymentStatus() == PaymentStatus.PENDING)
                .map(EventParticipant::getAmountOwed)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<EventBalanceResponse.ParticipantBalance> participantBalances =
                participants.stream()
                        .map(eventParticipantMapper::toParticipantBalance)
                        .toList();

        return new EventBalanceResponse(
                event.getId(),
                event.getTotalAmount(),
                totalPaid,
                totalPending,
                participantBalances
        );
    }

    private void assertEventOpen(Event event) {
        if (event.getStatus() != EventStatus.OPEN) {
            throw new BusinessRuleException("Esta operação só é permitida em eventos abertos");
        }
    }

    private boolean isAdmin(Long clubId, Long userId) {
        try {
            clubService.assertAdmin(clubId, userId);
            return true;
        } catch (ForbiddenOperationException exception) {
            return false;
        }
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventId));
    }
}