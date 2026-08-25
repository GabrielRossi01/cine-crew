package br.com.cinecrew.cinecrew.service;

import br.com.cinecrew.cinecrew.dto.request.CreateClubRequest;
import br.com.cinecrew.cinecrew.dto.response.ClubMemberResponse;
import br.com.cinecrew.cinecrew.dto.response.ClubResponse;
import br.com.cinecrew.cinecrew.exception.BusinessRuleException;
import br.com.cinecrew.cinecrew.exception.ForbiddenOperationException;
import br.com.cinecrew.cinecrew.exception.ResourceNotFoundException;
import br.com.cinecrew.cinecrew.mapper.ClubMapper;
import br.com.cinecrew.cinecrew.mapper.ClubMemberMapper;
import br.com.cinecrew.cinecrew.model.Club;
import br.com.cinecrew.cinecrew.model.ClubMember;
import br.com.cinecrew.cinecrew.model.User;
import br.com.cinecrew.cinecrew.model.enums.ClubMemberRole;
import br.com.cinecrew.cinecrew.repository.ClubMemberRepository;
import br.com.cinecrew.cinecrew.repository.ClubRepository;
import br.com.cinecrew.cinecrew.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final UserRepository userRepository;
    private final ClubMapper clubMapper;
    private final ClubMemberMapper clubMemberMapper;

    @Transactional
    public ClubResponse createClub(Long authenticatedUserId, CreateClubRequest request) {
        User owner = findUserById(authenticatedUserId);

        Club club = Club.builder()
                .name(request.name().trim())
                .description(normalizeDescription(request.description()))
                .owner(owner)
                .build();

        Club savedClub = clubRepository.save(club);

        ClubMember ownerMembership = ClubMember.builder()
                .club(savedClub)
                .user(owner)
                .role(ClubMemberRole.ADMIN)
                .clubScore(0)
                .build();

        clubMemberRepository.save(ownerMembership);

        return clubMapper.toResponse(savedClub);
    }

    @Transactional(readOnly = true)
    public List<ClubResponse> getMyClubs(Long authenticatedUserId) {
        return clubMemberRepository.findByUserId(authenticatedUserId)
                .stream()
                .map(ClubMember::getClub)
                .map(clubMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClubResponse getClubById(Long authenticatedUserId, Long clubId) {
        Club club = findClubById(clubId);
        assertMember(clubId, authenticatedUserId);

        return clubMapper.toResponse(club);
    }

    @Transactional(readOnly = true)
    public List<ClubMemberResponse> getMembers(Long authenticatedUserId, Long clubId) {
        assertMember(clubId, authenticatedUserId);

        return clubMemberRepository.findByClubId(clubId)
                .stream()
                .map(clubMemberMapper::toResponse)
                .toList();
    }

    @Transactional
    public void removeMember(Long authenticatedUserId, Long clubId, Long memberUserId) {
        assertAdmin(clubId, authenticatedUserId);

        Club club = findClubById(clubId);

        if (club.getOwner().getId().equals(memberUserId)) {
            throw new BusinessRuleException("O proprietário do clube não pode ser removido");
        }

        ClubMember membership = clubMemberRepository
                .findByClubIdAndUserId(clubId, memberUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro do clube", memberUserId));

        clubMemberRepository.delete(membership);
    }

    public void assertMember(Long clubId, Long userId) {
        if (!clubMemberRepository.existsByClubIdAndUserId(clubId, userId)) {
            throw new ForbiddenOperationException("Você não pertence a este clube");
        }
    }

    public void assertAdmin(Long clubId, Long userId) {
        ClubMember membership = clubMemberRepository
                .findByClubIdAndUserId(clubId, userId)
                .orElseThrow(() -> new ForbiddenOperationException(
                        "Você não pertence a este clube"
                ));

        if (membership.getRole() != ClubMemberRole.ADMIN) {
            throw new ForbiddenOperationException("Apenas administradores podem executar esta ação");
        }
    }

    private Club findClubById(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Clube", clubId));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", userId));
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }

    @Transactional(readOnly = true)
    public boolean isAdmin(Long clubId, Long userId) {
        return clubMemberRepository.findByClubIdAndUserId(clubId, userId)
                .map(membership -> membership.getRole() == ClubMemberRole.ADMIN)
                .orElse(false);
    }
}