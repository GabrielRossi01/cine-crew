package br.com.cinecrew.cinecrew.service;

import br.com.cinecrew.cinecrew.exception.ForbiddenOperationException;
import br.com.cinecrew.cinecrew.model.ClubMember;
import br.com.cinecrew.cinecrew.model.enums.ClubMemberRole;
import br.com.cinecrew.cinecrew.repository.ClubMemberRepository;
import br.com.cinecrew.cinecrew.repository.ClubRepository;
import br.com.cinecrew.cinecrew.repository.UserRepository;
import br.com.cinecrew.cinecrew.mapper.ClubMapper;
import br.com.cinecrew.cinecrew.mapper.ClubMemberMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClubServiceTest {

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private ClubMemberRepository clubMemberRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClubMapper clubMapper;

    @Mock
    private ClubMemberMapper clubMemberMapper;

    private ClubService clubService;

    @BeforeEach
    void setUp() {
        clubService = new ClubService(
                clubRepository,
                clubMemberRepository,
                userRepository,
                clubMapper,
                clubMemberMapper
        );
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForMembroDoClube() {
        Long clubId = 1L;
        Long userId = 2L;

        when(clubMemberRepository.existsByClubIdAndUserId(clubId, userId)).thenReturn(false);

        assertThatThrownBy(() -> clubService.assertMember(clubId, userId))
                .isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForAdminDoClube() {
        Long clubId = 1L;
        Long userId = 2L;

        ClubMember membership = ClubMember.builder()
                .role(ClubMemberRole.MEMBER)
                .build();

        when(clubMemberRepository.findByClubIdAndUserId(clubId, userId))
                .thenReturn(Optional.of(membership));

        assertThatThrownBy(() -> clubService.assertAdmin(clubId, userId))
                .isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void deveRetornarTrueQuandoIsAdminForConsultadoParaUmAdministrador() {
        Long clubId = 1L;
        Long userId = 2L;

        ClubMember membership = ClubMember.builder()
                .role(ClubMemberRole.ADMIN)
                .build();

        when(clubMemberRepository.findByClubIdAndUserId(clubId, userId))
                .thenReturn(Optional.of(membership));

        boolean result = clubService.isAdmin(clubId, userId);

        assertThat(result).isTrue();
    }
}