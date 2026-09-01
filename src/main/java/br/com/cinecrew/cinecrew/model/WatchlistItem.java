package br.com.cinecrew.cinecrew.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "watchlist_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_watchlist_user_movie",
                columnNames = {"user_id", "movie_id"}
        )
)
public class WatchlistItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "watchlist_items_seq")
    @SequenceGenerator(name = "watchlist_items_seq", sequenceName = "watchlist_items_seq", allocationSize = 50)
    @Column(nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
}