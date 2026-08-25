package br.com.cinecrew.cinecrew.service;

import br.com.cinecrew.cinecrew.dto.response.InviteResponse;
import br.com.cinecrew.cinecrew.exception.BusinessRuleException;
import br.com.cinecrew.cinecrew.exception.InviteExpiredException;
import br.com.cinecrew.cinecrew.exception.ResourceNotFoundException;
import br.com.cinecrew.cinecrew.model.Club;
import br.com.cinecrew.cinecrew.model.ClubMember;
import br.com.cinecrew.cinecrew.model.User;
import br.com.cinecrew.cinecrew.model.enums.ClubMemberRole;
import br.com.cinecrew.cinecrew.repository.ClubMemberRepository;
import br.com.cinecrew.cinecrew.repository.ClubRepository;
import br.com.cinecrew.cinecrew.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class InviteService {

    private static final String INVITE_KEY_PREFIX = "cinecrew:invite:";

    private final StringRedisTemplate redisTemplate;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final UserRepository userRepository;
    private final ClubService clubService;

    @Value("${app.invite.ttl-minutes:30}")
    private long inviteTtlMinutes;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Transactional(readOnly = true)
    public InviteResponse createInvite(Long authenticatedUserId, Long clubId) {
        clubService.assertAdmin(clubId, authenticatedUserId);

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Clube", clubId));

        String inviteCode = generateSecureInviteCode();
        Duration ttl = Duration.ofMinutes(inviteTtlMinutes);

        redisTemplate.opsForValue().set(
                buildRedisKey(inviteCode),
                club.getId().toString(),
                ttl
        );

        String inviteUrl = frontendUrl + "/clubs/join/" + inviteCode;

        return new InviteResponse(
                inviteCode,
                inviteUrl,
                Instant.now().plus(ttl)
        );
    }

    @Transactional
    public void joinClub(Long authenticatedUserId, String inviteCode) {
        String clubIdValue = redisTemplate.opsForValue()
                .get(buildRedisKey(inviteCode));

        if (clubIdValue == null) {
            throw new InviteExpiredException();
        }

        Long clubId;
        try {
            clubId = Long.valueOf(clubIdValue);
        } catch (NumberFormatException exception) {
            throw new InviteExpiredException();
        }

        if (clubMemberRepository.existsByClubIdAndUserId(clubId, authenticatedUserId)) {
            throw new BusinessRuleException(
                    "Você já é membro deste clube"
            );
        }

        Club club = clubRepository.findById(clubId)
                .orElseThrow(InviteExpiredException::new);

        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário",
                        authenticatedUserId
                ));

        ClubMember membership = ClubMember.builder()
                .club(club)
                .user(user)
                .role(ClubMemberRole.MEMBER)
                .clubScore(0)
                .build();

        clubMemberRepository.save(membership);

        redisTemplate.delete(buildRedisKey(inviteCode));
    }

    private String generateSecureInviteCode() {
        byte[] randomBytes = new byte[24];
        new SecureRandom().nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    private String buildRedisKey(String inviteCode) {
        return INVITE_KEY_PREFIX + inviteCode;
    }
}