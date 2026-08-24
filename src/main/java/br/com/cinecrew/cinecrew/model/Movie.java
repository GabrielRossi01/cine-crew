package br.com.cinecrew.cinecrew.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movies")
public class Movie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movies_seq")
    @SequenceGenerator(name = "movies_seq", sequenceName = "users_seq", allocationSize = 50)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "tmdb_id", unique = true)
    private Long tmdbId;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Builder.Default
    @OneToMany(mappedBy = "movie")
    private List<Event> events = new ArrayList<>();
}