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
@Table(name = "posts")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posts_seq")
    @SequenceGenerator(name = "posts_seq", sequenceName = "posts_seq", allocationSize = 50)
    @Column(nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(length = 1_000)
    private String caption;
}