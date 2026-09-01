package br.com.cinecrew.cinecrew.mapper;

import br.com.cinecrew.cinecrew.dto.response.MovieSummaryResponse;
import br.com.cinecrew.cinecrew.dto.response.WatchlistItemResponse;
import br.com.cinecrew.cinecrew.model.WatchlistItem;
import org.springframework.stereotype.Component;

@Component
public class WatchlistItemMapper {

    public WatchlistItemResponse toDto(WatchlistItem entity) {
        return new WatchlistItemResponse(
                entity.getId(),
                toMovieSummaryDto(entity.getMovie()),
                entity.getCreatedAt()
        );
    }

    private MovieSummaryResponse toMovieSummaryDto(br.com.cinecrew.cinecrew.model.Movie movie) {
        return new MovieSummaryResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getPosterUrl(),
                movie.getReleaseYear()
        );
    }
}