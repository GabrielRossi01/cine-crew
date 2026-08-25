package br.com.cinecrew.cinecrew.service;

import br.com.cinecrew.cinecrew.dto.response.RankingEntryResponse;
import br.com.cinecrew.cinecrew.exception.ResourceNotFoundException;
import br.com.cinecrew.cinecrew.mapper.ClubMemberMapper;
import br.com.cinecrew.cinecrew.model.ClubMember;
import br.com.cinecrew.cinecrew.repository.ClubMemberRepository;
import br.com.cinecrew.cinecrew.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final ClubMemberMapper clubMemberMapper;
    private final ClubService clubService;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "club-rankings", key = "#clubId")
    public List<RankingEntryResponse> getClubRanking(Long authenticatedUserId, Long clubId) {
        clubService.assertMember(clubId, authenticatedUserId);

        if (!clubRepository.existsById(clubId)) {
            throw new ResourceNotFoundException("Clube", clubId);
        }

        List<ClubMember> members = clubMemberRepository.findByClubIdOrderByClubScoreDesc(clubId);

        return IntStream.range(0, members.size())
                .mapToObj(index -> clubMemberMapper.toRankingEntry(
                        members.get(index),
                        index + 1
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public RankingEntryResponse getMyRanking(Long authenticatedUserId, Long clubId) {
        clubService.assertMember(clubId, authenticatedUserId);

        List<ClubMember> members = clubMemberRepository.findByClubIdOrderByClubScoreDesc(clubId);

        return IntStream.range(0, members.size())
                .filter(index -> members.get(index)
                        .getUser()
                        .getId()
                        .equals(authenticatedUserId))
                .mapToObj(index -> clubMemberMapper.toRankingEntry(
                        members.get(index),
                        index + 1
                ))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Posição no ranking",
                        authenticatedUserId
                ));
    }
}