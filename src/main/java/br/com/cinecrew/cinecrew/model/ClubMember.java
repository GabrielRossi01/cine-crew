package br.com.cinecrew.cinecrew.model;

import br.com.cinecrew.cinecrew.model.enums.ClubMemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "club_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_club_members_club_user",
                        columnNames = {"club_id", "user_id"}
                )
        }
)
public class ClubMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "club_members_seq")
    @SequenceGenerator(name = "club_members_seq", sequenceName = "users_seq", allocationSize = 50)
    @Column(nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClubMemberRole role;

    @Builder.Default
    @Column(name = "club_score", nullable = false)
    private Integer clubScore = 0;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;
}